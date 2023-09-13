package de.l3s.interweb.connector.slideshare;

import java.util.List;

import jakarta.enterprise.context.Dependent;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.l3s.interweb.connector.slideshare.entity.Slideshow;
import de.l3s.interweb.connector.slideshare.entity.Slideshows;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;
import de.l3s.interweb.core.util.StringUtils;

/**
 * LinkedIn SlideShare is an American hosting service for professional content including presentations, infographics, documents, and videos.
 * TODO missing search implementations: extras, date.
 *
 * @see <a href="https://www.slideshare.net/developers/documentation#search_slideshows">SlideShare Search API</a>
 */
@Dependent
public class SlideShareConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(SlideShareConnector.class);

    @ConfigProperty(name = "connector.slideshare.secret")
    String slideShareSecret;

    @RestClient
    SlideShareSearchClient searchClient;

    @Override
    public String getName() {
        return "SlideShare";
    }

    @Override
    public String getBaseUrl() {
        return "https://www.slideshare.net/";
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.text, ContentType.presentation, ContentType.video};
    }

    @Override
    public SearchConnectorResults search(SearchQuery query) throws ConnectorException {
        try {
            long timestamp = System.currentTimeMillis() / 1000;

            String responseBody = searchClient.search(
                    query.getQuery(),
                    query.getPage(),
                    query.getPerPage(),
                    SlideShareUtils.convertRanking(query.getRanking()),
                    query.getLanguage(),
                    SlideShareUtils.convertContentType(query.getContentTypes()),
                    null,

                    timestamp,
                    SlideShareUtils.hash(slideShareSecret + timestamp).toLowerCase()
            ).await().indefinitely();

            XmlMapper objectMapper = new XmlMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            Slideshows sr = objectMapper.readValue(responseBody, Slideshows.class);

            SearchConnectorResults queryResult = new SearchConnectorResults();
            queryResult.setTotalResults(sr.getMeta().getTotalResults());
            int count = sr.getMeta().getResultOffset() - 1;
            List<Slideshow> searchResults = sr.getSearchResults();
            if (searchResults == null) {
                return queryResult;
            }

            for (Slideshow sre : searchResults) {
                SearchItem resultItem = new SearchItem(count++);
                resultItem.setType(SlideShareUtils.createType(sre.getSlideshowType()));
                resultItem.setId(Integer.toString(sre.getId()));
                resultItem.setTitle(sre.getTitle());
                resultItem.setDescription(sre.getDescription());
                resultItem.setUrl(sre.getUrl());
                resultItem.setDate(DateUtils.parse(sre.getUpdated()));
                resultItem.setAuthor(sre.getUserName());
                resultItem.setAuthorUrl("https://www.slideshare.net/" + sre.getUserName());

                resultItem.setThumbnailLarge(SlideShareUtils.parseThumbnail(sre.getThumbnailXXLargeURL()));
                resultItem.setThumbnailMedium(SlideShareUtils.parseThumbnail(sre.getThumbnailXLargeURL()));
                resultItem.setThumbnailSmall(SlideShareUtils.parseThumbnail(sre.getThumbnailSmallURL()));

                resultItem.setEmbeddedUrl(StringUtils.parseSourceUrl(sre.getEmbed()));

                queryResult.addResultItem(resultItem);
            }
            return queryResult;
        } catch (Exception e) {
            throw new ConnectorException("Failed to retrieve results", e);
        }
    }
}
