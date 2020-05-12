package de.l3s.interwebj.webutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javax.el.ELResolver;
import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.l3s.interwebj.bean.InterWebJBean;
import de.l3s.interwebj.bean.SessionBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FacesUtils
{
	private static final Logger log = LogManager.getLogger(FacesUtils.class);

    private static abstract class FacesContextWrapper extends FacesContext
    {

	protected static void setCurrentInstance(FacesContext facesContext)
	{
	    FacesContext.setCurrentInstance(facesContext);
	}
    }

    public static void addGlobalMessage(FacesMessage.Severity severity, String message)
    {
	addGlobalMessage(severity, message, null);
    }

    public static void addGlobalMessage(FacesMessage.Severity severity, String message, String id)
    {
	FacesContext fc = FacesContext.getCurrentInstance();
	fc.addMessage(id, new FacesMessage(severity, message, null));
    }

    public static void addGlobalMessage(FacesMessage.Severity severity, Throwable e)
    {
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	log.error(e);
	String message = new String(os.toByteArray(), StandardCharsets.UTF_8);
	addGlobalMessage(severity, message);
    }

    public static String getContextPath()
    {
	HttpServletRequest request = (HttpServletRequest) FacesUtils.getExternalContext().getRequest();
	return request.getContextPath();
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

    public static FacesContext getFacesContext(HttpServletRequest request, HttpServletResponse response)
    {
	FacesContext facesContext = FacesContext.getCurrentInstance();
	if(facesContext == null)
	{
	    LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
	    Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	    FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
	    facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(), request, response, lifecycle);
	    UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
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
	catch(Exception e)
	{
	    log.error(e);
	}
	return null;
    }

    public static Object getManagedBean(String beanName)
    {
	FacesContext fc = FacesContext.getCurrentInstance();
	return (fc == null) ? null : getManagedBean(fc, beanName);
    }

    public static HttpServletRequest getRequest()
    {
	ExternalContext ec = getExternalContext();
	return (HttpServletRequest) ec.getRequest();
    }

    public static HttpServletResponse getResponse()
    {
	ExternalContext ec = getExternalContext();
	return (HttpServletResponse) ec.getResponse();
    }

    public static SessionBean getSessionBean()
    {
	return (SessionBean) getManagedBean("sessionBean");
    }

    public static SessionBean getSessionBean(FacesContext fc)
    {
	return (SessionBean) getManagedBean(fc, "sessionBean");
    }

    public static void redirect(String redirectPath) throws IOException
    {
	ExternalContext externalContext = getExternalContext();
	externalContext.redirect(redirectPath);
    }
}
