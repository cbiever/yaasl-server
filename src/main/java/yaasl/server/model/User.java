package yaasl.server.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class User implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private boolean nonExpired;
    private boolean nonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
    private boolean isMD5;

    public User() {
        nonExpired = true;
        nonLocked = true;
        credentialsNonExpired = true;
        enabled = true;
        isMD5 = false;
    }

    @Id
    @GeneratedValue(strategy=AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return nonExpired;
    }

    public void setAccountNonExpired(boolean nonExpired) {
        this.nonExpired = nonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return nonLocked;
    }

    public void setAccountNonLocked(boolean nonLocked) {
        this.nonLocked = nonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    @Transient // Fail to process type argument in a generic declaration
    public Collection<? extends GrantedAuthority> getAuthorities() {
            return grantedAuthorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = new HashSet<GrantedAuthority>(grantedAuthorities);
    }

    public void addAuthority(GrantedAuthority authority) {
        this.grantedAuthorities.add(authority);
    }

    @ManyToMany(targetEntity = Authority.class)
    @JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    public Set<Authority> getAuthorities2() {
        Set<Authority> authorities = new HashSet<Authority>();
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            authorities.add((Authority) grantedAuthority);
        }
        return authorities;
    }

    public void setAuthorities2(Set<Authority> authorities) {
        grantedAuthorities.clear();
        for (Authority authority : authorities) {
            grantedAuthorities.add(authority);
        }
    }

    public boolean isMD5() {
        return isMD5;
    }

    public void setMD5(boolean MD5) {
        isMD5 = MD5;
    }

}
