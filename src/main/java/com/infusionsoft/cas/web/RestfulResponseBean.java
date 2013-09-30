package com.infusionsoft.cas.web;

import com.infusionsoft.cas.api.domain.PageMetaData;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

public class RestfulResponseBean<T> {

    public RestfulResponseBean(List<T> content, PageMetaData page){
        this.content = content;
        this.page = page;
    }

    private List<T> content;
    @JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
    private PageMetaData page;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public PageMetaData getPage() {
        return page;
    }

    public void setPage(PageMetaData page) {
        this.page = page;
    }
}
