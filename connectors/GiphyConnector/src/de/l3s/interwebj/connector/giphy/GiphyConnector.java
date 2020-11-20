package de.l3s.interwebj.connector.giphy;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.trievosoftware.giphy4j.Giphy;
import com.trievosoftware.giphy4j.entity.giphy.GiphyContainer;
import com.trievosoftware.giphy4j.entity.giphy.GiphyData;
import com.trievosoftware.giphy4j.entity.giphy.GiphyImage;
import com.trievosoftware.giphy4j.entity.search.SearchFeed;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.connector.ConnectorSearchResults;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;

/**
 * Giphy, styled as GIPHY, is an American online database and search engine that allows users to search for
 * and share short looping videos with no sound, that resemble animated GIF files.
 * TODO missing search implementations: extras, search_in, date, ranking.
 *
 * @see <a href="https://developers.giphy.com/docs/api/endpoint#search">Giphy Search API</a>
 */
public class GiphyConnector extends ServiceConnector {
    private static final Logger log = LogManager.getLogger(GiphyConnector.class);

    public GiphyConnector() {
        super("Giphy", "https://giphy.com/", ContentType.image);
    }

    public GiphyConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public ServiceConnector clone() {
        return new GiphyConnector(getAuthCredentials());
    }

    @Override
    public ConnectorSearchResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        notNull(query, "query");

        if (authCredentials == null) {
            authCredentials = getAuthCredentials();
        }

        try {
            Giphy giphy = new Giphy(authCredentials.getSecret());

            SearchFeed feed = giphy.search(query.getQuery(), query.getPerPage(), (query.getPage() - 1) * query.getPerPage(), query.getLanguage());

            ConnectorSearchResults results = new ConnectorSearchResults(query, getName());

            if (feed.getDataList() != null && !feed.getDataList().isEmpty()) {
                results.setTotalResultCount(feed.getPagination().getTotalCount());

                int index = 1;
                for (GiphyData image : feed.getDataList()) {
                    ResultItem resultItem = new ResultItem(getName(), index++);
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

                    if (giphyThumbnails.getFixedHeightSmallStill() != null) {
                        resultItem.setThumbnailSmall(createThumbnail(giphyThumbnails.getFixedHeightSmallStill()));
                    } else if (giphyThumbnails.getFixedHeightSmall() != null) {
                        resultItem.setThumbnailSmall(createThumbnail(giphyThumbnails.getFixedHeightSmall()));
                    }

                    if (giphyThumbnails.getFixedHeightDownsampled() != null) {
                        resultItem.setThumbnailMedium(createThumbnail(giphyThumbnails.getFixedHeightDownsampled()));
                    } else if (giphyThumbnails.getFixedHeight() != null) {
                        resultItem.setThumbnailMedium(createThumbnail(giphyThumbnails.getFixedHeight()));
                    }

                    if (giphyThumbnails.getDownsizedMedium() != null) {
                        resultItem.setThumbnailLarge(createThumbnail(giphyThumbnails.getDownsizedMedium()));
                    } else if (giphyThumbnails.getDownsizedLarge() != null) {
                        resultItem.setThumbnailLarge(createThumbnail(giphyThumbnails.getDownsizedLarge()));
                    }

                    if (giphyThumbnails.getOriginal() != null) {
                        resultItem.setThumbnailOriginal(createThumbnail(giphyThumbnails.getOriginal()));
                    } else if (giphyThumbnails.getOriginalStill() != null) {
                        resultItem.setThumbnailOriginal(createThumbnail(giphyThumbnails.getOriginalStill()));
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
        return new Thumbnail(image.getUrl(), Integer.parseInt(image.getWidth()), Integer.parseInt(image.getHeight()));
    }
}
