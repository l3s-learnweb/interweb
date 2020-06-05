package de.l3s.interwebj.connector.ipernity;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1Builder;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import de.l3s.interwebj.connector.ipernity.jaxb.CheckTokenResponse;
import de.l3s.interwebj.connector.ipernity.jaxb.Doc;
import de.l3s.interwebj.connector.ipernity.jaxb.IpernityResponse;
import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.SearchRanking;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;

public class IpernityConnector extends ServiceConnector implements Cloneable {
    private static final Logger log = LogManager.getLogger(IpernityConnector.class);

    private static final String REQUEST_TOKEN_PATH = "http://www.ipernity.com/apps/oauth/request";
    private static final String AUTHORIZATION_PATH = "http://www.ipernity.com/apps/oauth/authorize";
    private static final String ACCESS_TOKEN_PATH = "http://www.ipernity.com/apps/oauth/access";
    private static final String IPERNITY_BASE = "http://api.ipernity.com/api/";

    public IpernityConnector() {
        super("Ipernity", "http://www.ipernity.com", ContentType.image);
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
    public ConnectorResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        notNull(query, "query");
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }
        ConnectorResults queryResult = new ConnectorResults(query, getName());

        if (!query.getContentTypes().contains(ContentType.image)
            && !query.getContentTypes().contains(ContentType.video)
            && !query.getContentTypes().contains(ContentType.audio)) {
            return queryResult;
        }

        if (query.getQuery().startsWith("user::")) {
            //String username = query.getQuery().substring(6).trim();

            return queryResult;
        }

        WebTarget resource = createWebTarget(IPERNITY_BASE + "doc.search/xml", getAuthCredentials(), null);

        resource = resource.queryParam("text", query.getQuery());
        resource = resource.queryParam("media", "photo"); // TODO media values are : photo, audio, video  query.getContentTypes()  Query.CT_AUDIO
        resource = resource.queryParam("page", Integer.toString(query.getPage()));
        resource = resource.queryParam("per_page", Integer.toString(query.getPerPage()));
        resource = resource.queryParam("sort", createSortOrder(query.getRanking()));
        resource = resource.queryParam("thumbsize", "2048");
        resource = resource.queryParam("extra", "medias,count"); //,dates,original

        if (query.getDateFrom() != null) {
            try {
                resource = resource.queryParam("created_min", query.getDateFrom());
            } catch (Exception e) {
                log.error(e);
            }
        }

        if (query.getDateTill() != null) {
            try {
                resource = resource.queryParam("created_max", query.getDateTill());
            } catch (Exception e) {
                log.error(e);
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
            resultItem.setCommentCount(doc.getCount().getComments());
            resultItem.setViewCount(doc.getCount().getVisits());

            // valid labels are: 75x, 100, 240, 250x, 500, 560, 640, 800, 1024, 1600 or 2048
            // original: 2048, 1600
            // large: 640, 800 or 1024
            // medium: 500, 560
            // small: 100, 240
            // we are trying to retrieve max, and then smaller values, the API always return the max value they have
            String thumbLabel = doc.getThumb().getLabel();
            String thumbUrl = doc.getThumb().getUrl();
            int thumWidth = doc.getThumb().getW();
            int thumHeight = doc.getThumb().getH();

            if (thumbLabel.equals("2048") || thumbLabel.equals("1600")) {
                resultItem.setThumbnailOriginal(new Thumbnail(thumbUrl, thumWidth, thumHeight));

                thumbUrl = thumbUrl.replace("." + thumbLabel + ".", ".1024.");
                thumbLabel = "1024";
                int[] newSize = CoreUtils.scaleThumbnail(thumWidth, thumHeight, 1024);
                thumWidth = newSize[0];
                thumHeight = newSize[1];
            }

            if (thumbLabel.equals("1024") || thumbLabel.equals("800") || thumbLabel.equals("640")) {
                resultItem.setThumbnailLarge(new Thumbnail(thumbUrl, thumWidth, thumHeight));

                thumbUrl = thumbUrl.replace("." + thumbLabel + ".", ".560.");
                thumbLabel = "560";
                int[] newSize = CoreUtils.scaleThumbnail(thumWidth, thumHeight, 560);
                thumWidth = newSize[0];
                thumHeight = newSize[1];
            }

            if (thumbLabel.equals("560") || thumbLabel.equals("500")) {
                resultItem.setThumbnailMedium(new Thumbnail(thumbUrl, thumWidth, thumHeight));

                thumbUrl = thumbUrl.replace("." + thumbLabel + ".", ".240.");
                thumbLabel = "240";
                int[] newSize = CoreUtils.scaleThumbnail(thumWidth, thumHeight, 240);
                thumWidth = newSize[0];
                thumHeight = newSize[1];
            }

            if (thumbLabel.equals("240") || thumbLabel.equals("100")) {
                resultItem.setThumbnailSmall(new Thumbnail(thumbUrl, thumWidth, thumHeight));
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
        log.info("auth cred" + authCredentials);

        final ConsumerCredentials consumerCredentials = new ConsumerCredentials(authCredentials.getKey(), authCredentials.getSecret());
        final OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport.builder(consumerCredentials)
            .authorizationFlow(REQUEST_TOKEN_PATH, ACCESS_TOKEN_PATH, AUTHORIZATION_PATH)
            .callbackUri(callbackUrl).build();

        final String authorizationUri = authFlow.start();
        log.info("requesting url: " + authorizationUri);

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
        log.info("oauth_token: " + oauthToken);
        String oauthTokenSecret = params.get(Parameters.OAUTH_TOKEN_SECRET);
        log.info("oauth_token_secret: " + oauthTokenSecret);
        String oauthVerifier = params.get(Parameters.OAUTH_VERIFIER);
        log.info("oauth_verifier: " + oauthVerifier);

        AuthCredentials authCredentials = getAuthCredentials();
        log.info("auth cred" + authCredentials);

        final ConsumerCredentials consumerCredentials = new ConsumerCredentials(authCredentials.getKey(), authCredentials.getSecret());
        final OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport.builder(consumerCredentials)
            .authorizationFlow(REQUEST_TOKEN_PATH, ACCESS_TOKEN_PATH, AUTHORIZATION_PATH).build();

        log.info("requesting access token");
        final AccessToken accessToken = authFlow.finish(oauthVerifier);

        log.info("ipernity response: " + accessToken);
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
    public boolean isConnectorRegistrationDataRequired() {
        return true;
    }

    @Override
    public boolean isUserRegistrationDataRequired() {
        return false;
    }

    @Override
    public boolean isUserRegistrationRequired() {
        return true;
    }

    private static String createSortOrder(SearchRanking searchRanking) {
        /*
         * Method to sort by: relevant, newest, oldest, most_played, most_commented, or most_liked.
         */
        switch (searchRanking) {
            case relevance:
                return "relevance";
            case date:
                return "posted-desc";
            case interestingness:
                return "popular";
            default:
                return "relevance";
        }
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
