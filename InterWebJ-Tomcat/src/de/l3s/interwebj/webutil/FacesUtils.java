package de.l3s.interwebj.webutil;


import javax.el.*;
import javax.faces.*;
import javax.faces.application.*;
import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.lifecycle.*;
import javax.servlet.http.*;

import de.l3s.interwebj.bean.*;
import de.l3s.interwebj.core.*;


public class FacesUtils
{
	
	private static abstract class FacesContextWrapper
	    extends FacesContext
	{
		
		protected static void setCurrentInstance(FacesContext facesContext)
		{
			FacesContext.setCurrentInstance(facesContext);
		}
	}
	

	public static void addGlobalMessage(FacesMessage.Severity severity,
	                                    String message)
	{
		addGlobalMessage(severity, message, null);
	}
	

	public static void addGlobalMessage(FacesMessage.Severity severity,
	                                    String message,
	                                    String id)
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(id, new FacesMessage(severity, message, null));
	}
	

	public static ExternalContext getExternalContext()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		return getExternalContext(fc);
	}
	

	public static ExternalContext getExternalContext(FacesContext fc)
	{
		return fc.getExternalContext();
	}
	

	public static FacesContext getFacesContext(HttpServletRequest request,
	                                           HttpServletResponse response)
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null)
		{
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
			FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(),
			                                              request,
			                                              response,
			                                              lifecycle);
			UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext,
			                                                                            "");
			facesContext.setViewRoot(view);
			FacesContextWrapper.setCurrentInstance(facesContext);
		}
		return facesContext;
	}
	

	public static InterWebJBean getInterWebJBean()
	{
		return (InterWebJBean) getManagedBean("interwebj");
	}
	

	public static Object getManagedBean(FacesContext fc, String beanName)
	{
		ELResolver elResolver = fc.getApplication().getELResolver();
		try
		{
			return elResolver.getValue(fc.getELContext(), null, beanName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
		}
		return null;
	}
	

	public static Object getManagedBean(String beanName)
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		return (fc == null)
		    ? null : getManagedBean(fc, beanName);
	}
	

	public static PrincipalBean getPrincipalBean()
	{
		return (PrincipalBean) getManagedBean("principalBean");
	}
	

	public static PrincipalBean getPrincipalBean(FacesContext fc)
	{
		return (PrincipalBean) getManagedBean(fc, "principalBean");
	}
}
