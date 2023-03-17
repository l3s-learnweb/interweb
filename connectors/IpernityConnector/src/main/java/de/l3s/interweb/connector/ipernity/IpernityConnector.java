package de.l3s.interweb.connector.ipernity;

import static de.l3s.interweb.core.util.Assertions.notNull;

import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1Builder;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import com.google.auto.service.AutoService;

import de.l3s.interweb.connector.ipernity.jaxb.CheckTokenResponse;
import de.l3s.interweb.connector.ipernity.jaxb.Doc;
import de.l3s.interweb.connector.ipernity.jaxb.IpernityResponse;
import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.Parameters;
import de.l3s.interweb.core.connector.ConnectorSearchResults;
import de.l3s.interweb.core.connector.ServiceConnector;
import de.l3s.interweb.core.query.ContentType;
import de.l3s.interweb.core.query.Query;
import de.l3s.interweb.core.query.ResultItem;
import de.l3s.interweb.core.query.SearchRanking;
import de.l3s.interweb.core.query.SearchScope;
import de.l3s.interweb.core.query.Thumbnail;

/**
 * Ipernity is a non-commercial photo sharing community which is financed exclusively by membership dues without any intention of making a profit.
 * TODO missing search implementations: extras, language.
 *
 * @see <a href="http://www.ipernity.com/help/api/method/doc.search">Ipernity Search API</a>
 */
@AutoService(ServiceConnector.class)
public class IpernityConnector extends ServiceConnector {
    private static final Logger log = LogManager.getLogger(IpernityConnector.class);

    private static final String REQUEST_TOKEN_PATH = "http://www.ipernity.com/apps/oauth/request";
    private static final String AUTHORIZATION_PATH = "http://www.ipernity.com/apps/oauth/authorize";
    private static final String ACCESS_TOKEN_PATH = "http://www.ipernity.com/apps/oauth/access";
    private static final String IPERNITY_BASE = "http://api.ipernity.com/api/";

    public IpernityConnector() {
        super("Ipernity", "http://www.ipernity.com/", ContentType.image);
    }

    public IpernityConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public ServiceConnector clone() {
        return new IpernityConnector(getAuthCredentials());
    }

