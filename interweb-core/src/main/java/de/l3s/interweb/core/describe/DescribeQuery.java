package de.l3s.interweb.core.describe;

import de.l3s.interweb.core.Query;

public class DescribeQuery extends Query {
    private String id;
    private String link;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
