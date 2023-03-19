package de.l3s.interweb.tomcat.app;

import static de.l3s.interweb.core.util.Assertions.notEmpty;
import static de.l3s.interweb.core.util.Assertions.notNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.l3s.interweb.core.AuthCredentials;

public class InterWebPrincipal implements java.security.Principal, Serializable {
    @Serial
    private static final long serialVersionUID = 3491812866957391163L;

    public static final String DEFAULT_ROLE = "default";
    public static final String MANAGER_ROLE = "manager";

    private final String name;
    private final String email;
    private final Set<String> roles;
    private AuthCredentials oauthCredentials;

    public InterWebPrincipal(String name) {
        this(name, null);
    }

    public InterWebPrincipal(String name, String email) {
        this(name, email, new HashSet<>());
    }

    public InterWebPrincipal(String name, String email, Set<String> roles) {
        notEmpty(name, "name");
        notNull(roles, "roles");
        this.name = name;
        this.email = email;
        this.roles = new HashSet<>();
        for (String role : roles) {
            addRole(role);
        }
    }

    public void addRole(String role) {
        notEmpty(role, "role");
        roles.add(role.toLowerCase());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        InterWebPrincipal other = (InterWebPrincipal) obj;
        if (name == null) {
            return other.name == null;
        } else {
            return name.equals(other.name);
        }
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    public AuthCredentials getOauthCredentials() {
        return oauthCredentials;
    }

    public void setOauthCredentials(AuthCredentials oauthCredentials) {
        this.oauthCredentials = oauthCredentials;
    }

    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public boolean hasRole(String role) {
        if (role == null) {
            return false;
        }
        return roles.contains(role.toLowerCase());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("name", name)
            .append("email", email)
            .append("roles", roles)
            .append("oauthCredentials", oauthCredentials)
            .toString();
    }

    public static InterWebPrincipal createDefault(String name) {
        return createDefault(name, null);
    }

    public static InterWebPrincipal createDefault(String name, String email) {
        InterWebPrincipal principal = new InterWebPrincipal(name, email);
        principal.addRole(DEFAULT_ROLE);
        return principal;
    }
}
