package hpi.rcstream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.util.parsing.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by magnus on 19.04.16.
 */
public class RCStream implements IOCallback {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private SocketIO socket;
    private String room;
    private ObjectMapper mapper;

    public RCStream(String room) {
        this.room = room;
        this.mapper = new ObjectMapper();

        try {
            socket = new SocketIO("http://stream.wikimedia.org/rc");
        } catch (MalformedURLException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
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
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public void onError(SocketIOException e) {
        logger.error(e.getLocalizedMessage(), e);
    }

}
