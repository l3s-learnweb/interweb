package de.l3s.interweb.connector.flickr.entity;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Photos{

    @JsonProperty("perpage")
    private Integer perPage;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("pages")
    private Integer pages;

    @JsonProperty("photo")
    private List<PhotoItem> photo;

    @JsonProperty("page")
    private Integer page;

    public void setPerPage(Integer perPage){
        this.perPage = perPage;
    }

    public Integer getPerPage(){
        return perPage;
    }

    public void setTotal(Integer total){
        this.total = total;
    }

    public Integer getTotal(){
        return total;
    }

    public void setPages(Integer pages){
        this.pages = pages;
    }

    public Integer getPages(){
        return pages;
    }

    public void setPhoto(List<PhotoItem> photo){
        this.photo = photo;
    }

    public List<PhotoItem> getPhoto(){
        return photo;
    }

    public void setPage(Integer page){
        this.page = page;
    }

    public Integer getPage(){
        return page;
    }
}