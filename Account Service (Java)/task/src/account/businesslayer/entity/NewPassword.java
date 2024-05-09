package account.businesslayer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public class NewPassword {
    @JsonProperty("new_password")
    @Size(min = 12, message = "passwordValidateError")
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }
}
