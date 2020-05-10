package de.l3s.interwebj;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/*
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
*/
public class AuthorizationInformation implements Serializable
{
    private static final long serialVersionUID = -9050656498265764056L;

    private List<ServiceInformation> services;

    protected AuthorizationInformation(InputStream xmlInputStream) throws IllegalResponseException
    {
        services = new ArrayList<AuthorizationInformation.ServiceInformation>();

        /*    
        Document document;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
              document = builder.parse( xmlInputStream );
        }
        catch(Exception e)
        {
            throw new IllegalResponseException(e.getMessage());
        }
        Element root = document.getDocumentElement();
        
        if(!root.getAttribute("stat").equals("ok"))
            throw new IllegalResponseException(root.toString());
        
        NodeList servicesEl = root.getElementsByTagName("service");
        
        for(int i=0; i<servicesEl.getLength(); i++)//.item(0).getElementsByTagName("service"))
        {
            Node service = servicesEl.item(i);
            ServiceInformation info = new ServiceInformation();
            info.setId(service.getAttribute("id"));
            info.setTitle(service.getElementsByTagName("title").item(0).getTextContent())
            /*;
            info.setMediaTypes(service.elementText("mediatypes"));
            info.setAuthorized(Boolean.parseBoolean(service.elementText("authorized")));
        
            if(info.getTitle().equals("BibSonomy"))
        	continue;
        
            if(info.isAuthorized())
            {
        	info.setUserIdAtService(service.elementText("serviceuserid"));
        	//info.setRevokeauthorization(service.element("revokeauthorization").elementText("link"));
            }
            else
            {
        	Element authorization = service.element("authorization");
        	info.setAuthorizationType(authorization.attributeValue("type"));
        
        	Map<String, String> params = new HashMap<String, String>();
        	if(info.getAuthorizationType().equals("login"))
        	{
        	    for(Element param : authorization.element("parameters").elements("parameter"))
        	    {
        		params.put(param.attributeValue("type"), param.getText());
        	    }
        	}
        	info.setAuthorization(params);
            }
            * /
            services.add(info);
        }
            /*****************************************************************************
        Document res;
        try
        {
           res = new SAXReader().read(xmlInputStream);
        }
        catch(DocumentException e)
        {
           throw new IllegalResponseException(e.getMessage());
        }
        Element root = res.getRootElement();
        
        if(!root.attribute("stat").getValue().equals("ok"))
           throw new IllegalResponseException(root.asXML());
        
        for(Element service : root.element("services").elements("service"))
        {
           ServiceInformation info = new ServiceInformation();
           info.setId(service.attributeValue("id"));
           info.setTitle(service.elementText("title"));
           info.setMediaTypes(service.elementText("mediatypes"));
           info.setAuthorized(Boolean.parseBoolean(service.elementText("authorized")));
        
           if(info.getTitle().equals("BibSonomy"))
        continue;
        
           if(info.isAuthorized())
           {
        info.setUserIdAtService(service.elementText("serviceuserid"));
        //info.setRevokeauthorization(service.element("revokeauthorization").elementText("link"));
           }
           else
           {
        Element authorization = service.element("authorization");
        info.setAuthorizationType(authorization.attributeValue("type"));
        
        Map<String, String> params = new HashMap<String, String>();
        if(info.getAuthorizationType().equals("login"))
        {
            for(Element param : authorization.element("parameters").elements("parameter"))
            {
        	params.put(param.attributeValue("type"), param.getText());
            }
        }
        info.setAuthorization(params);
           }
           services.add(info);
        }
        */
    }

    public List<ServiceInformation> getServices()
    {
        return services;
    }

    public class ServiceInformation implements Serializable
    {
        private static final long serialVersionUID = -4992768571552034936L;

        private String id;
        private String title;
        private boolean authorized;
        private String userIdAtService;
        private String revokeauthorization;
        private Map<String, String> authorization;
        private String authorizationType;
        private List<String> mediaTypes;

        private String key;
        private String secret;

        public String getKey()
        {
            return this.key;
        }

        public void setKey(String key)
        {
            this.key = key;
        }

        public String getSecret()
        {
            return this.secret;
        }

        public void setSecret(String secret)
        {
            this.secret = secret;
        }

        public String getId()
        {
            return id;
        }

        public void setMediaTypes(String mediaTypes)
        {
            if(null != mediaTypes)
                this.mediaTypes = Arrays.asList(mediaTypes.split(","));

        }

        public List<String> getMediaTypes()
        {
            return mediaTypes;
        }

        public void setMediaTypes(List<String> mediaTypes)
        {
            this.mediaTypes = mediaTypes;
        }

        public void setId(String serviceId)
        {
            this.id = serviceId;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String serviceTitle)
        {
            this.title = serviceTitle;
        }

        public boolean isAuthorized()
        {
            return authorized;
        }

        public void setAuthorized(boolean authorized)
        {
            this.authorized = authorized;
        }

        public String getUserIdAtService()
        {
            return userIdAtService;
        }

        public void setUserIdAtService(String userIdAtService)
        {
            this.userIdAtService = userIdAtService;
        }

        public String getRevokeauthorization()
        {
            return revokeauthorization;
        }

        public void setRevokeauthorization(String revokeauthorization)
        {
            this.revokeauthorization = revokeauthorization;
        }

        public Map<String, String> getAuthorization()
        {
            return authorization;
        }

        public void setAuthorization(Map<String, String> authorization)
        {
            this.authorization = authorization;
        }

        public String getAuthorizationType()
        {
            return authorizationType;
        }

        public void setAuthorizationType(String authorizationType)
        {
            this.authorizationType = authorizationType;
        }
    }

}
