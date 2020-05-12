package de.l3s.interwebj.bean;

import java.net.URI;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUtils;

import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.webutil.FacesUtils;

@ManagedBean(name = "interwebj")
@ApplicationScoped
public class InterWebJBean
{
    @SuppressWarnings("deprecation")
    public String getBaseUrl()
    {
	HttpServletRequest req = (HttpServletRequest) FacesUtils.getExternalContext().getRequest();
	URI currentUri = URI.create(HttpUtils.getRequestURL(req).toString());
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
