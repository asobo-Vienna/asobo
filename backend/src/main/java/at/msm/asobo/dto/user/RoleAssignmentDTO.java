package at.msm.asobo.dto.user;

import java.util.Set;
import java.util.UUID;

public class RoleAssignmentDTO {
    private UUID userId;
    private Set<String> roles;

    RoleAssignmentDTO() {}

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
