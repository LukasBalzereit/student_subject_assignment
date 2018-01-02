package lb.ssa.ssa.security.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

public class CurrentUser  extends org.springframework.security.core.userdetails.User  {
    private String name;
    private Role role;

    public CurrentUser( String username,String password, Role role) {
        super(username,password, AuthorityUtils.createAuthorityList(role.toString()));
        this.name = username;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }
}
