package yaasl.server.model;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

@Entity
public class RememberMeToken  {

    private Long id;
    private String username;
    private String series;
    private String tokenValue;
    private Date date;
    private User user;

    public RememberMeToken() {
    }

    public RememberMeToken(PersistentRememberMeToken rememberMeToken, User user) {
        this.username = rememberMeToken.getUsername();
        this.date = rememberMeToken.getDate();
        this.series = rememberMeToken.getSeries();
        this.tokenValue = rememberMeToken.getTokenValue();
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy=AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne(fetch = LAZY)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
