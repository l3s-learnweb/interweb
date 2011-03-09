package de.l3s.interwebj.bean;


import java.io.*;
import java.net.*;
import java.util.*;

import javax.faces.bean.*;
import javax.faces.model.*;

import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.connector.ServiceConnector.PermissionLevel;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.util.*;


@ManagedBean
@RequestScoped
public class ServicesBean
{
	
	private String permission;
	private List<SelectItem> permissions;
	

	public ServicesBean()
	{
		permissions = new ArrayList<SelectItem>();
		permissions.add(new SelectItem("read"));
		permissions.add(new SelectItem("write"));
		permissions.add(new SelectItem("delete"));
	}
	

	public String authenticate(Object connector)
	    throws InterWebException
	{
		Environment.logger.debug("requested permission: " + permission);
		PermissionLevel permissionLevel = PermissionLevel.getPermissionLevel(permission);
		Environment.logger.debug("requested permission level fetched: "
		                         + permissionLevel.getName());
		URL requestTokenUrl = ((ServiceConnector) connector).authenticate(permissionLevel);
		Environment.logger.debug(requestTokenUrl.toExternalForm());
		SessionBean sessionBean = (SessionBean) Utils.getManagedBean("sessionBean");
		sessionBean.addAwaitingAuthenticationConnectors((ServiceConnector) connector);
		try
		{
			Utils.getExternalContext().redirect(requestTokenUrl.toExternalForm());
		}
		catch (IOException e)
		{
			Environment.logger.error(e);
			throw new InterWebException(e);
		}
		return null;
	}
	

	public List<ServiceConnector> getConnectors()
	    throws InterWebException
	{
		return Utils.getEngine().getConnectors();
	}
	

	public String getPermission()
	{
		return permission;
	}
	

	public List<SelectItem> getPermissions()
	{
		return permissions;
	}
	

	public boolean isConnectorRegistered(Object connector)
	    throws InterWebException
	{
		Engine engine = Utils.getEngine();
		return engine.isConnectorRegistered((ServiceConnector) connector);
	}
	

	public boolean isUserAuthenticated(Object connector)
	    throws InterWebException
	{
		Engine engine = Utils.getEngine();
		IWPrincipal principal = Utils.getPrincipalBean().getPrincipal();
		if (principal != null)
		{
			Environment.logger.debug("current user: " + principal.getName());
			return engine.isUserAuthenticated((ServiceConnector) connector,
			                                  principal);
		}
		return false;
	}
	

	public String revoke(Object connector)
	    throws InterWebException
	{
		Environment.logger.debug("revoking user authentication");
		Engine engine = Utils.getEngine();
		IWPrincipal principal = Utils.getPrincipalBean().getPrincipal();
		Environment.logger.debug("current user: " + principal.getName());
		engine.setUserAuthData((ServiceConnector) connector, principal, null);
		return null;
	}
	

	public void setPermission(String permission)
	{
		this.permission = permission;
	}
	
}
