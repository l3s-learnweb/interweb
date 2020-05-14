package de.l3s.interwebj.tomcat.bean;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.tomcat.webutil.FacesUtils;

import org.apache.catalina.util.RequestUtil;

@Named("interwebj")
@ApplicationScoped
public class InterWebJBean
{
    public String getBaseUrl()
    {
	HttpServletRequest req = (HttpServletRequest) FacesUtils.getExternalContext().getRequest();
	URI currentUri = URI.create(RequestUtil.getRequestURL(req).toString());
	URI baseUri = currentUri.resolve(getContextPath() + "/");
	return baseUri.toASCIIString();
    }

    public String getContextPath()
    {
	return FacesUtils.getContextPath();
    }

    public Environment getEnvironment()
    {
	return Environment.getInstance();
    }
}
