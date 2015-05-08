package production;

public class Logger {
    private static Logger instance;

    public void log(String s) {
        System.out.println(s);
    }

    private Logger() {
        log("New instance of logger created");
    }

    public static Logger getInstance() {
        if(instance == null) {
            instance = new Logger();
        }
        return instance;
    }
}
