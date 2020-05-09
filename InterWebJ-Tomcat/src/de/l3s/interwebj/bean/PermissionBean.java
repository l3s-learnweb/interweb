package de.l3s.interwebj.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import com.sun.istack.NotNull;

@ManagedBean
@RequestScoped
public class PermissionBean
{

    @NotNull
    private String level;
    private List<SelectItem> levels;

    public PermissionBean()
    {
	levels = new ArrayList<SelectItem>();
	levels.add(new SelectItem("read"));
	levels.add(new SelectItem("write"));
	levels.add(new SelectItem("delete"));
    }

    public String getLevelIndex()
    {
	return level;
    }

    public List<SelectItem> getLevels()
    {
	return levels;
    }

    public void setLevel(String level)
    {
	this.level = level;
    }
}
