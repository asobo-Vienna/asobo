package at.msm.asobo.dto.user;

import java.util.Set;
import java.util.UUID;

public class UserRolesDTO {
    private UUID userId;
    private Set<RoleDTO> roles;

    public UserRolesDTO() {}

    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Set<RoleDTO> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<RoleDTO> roles) {
        this.roles = roles;
    }
}
