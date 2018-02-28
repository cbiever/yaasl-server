package yaasl.server.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

import java.util.Set;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

@Entity
public class Authority implements GrantedAuthority {

    private Long id;
    private String authority;
    private Set<User> users;

    public Authority() {
    }

    public Authority(String authority) {
        this.authority = authority;
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
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @ManyToMany(fetch = LAZY)
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}