    /**
     * API Docs:
     * http://www.ipernity.com/help/api/method/doc.search
     */
    @Override
    public ConnectorSearchResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        notNull(query, "query");
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }

        ConnectorSearchResults queryResult = new ConnectorSearchResults(query, getName());
        if (!query.getContentTypes().contains(ContentType.image)) {
            return queryResult;
        }

        if (query.getQuery().startsWith("user::")) {
            //String username = query.getQuery().substring(6).trim();
            return queryResult;
        }

        WebTarget resource = createWebTarget(IPERNITY_BASE + "doc.search/xml", getAuthCredentials(), null);

        if (query.getSearchScope() == SearchScope.text) {
            resource = resource.queryParam("text", query.getQuery());
        } else if (query.getSearchScope() == SearchScope.tags) {
            resource = resource.queryParam("tags", query.getQuery());
        }

        resource = resource.queryParam("media", "photo");
        resource = resource.queryParam("page", Integer.toString(query.getPage()));
        resource = resource.queryParam("per_page", Integer.toString(query.getPerPage()));
        resource = resource.queryParam("sort", convertRanking(query.getRanking()));
        resource = resource.queryParam("thumbsize", "1024"); // 75x, 100, 240, 250x, 500, 560, 640, 800, 1024, 1600 or 2048
        resource = resource.queryParam("share", "4"); // 4 - only public docs
        resource = resource.queryParam("extra", "count,original"); // owner, dates, count, license, medias, geo, original

        if (query.getDateFrom() != null) {
            try {
                resource = resource.queryParam("created_min", query.getDateFrom());
            } catch (Exception e) {
                log.error("Error parsing from date", e);
            }
        }

        if (query.getDateTill() != null) {
            try {
                resource = resource.queryParam("created_max", query.getDateTill());
            } catch (Exception e) {
                log.error("Error parsing to date", e);
            }
        }

        IpernityResponse sr = resource.request().get(IpernityResponse.class);

        long totalResultCount = sr.getDocs().getTotal();
        queryResult.setTotalResultCount(totalResultCount);
        int count = (sr.getDocs().getPage() - 1) * sr.getDocs().getPerPage();

        List<Doc> docs = sr.getDocs().getDoc();
        for (Doc doc : docs) {
            ResultItem resultItem = new ResultItem(getName(), count++);
            resultItem.setType(ContentType.image);
            resultItem.setId(Integer.toString(doc.getDocId()));
            resultItem.setTitle(doc.getTitle());
            resultItem.setUrl("http://ipernity.com/doc/" + doc.getOwner().getUserId() + "/" + doc.getDocId());
            resultItem.setCommentsCount(doc.getCount().getComments());
            resultItem.setViewsCount(doc.getCount().getVisits());

            if (doc.getThumb() != null) {
                resultItem.setThumbnailLarge(new Thumbnail(doc.getThumb().getUrl(), doc.getThumb().getW(), doc.getThumb().getH()));
                resultItem.setWidth(doc.getThumb().getW());
                resultItem.setHeight(doc.getThumb().getH());
            }

            if (doc.getOriginal() != null) {
                resultItem.setThumbnailOriginal(new Thumbnail(doc.getOriginal().getUrl(), doc.getOriginal().getW(), doc.getOriginal().getH()));
                resultItem.setWidth(doc.getOriginal().getW());
                resultItem.setHeight(doc.getOriginal().getH());
            }

            queryResult.addResultItem(resultItem);
        }

        return queryResult;
    }

    @Override
    public Parameters authenticate(String callbackUrl, Parameters parameters) throws InterWebException {
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }

        AuthCredentials authCredentials = getAuthCredentials();
        log.info("auth cred{}", authCredentials);

        final ConsumerCredentials consumerCredentials = new ConsumerCredentials(authCredentials.getKey(), authCredentials.getSecret());
        final OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport.builder(consumerCredentials)
            .authorizationFlow(REQUEST_TOKEN_PATH, ACCESS_TOKEN_PATH, AUTHORIZATION_PATH)
            .callbackUri(callbackUrl).build();

        final String authorizationUri = authFlow.start();
        log.info("requesting url: {}", authorizationUri);

        Parameters params = new Parameters();
        params.add(Parameters.AUTHORIZATION_URL, authorizationUri);
        return params;
    }

    @Override
    public AuthCredentials completeAuthentication(Parameters params) throws InterWebException {
        notNull(params, "params");

        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }

        String oauthToken = params.get(Parameters.OAUTH_TOKEN);
        log.info("oauth_token: {}", oauthToken);
        String oauthTokenSecret = params.get(Parameters.OAUTH_TOKEN_SECRET);
        log.info("oauth_token_secret: {}", oauthTokenSecret);
        String oauthVerifier = params.get(Parameters.OAUTH_VERIFIER);
        log.info("oauth_verifier: {}", oauthVerifier);

        AuthCredentials authCredentials = getAuthCredentials();
        log.info("auth cred{}", authCredentials);

        final ConsumerCredentials consumerCredentials = new ConsumerCredentials(authCredentials.getKey(), authCredentials.getSecret());
        final OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport.builder(consumerCredentials)
            .authorizationFlow(REQUEST_TOKEN_PATH, ACCESS_TOKEN_PATH, AUTHORIZATION_PATH).build();

        log.info("requesting access token");
        final AccessToken accessToken = authFlow.finish(oauthVerifier);

        log.info("ipernity response: {}", accessToken);
        params.add(Parameters.OAUTH_TOKEN, accessToken.getToken());
        params.add(Parameters.OAUTH_TOKEN_SECRET, accessToken.getAccessTokenSecret());

        return new AuthCredentials(accessToken.getToken(), accessToken.getAccessTokenSecret());
    }

    @Override
    public String getUserId(AuthCredentials authCredentials) {
        WebTarget target = createWebTarget(IPERNITY_BASE + "auth.checkToken/xml", getAuthCredentials(), authCredentials);
        CheckTokenResponse response = target.request().get(CheckTokenResponse.class);
        return response.getAuth().getUser().getUsername();
    }

    @Override
    public boolean isUserRegistrationRequired() {
        return true;
    }

    /**
     * Supported values are: relevance, popular, posted-desc, posted-asc.
     */
    private static String convertRanking(SearchRanking ranking) {
        return switch (ranking) {
            case date -> "posted-desc";
            case interestingness -> "popular";
            default -> "relevance";
        };
    }

    private static WebTarget createWebTarget(String apiUrl, AuthCredentials consumerAuthCredentials, AuthCredentials userAuthCredentials) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(apiUrl);

        ConsumerCredentials consumerCredentials = new ConsumerCredentials(consumerAuthCredentials.getKey(), consumerAuthCredentials.getSecret());

        OAuth1Builder.FilterFeatureBuilder filterFeature = OAuth1ClientSupport.builder(consumerCredentials).feature();

        if (userAuthCredentials != null) {
            AccessToken storedToken = new AccessToken(userAuthCredentials.getKey(), userAuthCredentials.getSecret());
            filterFeature.accessToken(storedToken);
        }
        target.register(filterFeature.build());

        return target;
    }
}
