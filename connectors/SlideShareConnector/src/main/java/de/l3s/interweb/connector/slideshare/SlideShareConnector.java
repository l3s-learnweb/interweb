package de.l3s.interweb.connector.slideshare;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
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
 *
 * @see <a href="https://www.slideshare.net/developers/documentation#search_slideshows">SlideShare Search API</a>
 */
@Dependent
public class SlideShareConnector implements SearchConnector {
    private static final int fallbackPerPage = 50;

    @ConfigProperty(name = "connector.slideshare.secret")
    Optional<String> slideShareSecret;

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
        return new ContentType[]{ContentType.webpage, ContentType.presentation, ContentType.video};
    }

    @Override
    public Uni<SearchConnectorResults> search(SearchQuery query) throws ConnectorException {
        long timestamp = System.currentTimeMillis() / 1000;

        if (slideShareSecret.isEmpty()) {
            throw new ConnectorException("SlideShare secret is not configured");
        }

        return searchClient.search(
            query.getQuery(),
            query.getPage(),
            query.getPerPage(fallbackPerPage),
            SlideShareUtils.convertSort(query.getSort()),
            query.getLanguage(),
            SlideShareUtils.convertContentType(query.getContentTypes()),
            null,

            timestamp,
            SlideShareUtils.hash(slideShareSecret.get() + timestamp)
        ).map(Unchecked.function(body -> {
            try {
                XmlMapper objectMapper = new XmlMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                Slideshows slideshows = objectMapper.readValue(body, Slideshows.class);
                if (slideshows.getErrorMessage() != null) {
                    throw new ConnectorException(slideshows.getErrorMessage().getMessage());
                }
                return slideshows;
            } catch (JsonProcessingException e) {
                throw new ConnectorException("Failed to parse response", e);
            }
        })).map(slideshows -> {
            SearchConnectorResults queryResult = new SearchConnectorResults();
            queryResult.setTotalResults(slideshows.getMeta().getTotalResults());
            List<Slideshow> searchResults = slideshows.getSearchResults();
            if (searchResults == null) {
                return queryResult;
            }

            int rank = slideshows.getMeta().getResultOffset();
            for (Slideshow sre : searchResults) {
                queryResult.addResultItem(createSearchItem(sre, ++rank));
            }
            return queryResult;
        });
    }

    private static SearchItem createSearchItem(Slideshow sre, int rank) {
        SearchItem resultItem = new SearchItem(rank);
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
        resultItem.setEmbedUrl(StringUtils.parseSourceUrl(sre.getEmbed()));

        return resultItem;
    }
}
