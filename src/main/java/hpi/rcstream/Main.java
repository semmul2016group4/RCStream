package hpi.rcstream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by magnus on 19.04.16.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        logger.debug("Hello.");

        RCStream rc = new RCStream("en.wikipedia.org");
        rc.start();
    }
}
