package de.l3s.interwebj.bean;


import java.io.*;
import java.util.*;

import javax.faces.bean.*;
import javax.faces.model.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.core.ServiceConnector.PermissionLevel;
import de.l3s.interwebj.webutil.*;


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
		String callbackUrl = FacesUtils.getInterWebJBean().getBaseUrl()
		                     + "callback";
		Environment.logger.debug("callbackUrl: [" + callbackUrl + "]");
		Parameters params = ((ServiceConnector) connector).authenticate(permissionLevel,
		                                                                callbackUrl);
		String oauthAuthorizationUrl = params.get(Parameters.OAUTH_AUTHORIZATION_URL);
		if (oauthAuthorizationUrl != null)
		{
			Environment.logger.debug("redirecting to service authorization url: "
			                         + oauthAuthorizationUrl);
			SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
			sessionBean.addPendingAuthorizationConnector((ServiceConnector) connector,
			                                             params);
			try
			{
				FacesUtils.getExternalContext().redirect(oauthAuthorizationUrl);
			}
			catch (IOException e)
			{
				Environment.logger.error(e);
				throw new InterWebException(e);
			}
		}
		return null;
	}
	

	public String getPermission()
	{
		return permission;
	}
	

	public List<SelectItem> getPermissions()
	{
		return permissions;
	}
	

	public List<ServiceConnector> getRegisteredConnectors()
	    throws InterWebException
	{
		Engine engine = Environment.getInstance().getEngine();
		List<ServiceConnector> registeredConnectors = new ArrayList<ServiceConnector>();
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (connector.isRegistered())
			{
				registeredConnectors.add(connector);
			}
		}
		return registeredConnectors;
	}
	

	public boolean isUserAuthenticated(Object connector)
	    throws InterWebException
	{
		Engine engine = Environment.getInstance().getEngine();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		if (principal != null)
		{
			return engine.isUserAuthenticated((ServiceConnector) connector,
			                                  principal);
		}
		return false;
	}
	

	public String revoke(Object connector)
	    throws InterWebException
	{
		Environment.logger.debug("revoking user authentication");
		Engine engine = Environment.getInstance().getEngine();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		//		Environment.logger.debug("current user: " + principal.getName());
		engine.setUserAuthCredentials(((ServiceConnector) connector).getName(),
		                              principal,
		                              null);
		return null;
	}
	

	public void setPermission(String permission)
	{
		this.permission = permission;
	}
	
}
