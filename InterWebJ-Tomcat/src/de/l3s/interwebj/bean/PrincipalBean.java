package de.l3s.interwebj.bean;


import javax.faces.bean.*;

import de.l3s.interwebj.core.*;


@ManagedBean
@SessionScoped
public class PrincipalBean
{
	
	private IWPrincipal principal;
	

	public IWPrincipal getPrincipal()
	{
		return principal;
	}
	

	public boolean isLoggedIn()
	{
		return principal != null;
	}
	

	public String logout()
	{
		principal = null;
		return "/index.xhtml";
	}
	

	public void setPrincipal(IWPrincipal principal)
	{
		this.principal = principal;
	}
	
}
