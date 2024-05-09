package account.businesslayer.exception;

public class CustomException extends RuntimeException {
    private final String path;

    public CustomException(String message, String path) {
        super(message);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
