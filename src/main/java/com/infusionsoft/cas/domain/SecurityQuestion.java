package com.infusionsoft.cas.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "SecurityQuestion")
@Table(name = "security_question", uniqueConstraints = {@UniqueConstraint(columnNames = {"question"})})
public class SecurityQuestion implements Serializable{

    private Long id;
    private String question;
    private String iconPath;
    private boolean enabled = true;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Column
    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @Column
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
