package pl.darsonn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logs {
    private static final Logger logger = LoggerFactory.getLogger(Logs.class);

    public static void information(String message) {
        logger.info(message);
    }
    
    public static void error(String message) {
        logger.error(message);
    }
}
