package de.l3s.interwebj.bean;


import java.io.*;
import java.util.*;

import javax.faces.bean.*;
import javax.servlet.http.*;
import javax.ws.rs.core.*;

import org.apache.commons.lang.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.util.*;
import de.l3s.interwebj.webutil.*;


@ManagedBean
@RequestScoped
public class ConsumersBean
    implements Serializable
{
	
	private static final long serialVersionUID = -2226768983834482837L;
	
	private String name;
	private String url;
	private String description;
	private AuthCredentials accessToken;
	private String oauthToken;
	private String callback;
	

	public ConsumersBean()
	{
		HttpServletRequest request = FacesUtils.getRequest();
		oauthToken = request.getParameter("oauth_token");
		callback = request.getParameter("oauth_callback");
		Engine engine = Environment.getInstance().getEngine();
		ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
		accessToken = (AuthCredentials) expirableMap.get("access_token:"
		                                                 + oauthToken);
	}
	

	public String addConsumer()
	    throws IOException
	{
		Database database = Environment.getInstance().getDatabase();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		AuthCredentials authCredentials = RandomGenerator.getInstance().nextOAuthCredentials();
		Consumer consumer = new Consumer(name,
		                                 url,
		                                 description,
		                                 authCredentials);
		database.saveConsumer(principal.getName(), consumer);
		Environment.logger.debug("consumer: [" + consumer.getName()
		                         + "] successfully added");
		String contextPath = FacesUtils.getContextPath();
		FacesUtils.redirect(contextPath + "/view/consumers.xhtml");
		return null;
	}
	

	public String authorizeConsumer()
	    throws IOException
	{
		HttpServletRequest request = FacesUtils.getRequest();
		String requestToken = request.getParameter("oauth_token");
		Engine engine = Environment.getInstance().getEngine();
		ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
		AuthCredentials authCredentials = (AuthCredentials) expirableMap.get("request_token:"
		                                                                     + requestToken);
		String consumerKey = (String) expirableMap.get("consumer_token:"
		                                               + requestToken);
		accessToken = new AuthCredentials(RandomGenerator.getInstance().nextOAuthToken(),
		                                  authCredentials.getSecret());
		expirableMap.remove("request_token:" + requestToken);
		expirableMap.remove("consumer_token:" + requestToken);
		expirableMap.put("access_token:" + accessToken.getKey(), accessToken);
		expirableMap.put("consumer_token:" + accessToken.getKey(), consumerKey);
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		principal.setOauthCredentials(accessToken);
		expirableMap.put("principal:" + accessToken.getKey(), principal);
		oauthToken = accessToken.getKey();
		if (!StringUtils.isEmpty(callback))
		{
			Environment.logger.debug("callback: [" + callback + "]");
			UriBuilder builder = UriBuilder.fromUri(callback);
			builder = builder.queryParam("oauth_token", accessToken.getKey());
			builder = builder.queryParam("oauth_token_secret",
			                             accessToken.getSecret());
			String callbackUrl = builder.build().toASCIIString();
			Environment.logger.debug("redirecting to callback URL: "
			                         + callbackUrl);
			FacesUtils.redirect(callbackUrl);
		}
		else
		{
			UriBuilder builder = UriBuilder.fromUri(request.getRequestURI());
			builder = builder.queryParam("oauth_token", oauthToken);
			builder = (callback == null)
			    ? builder : builder.queryParam("oauth_callback", callback);
			builder = builder.queryParam("registered", "true");
			String url = builder.build().toASCIIString();
			Environment.logger.debug("forwarding to URL: " + url);
			FacesUtils.redirect(url);
		}
		return null;
	}
	

	public AuthCredentials getAccessToken()
	{
		return accessToken;
	}
	

	public Consumer getAuthorizationConsumer()
	{
		Engine engine = Environment.getInstance().getEngine();
		ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
		String consumerKey = (String) expirableMap.get("consumer_token:"
		                                               + oauthToken);
		Database database = Environment.getInstance().getDatabase();
		Consumer consumer = database.readConsumerByKey(consumerKey);
		return consumer;
	}
	

	public String getCallback()
	{
		return callback;
	}
	

	public String getDescription()
	{
		return description;
	}
	

	public String getName()
	{
		return name;
	}
	

	public String getOauthToken()
	{
		return oauthToken;
	}
	

	public List<Consumer> getRegisteredConsumers()
	    throws InterWebException
	{
		Environment environment = Environment.getInstance();
		Database database = environment.getDatabase();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		return database.readConsumers(principal.getName());
	}
	

	public String getUrl()
	{
		return url;
	}
	

	public boolean isRegistered()
	{
		HttpServletRequest request = FacesUtils.getRequest();
		return Boolean.parseBoolean(request.getParameter("registered"));
	}
	

	public String newConsumer()
	    throws IOException
	{
		String contextPath = FacesUtils.getContextPath();
		FacesUtils.redirect(contextPath + "/view/add_consumer.xhtml");
		return null;
	}
	

	public String revoke(Object consumer)
	    throws InterWebException
	{
		String consumerName = ((Consumer) consumer).getName();
		Environment.logger.debug("revoking consumer [" + consumerName + "]");
		Environment environment = Environment.getInstance();
		Database database = environment.getDatabase();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		database.deleteConsumer(principal.getName(), consumerName);
		return null;
	}
	

	public void setDescription(String consumerDescription)
	{
		description = consumerDescription;
	}
	

	public void setName(String consumerName)
	{
		name = consumerName;
	}
	

	public void setUrl(String consumerUrl)
	{
		url = consumerUrl;
	}
}
