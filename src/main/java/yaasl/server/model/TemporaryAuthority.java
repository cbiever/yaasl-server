package yaasl.server.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

@Entity
public class TemporaryAuthority {

    private Long id;
    private Authority authority;
    private Date date;
    private User user;

    public TemporaryAuthority() {
    }

    public TemporaryAuthority(Date date, User user, Authority authority) {
        this.date = date;
        this.user = user;
        this.authority = authority;
    }

    @Id
    @GeneratedValue(strategy = AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.DATE)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne
    @JoinColumn(name="authority_id")
    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    @ManyToOne
    @JoinColumn(name="user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
