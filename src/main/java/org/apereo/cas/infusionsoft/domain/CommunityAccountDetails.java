package org.apereo.cas.infusionsoft.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Extra info about a Community account. This has a one-way reference to UserAccount.
 */
@Entity
@Table(name = "community_account_details", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_account_id"})})
public class CommunityAccountDetails implements Serializable {
    private Long id;
    private UserAccount userAccount;
    private String displayName;
    private String timeZone = "-7";
    private String twitterHandle;
    private String notificationEmailAddress;
    private Integer infusionsoftExperience = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne(targetEntity = UserAccount.class, optional = false)
    @JoinColumn(name = "user_account_id", unique = true, nullable = false, updatable = false)
    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Column(name = "display_name", length = 30)
    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Column(name = "time_zone", length = 30)
    @NotNull
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Column(name = "twitter_handle", length = 30)
    public String getTwitterHandle() {
        return twitterHandle;
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }

    @Column(name = "notification_email_address", length = 255)
    public String getNotificationEmailAddress() {
        return notificationEmailAddress;
    }

    public void setNotificationEmailAddress(String notificationEmailAddress) {
        this.notificationEmailAddress = notificationEmailAddress;
    }

    @Column(name = "infusionsoft_experience")
    public Integer getInfusionsoftExperience() {
        return infusionsoftExperience;
    }

    public void setInfusionsoftExperience(Integer infusionsoftExperience) {
        this.infusionsoftExperience = infusionsoftExperience;
    }
}
