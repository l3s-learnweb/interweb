package de.l3s.interwebj.tomcat.bean;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import com.sun.istack.NotNull;

@Named
@RequestScoped
public class PermissionBean {

    @NotNull
    private String level;
    private final List<SelectItem> levels;

    public PermissionBean() {
        levels = new ArrayList<>();
        levels.add(new SelectItem("read"));
        levels.add(new SelectItem("write"));
        levels.add(new SelectItem("delete"));
    }

    public String getLevelIndex() {
        return level;
    }

    public List<SelectItem> getLevels() {
        return levels;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
