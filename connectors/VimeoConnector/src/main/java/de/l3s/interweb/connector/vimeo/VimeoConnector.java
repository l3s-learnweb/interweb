package de.l3s.interweb.connector.vimeo;

import java.util.List;

import jakarta.enterprise.context.Dependent;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.connector.vimeo.entity.*;
import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;

@Dependent
public class VimeoConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(VimeoConnector.class);

    @RestClient
    VimeoSearchClient searchClient;

    @Override
    public String getName() {
        return "Vimeo";
    }

    @Override
    public String getBaseUrl() {
        return "https://vimeo.com/";
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.video};
    }

    @Override
    public SearchConnectorResults search(SearchQuery query, AuthCredentials credentials) throws ConnectorException {
        final VimeoResponse vimeoResponse = searchClient.search(
                query.getQuery(),
                query.getPage(),
                query.getPerPage(),
                createSortOrder(query.getRanking())
        ).await().indefinitely();

        if (vimeoResponse.getError() != null
                && vimeoResponse.getErrorCode() != 2286 // 2286 - no results for this page (when not first page requested)
                && vimeoResponse.getErrorCode() != 2969) { // 2969 - requested a page of results that does not exist
            throw new ConnectorException(vimeoResponse.getErrorCode() + ": " + vimeoResponse.getDeveloperMessage());
        }

        SearchConnectorResults queryResult = new SearchConnectorResults();
        if (vimeoResponse.getTotal() == 0) {
            return queryResult;
        }

        int count = (query.getPage() - 1) * query.getPerPage();
        queryResult.setTotalResults(vimeoResponse.getTotal());

        for (Datum video : vimeoResponse.getData()) {
            try {
                SearchItem resultItem = new SearchItem(count++);
                resultItem.setType(ContentType.video);
                resultItem.setId(video.getLink().substring(1 + video.getLink().lastIndexOf('/')));
                resultItem.setTitle(video.getName());
                resultItem.setDescription(video.getDescription());
                resultItem.setUrl(video.getLink());
                resultItem.setDuration(video.getDuration().longValue());
                resultItem.setDate(DateUtils.parse(video.getCreatedTime()));
                resultItem.setWidth(video.getWidth());
                resultItem.setHeight(video.getHeight());

                if (video.getUser() != null) {
                    resultItem.setAuthor(video.getUser().getName());
                    resultItem.setAuthorUrl(video.getUser().getLink());
                }

                if (video.getTags() != null) {
                    for (Tag tag : video.getTags()) {
                        resultItem.getTags().add(tag.getName());
                    }
                }
                if (video.getMetadata() != null && video.getMetadata().getConnections().getComments() != null) {
                    resultItem.setCommentsCount(video.getMetadata().getConnections().getComments().getTotal());
                }
                if (video.getStats() != null && video.getStats().getPlays() != null) {
                    resultItem.setViewsCount(video.getStats().getPlays());
                }

                resultItem.setEmbeddedUrl("https://player.vimeo.com/video/" + resultItem.getId() + "?dnt=1");

                Pictures pictures = video.getPictures();
                if (pictures == null) {
                    queryResult.addTotalResults(-1);
                    continue; // makes no sense, we can't show them
                }

                List<Size> pictureSizes = pictures.getSizes();
                for (Size size : pictureSizes) {
                    resultItem.setThumbnail(new Thumbnail(size.getLink(), size.getWidth(), size.getHeight()));
                }

                queryResult.addResultItem(resultItem);
            } catch (Throwable e) {
                log.error("Can't parse entry: ", e);
            }
        }

        return queryResult;
    }

    private static String createSortOrder(SearchRanking ranking) {
        return switch (ranking) {
            case date -> "date";
            case interestingness -> "plays";
            default -> "relevant";
        };
    }
}
