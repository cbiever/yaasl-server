package yaasl.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class CostSharing {

    private Long id;
    private String description;
    private String i18n;

    public CostSharing() {
    }

    public CostSharing(String description, String i18n) {
        this.description = description;
        this.i18n = i18n;
    }

    @Id
    @GeneratedValue(strategy=AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getI18n() {
        return i18n;
    }

    public void setI18n(String i18n) {
        this.i18n = i18n;
    }

}
