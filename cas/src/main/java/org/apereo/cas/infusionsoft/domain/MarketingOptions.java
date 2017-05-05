package org.apereo.cas.infusionsoft.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "MarketingOptions")
@Table(name = "marketingOptions")
public class MarketingOptions implements Serializable {
    private Long id;
    private String href;
    private String mobileImageSrcUrl;
    private String desktopImageSrcUrl;
    private boolean enableAds = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "href", length = 2000)
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Column(name = "mobileImageSrcUrl", length = 2000)
    public String getMobileImageSrcUrl() {
        return mobileImageSrcUrl;
    }

    public void setMobileImageSrcUrl(String mobileImageSrcUrl) {
        this.mobileImageSrcUrl = mobileImageSrcUrl;
    }

    @Column(name = "desktopImageSrcUrl", length = 2000)
    public String getDesktopImageSrcUrl() {
        return desktopImageSrcUrl;
    }

    public void setDesktopImageSrcUrl(String desktopImageSrcUrl) {
        this.desktopImageSrcUrl = desktopImageSrcUrl;
    }

    @Column(name = "enableAds", nullable = false)
    public boolean getEnableAds() {
        return enableAds;
    }

    public void setEnableAds(boolean enableAds) {
        this.enableAds = enableAds;
    }
}
