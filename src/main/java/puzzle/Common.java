package puzzle;

import java.util.logging.Logger;

public class Common {

    static String LOG_CONFIG_KEY = "java.util.logging.config.file";
    static String LOG_CONFIG = "logging.properties";
    static String LOG_FORMAT_KEY = "java.util.logging.SimpleFormatter.format";
    static String LOG_FORMAT = "%1$tFT%1$tT.%1$tL %4$s %3$s %5$s %6$s%n";
    static {
            System.setProperty(LOG_FORMAT_KEY, LOG_FORMAT);
    }

    public static Logger getLogger(Class<?> clas) {
        return Logger.getLogger(clas.getSimpleName());
    }

    public static String methodName() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        return elements[2].getMethodName();
    }

}
