package de.l3s.interwebj.connector.giphy;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.trievosoftware.giphy4j.Giphy;
import com.trievosoftware.giphy4j.entity.giphy.GiphyContainer;
import com.trievosoftware.giphy4j.entity.giphy.GiphyData;
import com.trievosoftware.giphy4j.entity.giphy.GiphyImage;
import com.trievosoftware.giphy4j.entity.giphy.GiphyOriginal;
import com.trievosoftware.giphy4j.entity.search.SearchFeed;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.AbstractServiceConnector;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;

public class GiphyConnector extends AbstractServiceConnector implements Cloneable {
    private static final Logger log = LogManager.getLogger(GiphyConnector.class);

    public GiphyConnector() {
        super("Giphy", "https://giphy.com", new TreeSet<>(Collections.singletonList("image")));
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
            authCredentials = getAuthCredentials(); // we have to use this; authCredentials parameter is null
        }

        try {
            Giphy giphy = new Giphy(authCredentials.getSecret());

            SearchFeed feed = giphy.search(query.getQuery(), query.getResultCount(), (query.getPage() - 1) * query.getResultCount(), query.getLanguage());

            ConnectorResults results = new ConnectorResults(query, getName());

            if (feed.getDataList() != null && !feed.getDataList().isEmpty()) {
                results.setTotalResultCount(feed.getPagination().getTotalCount());

                int index = 1;
                for (GiphyData image : feed.getDataList()) {
                    ResultItem resultItem = new ResultItem(getName());
                    resultItem.setType(Query.CT_IMAGE);
                    resultItem.setId(image.getId());
                    resultItem.setTitle(image.getTitle());
                    resultItem.setDate(image.getImportDatetime());
                    resultItem.setUrl(image.getUrl());
                    resultItem.setRank(index++);
                    resultItem.setTotalResultCount(results.getTotalResultCount());

                    GiphyContainer giphyThumbnails = image.getImages();

                    Set<Thumbnail> thumbnails = new LinkedHashSet<>();

                    try {
                        GiphyImage smallThumbnail = giphyThumbnails.getDownsized();
                        Thumbnail thumbnail = new Thumbnail(smallThumbnail.getUrl(), Integer.parseInt(smallThumbnail.getWidth()), Integer.parseInt(smallThumbnail.getHeight()));

                        if (thumbnail.getUrl() != null && thumbnail.getWidth() > 0 && thumbnail.getHeight() > 0) {
                            thumbnails.add(thumbnail);
                            resultItem.setEmbeddedSize1(createEmbedded(thumbnail));
                        }
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }

                    try {
                        GiphyImage mediumThumbnail = giphyThumbnails.getDownsizedMedium();
                        Thumbnail thumbnail = new Thumbnail(mediumThumbnail.getUrl(), Integer.parseInt(mediumThumbnail.getWidth()), Integer.parseInt(mediumThumbnail.getHeight()));

                        if (thumbnail.getUrl() != null && thumbnail.getWidth() > 0 && thumbnail.getHeight() > 0) {
                            thumbnails.add(thumbnail);
                            resultItem.setEmbeddedSize2(createEmbedded(thumbnail));
                        }
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }

                    try {
                        GiphyImage largeThumbnail = giphyThumbnails.getDownsizedLarge();
                        Thumbnail thumbnail = new Thumbnail(largeThumbnail.getUrl(), Integer.parseInt(largeThumbnail.getWidth()), Integer.parseInt(largeThumbnail.getHeight()));

                        if (thumbnail.getUrl() != null && thumbnail.getWidth() > 0 && thumbnail.getHeight() > 0) {
                            thumbnails.add(thumbnail);
                            resultItem.setEmbeddedSize2(createEmbedded(thumbnail));
                        }
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }

                    try {
                        GiphyOriginal originalThumbnail = giphyThumbnails.getOriginal();
                        Thumbnail thumbnail = new Thumbnail(originalThumbnail.getUrl(), Integer.parseInt(originalThumbnail.getWidth()), Integer.parseInt(originalThumbnail.getHeight()));

                        if (thumbnail.getUrl() != null && thumbnail.getWidth() > 0 && thumbnail.getHeight() > 0) {
                            thumbnails.add(thumbnail);
                            resultItem.setEmbeddedSize4(createEmbedded(thumbnail));
                        }
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }

                    resultItem.setThumbnails(thumbnails);
                    results.addResultItem(resultItem);
                }
            }

            return results;
        } catch (Exception e) {
            throw new InterWebException(e);
        }
    }

    private static String createEmbedded(Thumbnail thumbnail) {
        return "<img src=\"" + thumbnail.getUrl() + "\" height=\"" + thumbnail.getHeight() + "\" width=\"" + thumbnail.getWidth() + "\"/>";
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
}
