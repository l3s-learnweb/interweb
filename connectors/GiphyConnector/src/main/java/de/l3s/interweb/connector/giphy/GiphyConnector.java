package de.l3s.interweb.connector.giphy;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.connector.giphy.entity.giphy.GiphyContainer;
import de.l3s.interweb.connector.giphy.entity.giphy.GiphyData;
import de.l3s.interweb.connector.giphy.entity.giphy.GiphyImage;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;

/**
 * Giphy, styled as GIPHY, is an American online database and search engine that allows users to search for
 * and share short looping videos with no sound, that resemble animated GIF files.
 *
 * @see <a href="https://developers.giphy.com/docs/api/endpoint#search">Giphy Search API</a>
 */
@Dependent
public class GiphyConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(GiphyConnector.class);
    private static final int fallbackPerPage = 150;

    @RestClient
    GiphySearchClient searchClient;

    @Override
    public String getName() {
        return "Giphy";
    }

    @Override
    public String getBaseUrl() {
        return "https://giphy.com/";
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.image};
    }

    @Override
    public Uni<SearchConnectorResults> search(SearchQuery query) throws ConnectorException {
        return searchClient.search(
                query.getQuery(),
                query.getPerPage(fallbackPerPage),
                query.getOffset(fallbackPerPage),
                query.getLanguage()
        ).map(feed -> {
            SearchConnectorResults results = new SearchConnectorResults();

            if (feed.getDataList() != null && !feed.getDataList().isEmpty()) {
                results.setTotalResults(feed.getPagination().getTotalCount());

                int rank = query.getOffset(fallbackPerPage);
                for (GiphyData image : feed.getDataList()) {
                    results.addResultItem(createSearchItem(image, ++rank));
                }
            }

            return results;
        });
    }

    private static SearchItem createSearchItem(GiphyData image, int rank) {
        SearchItem resultItem = new SearchItem(rank);
        resultItem.setType(ContentType.image);
        resultItem.setId(image.getId());
        resultItem.setTitle(image.getTitle());
        resultItem.setDate(DateUtils.parse(image.getImportDatetime()));
        resultItem.setUrl(image.getUrl());
        if (image.getUser() != null) {
            resultItem.setAuthor(image.getUser().getDisplayName());
            resultItem.setAuthorUrl(image.getUser().getProfileUrl());
        }

        GiphyContainer giphyThumbnails = image.getImages();

        if (giphyThumbnails.getFixedHeightSmallStill() != null && giphyThumbnails.getFixedHeightSmallStill().getUrl() != null) {
            resultItem.setThumbnailSmall(createThumbnail(giphyThumbnails.getFixedHeightSmallStill()));
        } else if (giphyThumbnails.getFixedHeightSmall() != null && giphyThumbnails.getFixedHeightSmall().getUrl() != null) {
            resultItem.setThumbnailSmall(createThumbnail(giphyThumbnails.getFixedHeightSmall()));
        }

        if (giphyThumbnails.getFixedHeightDownsampled() != null && giphyThumbnails.getFixedHeightDownsampled().getUrl() != null) {
            resultItem.setThumbnailMedium(createThumbnail(giphyThumbnails.getFixedHeightDownsampled()));
        } else if (giphyThumbnails.getFixedHeight() != null && giphyThumbnails.getFixedHeight().getUrl() != null) {
            resultItem.setThumbnailMedium(createThumbnail(giphyThumbnails.getFixedHeight()));
        }

        if (giphyThumbnails.getDownsizedMedium() != null && giphyThumbnails.getDownsizedMedium().getUrl() != null) {
            resultItem.setThumbnailLarge(createThumbnail(giphyThumbnails.getDownsizedMedium()));
        } else if (giphyThumbnails.getDownsizedLarge() != null && giphyThumbnails.getDownsizedLarge().getUrl() != null) {
            resultItem.setThumbnailLarge(createThumbnail(giphyThumbnails.getDownsizedLarge()));
        }

        if (giphyThumbnails.getOriginal() != null && giphyThumbnails.getOriginal().getUrl() != null) {
            resultItem.setThumbnailOriginal(createThumbnail(giphyThumbnails.getOriginal()));
            resultItem.setWidth(giphyThumbnails.getOriginal().getWidth());
            resultItem.setHeight(giphyThumbnails.getOriginal().getHeight());
        } else if (giphyThumbnails.getOriginalStill() != null && giphyThumbnails.getOriginalStill().getUrl() != null) {
            resultItem.setThumbnailOriginal(createThumbnail(giphyThumbnails.getOriginalStill()));
            resultItem.setWidth(giphyThumbnails.getOriginalStill().getWidth());
            resultItem.setHeight(giphyThumbnails.getOriginalStill().getHeight());
        }

        return resultItem;
    }

    private static Thumbnail createThumbnail(GiphyImage image) {
        return new Thumbnail(image.getUrl(), image.getWidth(), image.getHeight());
    }
}
