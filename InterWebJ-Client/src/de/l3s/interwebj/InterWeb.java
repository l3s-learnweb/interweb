package de.l3s.interwebj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import de.l3s.interwebj.AuthorizationInformation.ServiceInformation;
import de.l3s.interwebj.jaxb.SearchResponse;
import de.l3s.interwebj.jaxb.SearchResultEntity;

public class InterWeb implements Serializable
{
    private static final Logger log = Logger.getLogger(InterWeb.class);
    private static final long serialVersionUID = -1621494088505203391L;

    private final String consumerKey;
    private final String consumerSecret;
    private final String interwebApiURL;

    private AuthCredentials iwToken = null;
    
    public InterWeb(String interwebApiURL, String consumerKey, String consumerSecret)
    {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.interwebApiURL = interwebApiURL;
    }

    public String getConsumerKey()
    {
        return consumerKey;
    }

    public String getConsumerSecret()
    {
        return consumerSecret;
    }

    public String getInterwebApiURL()
    {
        return interwebApiURL;
    }

    public AuthCredentials getIWToken()
    {
        return iwToken;
    }

    public void setIWToken(AuthCredentials iwToken)
    {
        this.iwToken = iwToken;
    }

    private WebResource createWebResource(String apiPath, AuthCredentials userAuthCredentials)
    {
        String apiUrl = getInterwebApiURL() + apiPath;
        AuthCredentials consumerAuthCredentials = new AuthCredentials(getConsumerKey(), getConsumerSecret());

        Client client = Client.create();
        WebResource resource = client.resource(apiUrl);
        OAuthParameters oauthParams = new OAuthParameters();
        oauthParams.consumerKey(consumerAuthCredentials.getKey());
        if(userAuthCredentials != null)
        {
            oauthParams.token(userAuthCredentials.getKey());
        }
        oauthParams.signatureMethod(HMAC_SHA1.NAME);
        oauthParams.timestamp();
        oauthParams.nonce();
        oauthParams.version();
        OAuthSecrets oauthSecrets = new OAuthSecrets();
        oauthSecrets.consumerSecret(consumerAuthCredentials.getSecret());
        if(userAuthCredentials != null && userAuthCredentials.getSecret() != null)
        {
            oauthSecrets.tokenSecret(userAuthCredentials.getSecret());
        }
        OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), oauthParams, oauthSecrets);
        resource.addFilter(filter);

        return resource;
    }

    private WebResource createPublicWebResource(String apiPath)
    {
        return createWebResource(apiPath, null);
    }

    public synchronized List<ServiceInformation> getServiceInformation(boolean useCache) throws IllegalResponseException
    {
            WebResource resource = createPublicWebResource("services");

            ClientResponse response = resource.get(ClientResponse.class);
            AuthorizationInformation temp = new AuthorizationInformation(response.getEntityInputStream());

            return temp.getServices();
    }

    public void deleteToken()
    {
        setIWToken(null);
    }

    public static String getClientResponseContent(ClientResponse response) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        InputStream is = response.getEntityInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        int c;
        while((c = br.read()) != -1)
        {
            sb.append((char) c);
        }
        br.close();
        return sb.toString();
    }


    /**
     * 
     * @param query The query string
     * @param params 
     * @return
     * @throws IOException
     * @throws IllegalResponseException
     */
    public SearchResponse search(String query, TreeMap<String, String> params) throws IOException, IllegalResponseException
    {
        if(null == query || query.length() == 0)
        {
            throw new IllegalArgumentException("empty query");
        }

        WebResource resource = createWebResource("search", getIWToken());
        resource = resource.queryParam("q", query);
        for(String key : params.keySet())
        {
            String value = params.get(key);
            resource = resource.queryParam(key, value);
        }

        ClientResponse response = resource.get(ClientResponse.class);

        if(response.getStatus() != 200)
        {
            String content = getClientResponseContent(response);

            log.fatal("Interweb request failes; Error code : " + response.getStatus() + "; for query:" + query + " | " + params + "; response: " + content);
            throw new RuntimeException("Interweb request failed : HTTP error code : " + response.getStatus());
        }

        return response.getEntity(SearchResponse.class);
    }

    public static void main(String[] args) throws Exception
    {
        TreeMap<String, String> params = new TreeMap<String, String>();

        params.put("media_types", "text"); // ,image
        params.put("services", "Bing"); // "YouTube,Vimeo"
        params.put("number_of_results", "10");
        params.put("page", "1");
        params.put("language", "de");
        
        InterWeb iw = new InterWeb("***REMOVED***/api/", "***REMOVED***", "***REMOVED***");

        SearchResponse response = iw.search("london", params);
        for(SearchResultEntity result :  response.getQuery().getResults())
        {
            System.out.println(result.getTitle());
            System.out.println(result.getUrl());
        }
     

    }

   
}
