package de.l3s.interweb.connector.giphy;

import static de.l3s.interweb.core.util.Assertions.notNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.auto.service.AutoService;
import com.trievosoftware.giphy4j.Giphy;
import com.trievosoftware.giphy4j.entity.giphy.GiphyContainer;
import com.trievosoftware.giphy4j.entity.giphy.GiphyData;
import com.trievosoftware.giphy4j.entity.giphy.GiphyImage;
import com.trievosoftware.giphy4j.entity.search.SearchFeed;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.search.SearchResults;
import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.core.query.ContentType;
import de.l3s.interweb.core.query.Query;
import de.l3s.interweb.core.search.SearchItem;
import de.l3s.interweb.core.query.Thumbnail;

/**
 * Giphy, styled as GIPHY, is an American online database and search engine that allows users to search for
 * and share short looping videos with no sound, that resemble animated GIF files.
 * TODO missing search implementations: extras, search_in, date, ranking.
 *
 * @see <a href="https://developers.giphy.com/docs/api/endpoint#search">Giphy Search API</a>
 */
@AutoService(SearchProvider.class)
public class GiphyConnector extends SearchProvider {
    private static final Logger log = LogManager.getLogger(GiphyConnector.class);

    public GiphyConnector() {
        super("Giphy", "https://giphy.com/", ContentType.image);
    }

    public GiphyConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public SearchProvider clone() {
        return new GiphyConnector(getAuthCredentials());
    }

    @Override
    public SearchResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        notNull(query, "query");

        if (authCredentials == null) {
            authCredentials = getAuthCredentials();
        }

        try {
            Giphy giphy = new Giphy(authCredentials.getSecret());

            SearchFeed feed = giphy.search(query.getQuery(), query.getPerPage(), (query.getPage() - 1) * query.getPerPage(), query.getLanguage());

            SearchResults results = new SearchResults(query, getName());

            if (feed.getDataList() != null && !feed.getDataList().isEmpty()) {
                results.setTotalResults(feed.getPagination().getTotalCount());

                int index = 1;
                for (GiphyData image : feed.getDataList()) {
                    SearchItem resultItem = new SearchItem(getName(), index++);
                    resultItem.setType(ContentType.image);
                    resultItem.setId(image.getId());
                    resultItem.setTitle(image.getTitle());
                    resultItem.setDate(image.getImportDatetime());
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
                        resultItem.setWidth(Integer.parseInt(giphyThumbnails.getOriginal().getWidth()));
                        resultItem.setHeight(Integer.parseInt(giphyThumbnails.getOriginal().getHeight()));
                    } else if (giphyThumbnails.getOriginalStill() != null && giphyThumbnails.getOriginalStill().getUrl() != null) {
                        resultItem.setThumbnailOriginal(createThumbnail(giphyThumbnails.getOriginalStill()));
                        resultItem.setWidth(Integer.parseInt(giphyThumbnails.getOriginalStill().getWidth()));
                        resultItem.setHeight(Integer.parseInt(giphyThumbnails.getOriginalStill().getHeight()));
                    }

                    results.addResultItem(resultItem);
                }
            }

            return results;
        } catch (Exception e) {
            throw new InterWebException(e);
        }
    }

    private static Thumbnail createThumbnail(GiphyImage image) {
        try {
            return new Thumbnail(image.getUrl(), Integer.parseInt(image.getWidth()), Integer.parseInt(image.getHeight()));
        } catch (Exception e) {
            log.error("Failed to parse numbers in {}", image, e);
            return null;
        }
    }
}
