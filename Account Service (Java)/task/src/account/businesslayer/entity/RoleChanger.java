package account.businesslayer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

public class RoleChanger {
    @NotBlank
    private String user;
    @NotBlank
    private String role;
    @Enumerated(EnumType.STRING)
    @JsonProperty("operation")
    private GrantRemoveOption operation;

    public String getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }

    public GrantRemoveOption getOption() {
        return operation;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setOption(GrantRemoveOption operation) {
        this.operation = operation;
    }
}
