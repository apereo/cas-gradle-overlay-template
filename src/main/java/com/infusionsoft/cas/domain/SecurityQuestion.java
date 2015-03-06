package com.infusionsoft.cas.domain;

import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    @NotNull
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
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
    @NotNull
    @Index(name = "enabled_index", columnNames = { "enabled" })
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
