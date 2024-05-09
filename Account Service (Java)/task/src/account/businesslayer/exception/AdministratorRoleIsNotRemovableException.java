package account.businesslayer.exception;

public class AdministratorRoleIsNotRemovableException extends CustomException{
    public AdministratorRoleIsNotRemovableException(String message, String path) {
        super(message, path);
    }
}
