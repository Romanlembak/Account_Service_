package account.businesslayer.exception;

public class CombineAdministrativeAndBusinessRolesException extends CustomException {
    public CombineAdministrativeAndBusinessRolesException(String message, String path) {
        super(message, path);
    }
}
