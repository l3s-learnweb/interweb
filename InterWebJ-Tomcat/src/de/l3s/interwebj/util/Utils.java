package de.l3s.interwebj.util;


import java.net.*;

import javax.el.*;
import javax.faces.application.*;
import javax.faces.context.*;

import de.l3s.interwebj.bean.*;
import de.l3s.interwebj.core.*;


public class Utils
{
	
	public static void addGlobalMessage(FacesMessage.Severity severity,
	                                    String message)
	{
		addGlobalMessage(severity, message, null);
	}
	

	public static void addGlobalMessage(FacesMessage.Severity severity,
	                                    String message,
	                                    String id)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(id, new FacesMessage(severity, message, null));
	}
	

	public static IWEnvironment getEnvironment()
	    throws MalformedURLException
	{
		return getInterWebJBean().getEnvironment();
	}
	

	public static InterWebJBean getInterWebJBean()
	{
		return (InterWebJBean) getManagedBean("interwebj");
	}
	

	public static Object getManagedBean(String beanName)
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ELResolver elResolver = fc.getApplication().getELResolver();
		return elResolver.getValue(fc.getELContext(), null, beanName);
	}
	

	public static PrincipalBean getPrincipalBean()
	{
		return (PrincipalBean) getManagedBean("principalBean");
	}
	
}
