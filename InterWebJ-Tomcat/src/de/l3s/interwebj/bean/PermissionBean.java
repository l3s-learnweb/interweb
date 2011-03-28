package de.l3s.interwebj.bean;


import java.util.*;

import javax.faces.bean.*;
import javax.faces.model.*;

import com.sun.istack.internal.*;


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
