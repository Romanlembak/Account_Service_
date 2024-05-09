package account.businesslayer.exception;

public class IdenticalPasswordExceptions extends CustomException{
    public IdenticalPasswordExceptions(String message, String path) {
        super(message, path);
    }
}
