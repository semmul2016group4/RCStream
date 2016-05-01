package hpi.rcstream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * Created by magnus on 19.04.16.
 */
public class RCStream implements IOCallback {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private SocketIO socket;
    private String room;
    private ObjectMapper mapper;

    private Connection connection;

    public RCStream(String room) {
        this.room = room;
        this.mapper = new ObjectMapper();

        try {
            socket = new SocketIO("http://stream.wikimedia.org/rc");
            //Class.forName("com.mysql.jdbc.driver");
            String host = System.getenv("host"); // including port and dbname
            String username = System.getenv("mysqluser");
            String password = System.getenv("mysqlpw");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host, username, password);
            assert (connection != null);
        } catch (MalformedURLException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    void start() {
        socket.connect(this);
    }

    public void onDisconnect() {
        logger.info("Server disconnected.");
    }

    public void onConnect() {
        logger.info("Server connected.");
        socket.emit("subscribe", room);
    }

    public void onMessage(String s, IOAcknowledge ioAcknowledge) {
        logger.debug("Message: " + s);
    }

    public void onMessage(JsonElement jsonElement, IOAcknowledge ioAcknowledge) {
        logger.debug("Message: " + jsonElement.getAsString());
    }

    public void on(String s, IOAcknowledge ioAcknowledge, JsonElement... jsonElements) {
        logger.debug(s);
        logger.debug(jsonElements[0].toString());
        try {
            RCFeedEntry entry = mapper.readValue(jsonElements[0].toString(), RCFeedEntry.class);
            logger.info(entry.toString());
            save(entry);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }

    }

    private void save(RCFeedEntry entry) {
        try {
            saveFullInformationSet(entry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveFullInformationSet(RCFeedEntry entry) throws SQLException {
        PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO SEMMUL.RCSTREAMFULL(ID, TYPE, NAMESPACE, TITLE, " +
                        "COMMENT, TIMESTAMP, USER, BOT, SERVER_URL, SERVER_NAME, " +
                        "SERVER_SCRIPT_PATH, WIKI, MINOR, PATROLLED, LENGTH_OLD," +
                        "LENGTH_NEW, REVISION_OLD, REVISION_NEW, LOG_ID, LOG_TYPE, " +
                        "LOG_ACTION, LOG_PARAMS, LOG_ACTION_COMMENT)" +
                        "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
        );
        insert.setString(1, String.valueOf(entry.id));
        insert.setString(2, entry.type);
        insert.setInt(3, entry.namespace);
        insert.setString(4, entry.title);
        insert.setString(5, entry.comment);
        insert.setLong(6, entry.timestamp);
        insert.setString(7, entry.user);
        insert.setBoolean(8, entry.bot);
        insert.setString(9, entry.server_url);
        insert.setString(10, entry.server_name);
        insert.setString(11, entry.server_script_path);
        insert.setString(12, entry.wiki);
        setPrimitive(insert, 13, entry.minor, Types.BOOLEAN);
        setPrimitive(insert, 14, entry.patrolled, Types.BOOLEAN);
        if (entry.length != null) {
            HashMap<String, Integer> length = (HashMap<String, Integer>) entry.length;
            setPrimitive(insert, 15, length.get("old"), Types.INTEGER);
            insert.setInt(16, length.get("new"));
        } else {
            insert.setNull(15, Types.INTEGER);
            insert.setNull(16, Types.INTEGER);
        }
        if (entry.revision != null) {
            HashMap<String, Integer> revision = (HashMap<String, Integer>) entry.revision;
            setPrimitive(insert, 17, revision.get("old"), Types.INTEGER);
            insert.setInt(18, revision.get("new"));
        } else {
            insert.setNull(17, Types.INTEGER);
            insert.setNull(18, Types.INTEGER);
        }
        insert.setInt(19, entry.log_id);
        insert.setString(20, entry.log_type);
        insert.setString(21, entry.log_action);
        insert.setString(22, entry.log_params != null ? entry.log_params.toString() : null);
        insert.setString(23, entry.log_action_comment);
        insert.execute();
    }

    private void setPrimitive(PreparedStatement stmt, int position, Object value, int sqlType) throws SQLException {
        if (value != null) {
            switch (sqlType) {
                case Types.BOOLEAN: {
                    stmt.setBoolean(position, (Boolean) value);
                    break;
                }
                case Types.INTEGER: {
                    stmt.setInt(position, (Integer) value);
                    break;
                }
            }
        } else {
            stmt.setNull(position, sqlType);
        }
    }


    public void onError(SocketIOException e) {
        logger.error(e.getLocalizedMessage(), e);
    }

}
