package account.businesslayer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Role implements Comparable<Role>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String name;
    @JsonIgnore
    private boolean businessRole;
    @ManyToMany(mappedBy = "roles", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Account> accounts;

    public Role() {
    }

    public Role(long id, String name, boolean businessRole) {
        this.id = id;
        this.name = name;
        this.businessRole = businessRole;
    }

    public Role(String name, boolean businessRole) {
        this.name = name;
        this.businessRole = businessRole;
    }

    public boolean isBusinessRole() {
        return businessRole;
    }

    public void setBusinessRole(boolean businessRole) {
        this.businessRole = businessRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Role o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return businessRole == role.businessRole && Objects.equals(id, role.id) && Objects.equals(name, role.name) && Objects.equals(accounts, role.accounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, businessRole, accounts);
    }
}
