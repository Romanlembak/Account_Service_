package account.businesslayer.exception;

public class PaymentNotExistException extends CustomException{
    public PaymentNotExistException(String message, String path) {
        super(message, path);
    }
}
