package de.l3s.interwebj.util;


import javax.el.*;
import javax.faces.*;
import javax.faces.application.*;
import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.lifecycle.*;
import javax.servlet.http.*;

import de.l3s.interwebj.bean.*;
import de.l3s.interwebj.core.*;


public class Utils
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
	

	public static Engine getEngine()
	    throws InterWebException
	{
		return getEnvironment().getEngine();
	}
	

	public static Environment getEnvironment()
	    throws InterWebException
	{
		return getInterWebJBean().getEnvironment();
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
		// Get current FacesContext.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// Check current FacesContext.
		if (facesContext == null)
		{
			// Create new Lifecycle.
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
			// Create new FacesContext.
			FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(),
			                                              request,
			                                              response,
			                                              lifecycle);
			// Create new View.
			UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext,
			                                                                            "");
			facesContext.setViewRoot(view);
			// Set current FacesContext.
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
		return elResolver.getValue(fc.getELContext(), null, beanName);
	}
	

	public static Object getManagedBean(String beanName)
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		return getManagedBean(fc, beanName);
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
