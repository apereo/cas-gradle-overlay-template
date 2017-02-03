package com.infusionsoft.cas.web.controllers.commands;

public class UserRoleSearchForm {
    private String authority;
    private Integer page;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
