package de.l3s.interwebj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import de.l3s.interwebj.jaxb.SearchResponse;

public class InterWeb implements Serializable
{
    private static final Logger log = LogManager.getLogger(InterWeb.class);
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

    public void deleteToken()
    {
        setIWToken(null);
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


    /**
     * @param query  The query string
     * @param params see http://athena.l3s.uni-hannover.de:8000/doc/search
     * @return
     * @throws IOException
     * @throws IllegalResponseException
     */
    public SearchResponse search(String query, TreeMap<String, String> params) throws IOException, IllegalResponseException
    {
        if(null == query || query.isEmpty())
        {
            throw new IllegalArgumentException("empty query");
        }

        WebResource resource = createWebResource("search", getIWToken());
        resource = resource.queryParam("q", query);
        for(final Map.Entry<String, String> entry : params.entrySet())
        {
            String value = entry.getValue();
            resource = resource.queryParam(entry.getKey(), value);
        }

        ClientResponse response = resource.get(ClientResponse.class);

        if(response.getStatus() != 200)
        {
            String content = responseToString(response);

            log.fatal("Interweb request failed; Error code : " + response.getStatus() + "; for query:" + query + " | " + params + "; response: " + content);
            throw new RuntimeException("Interweb request failed : HTTP error code : " + response.getStatus());
        }

        return response.getEntity(SearchResponse.class);
    }

    public static String responseToString(ClientResponse response)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            InputStream is = response.getEntityInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            int c;
            while((c = br.read()) != -1)
            {
                sb.append((char) c);
            }
            br.close();

            return sb.toString();
        }
        catch(IOException e)
        {
            log.warn("Can't convert response to String", e);
            return null;
        }
    }
}
