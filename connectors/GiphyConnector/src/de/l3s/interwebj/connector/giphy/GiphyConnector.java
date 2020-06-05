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
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;

public class GiphyConnector extends ServiceConnector implements Cloneable {
    private static final Logger log = LogManager.getLogger(GiphyConnector.class);

    public GiphyConnector() {
        super("Giphy", "https://giphy.com", ContentType.image);
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
    public ConnectorResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        notNull(query, "query");

        if (authCredentials == null) {
            authCredentials = getAuthCredentials();
        }

        try {
            Giphy giphy = new Giphy(authCredentials.getSecret());

            SearchFeed feed = giphy.search(query.getQuery(), query.getPerPage(), (query.getPage() - 1) * query.getPerPage(), query.getLanguage());

            ConnectorResults results = new ConnectorResults(query, getName());

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

    @Override
    public boolean isConnectorRegistrationDataRequired() {
        return false;
    }

    @Override
    public boolean isUserRegistrationDataRequired() {
        return false;
    }

    @Override
    public boolean isUserRegistrationRequired() {
        return false;
    }

    private static Thumbnail createThumbnail(GiphyImage image) {
        return new Thumbnail(image.getUrl(), Integer.parseInt(image.getWidth()), Integer.parseInt(image.getHeight()));
    }
}
