package com.infusionsoft.cas.domain;

import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "SecurityQuestionResponse")
@Table(name = "security_question_response", uniqueConstraints = {@UniqueConstraint(name = "security_question_user_unique", columnNames = {"security_question_id", "user_id"})})
public class SecurityQuestionResponse implements Serializable{

    private Long id;
    private SecurityQuestion securityQuestion;
    private User user;
    private String response;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JoinColumn(name = "security_question_id", nullable = false, updatable = false)
    @ManyToOne( targetEntity = SecurityQuestion.class, fetch = FetchType.EAGER)
    @NotNull
    public SecurityQuestion getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(SecurityQuestion securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @NotNull
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column
    @NotNull
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
