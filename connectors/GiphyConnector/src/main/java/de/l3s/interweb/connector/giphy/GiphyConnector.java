package de.l3s.interweb.connector.giphy;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

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
        resultItem.setThumbnailSmall(createThumbnail(giphyThumbnails.getFixedHeightSmallStill(), giphyThumbnails.getFixedHeightSmall()));
        resultItem.setThumbnailMedium(createThumbnail(giphyThumbnails.getFixedHeightDownsampled(), giphyThumbnails.getFixedHeight()));
        resultItem.setThumbnailLarge(createThumbnail(giphyThumbnails.getDownsizedMedium(), giphyThumbnails.getDownsizedLarge()));
        resultItem.setThumbnailOriginal(createThumbnail(giphyThumbnails.getOriginal(), giphyThumbnails.getOriginalStill()));

        if (resultItem.getLargestThumbnail() != null) {
            resultItem.setWidth(resultItem.getLargestThumbnail().getWidth());
            resultItem.setHeight(resultItem.getLargestThumbnail().getHeight());
        }

        return resultItem;
    }

    private static Thumbnail createThumbnail(GiphyImage first, GiphyImage second) {
        if (first != null && first.getUrl() != null) {
            return new Thumbnail(first.getUrl(), first.getWidth(), first.getHeight());
        } else if (second != null && second.getUrl() != null) {
            return new Thumbnail(second.getUrl(), second.getWidth(), second.getHeight());
        }
        return null;
    }
}
