package de.l3s.interweb.connector.vimeo;

import java.util.List;
import java.util.regex.Pattern;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.connector.vimeo.entity.Datum;
import de.l3s.interweb.connector.vimeo.entity.Size;
import de.l3s.interweb.connector.vimeo.entity.Tag;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.describe.DescribeConnector;
import de.l3s.interweb.core.describe.DescribeQuery;
import de.l3s.interweb.core.describe.DescribeResults;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;

@Dependent
public class VimeoConnector implements SearchConnector, DescribeConnector {
    private static final Logger log = Logger.getLogger(VimeoConnector.class);
    private static final Pattern pattern = Pattern.compile("(?:https?:)?//(?:www\\.)?(?:player\\.)?vimeo\\.com/(?:[a-z]*/)*([0-9]{6,11})[?]?.*", Pattern.CASE_INSENSITIVE);
    private static final int fallbackPerPage = 100;

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

    public Pattern getLinkPattern() {
        return pattern;
    }

    @Override
    public Uni<DescribeResults> describe(DescribeQuery query) throws ConnectorException {
        return searchClient.describe(query.getId()).map(datum -> new DescribeResults(createSearchItem(datum, null)));
    }

    @Override
    public Uni<SearchConnectorResults> search(SearchQuery query) throws ConnectorException {
        return searchClient.search(
                query.getQuery(),
                query.getPage(),
                query.getPerPage(fallbackPerPage),
                convertSort(query.getSort())
        ).invoke(Unchecked.consumer(vimeoResponse -> {
            if (vimeoResponse.getError() != null
                    && vimeoResponse.getErrorCode() != 2286 // 2286 - no results for this page (when not first page requested)
                    && vimeoResponse.getErrorCode() != 2969) { // 2969 - requested a page of results that does not exist
                throw new ConnectorException(vimeoResponse.getErrorCode() + ": " + vimeoResponse.getDeveloperMessage());
            }
        })).map(vimeoResponse -> {
            SearchConnectorResults queryResult = new SearchConnectorResults();
            queryResult.setTotalResults(vimeoResponse.getTotal());
            if (vimeoResponse.getTotal() == 0) {
                return queryResult;
            }

            int rank = query.getOffset(fallbackPerPage);
            for (Datum video : vimeoResponse.getData()) {
                queryResult.addResultItem(createSearchItem(video, ++rank));
            }

            return queryResult;
        });
    }

    private static SearchItem createSearchItem(Datum video, Integer rank) {
        SearchItem resultItem = new SearchItem(rank);
        resultItem.setType(ContentType.video);
        resultItem.setId(video.getLink().substring(1 + video.getLink().lastIndexOf('/')));
        resultItem.setTitle(video.getName());
        resultItem.setDescription(video.getDescription());
        resultItem.setUrl(video.getLink());
        resultItem.setDuration(video.getDuration());
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

        if (video.getPlayerEmbedUrl() != null) {
            resultItem.setEmbedUrl(video.getPlayerEmbedUrl());
        } else {
            resultItem.setEmbedUrl("https://player.vimeo.com/video/" + resultItem.getId() + "?dnt=1");
        }

        if (video.getPictures() != null) {
            List<Size> pictureSizes = video.getPictures().getSizes();
            for (Size size : pictureSizes) {
                resultItem.setThumbnail(new Thumbnail(size.getLink(), size.getWidth(), size.getHeight()));
            }
        }

        return resultItem;
    }

    private static String convertSort(SearchSort sort) {
        return switch (sort) {
            case date -> "date";
            case popularity -> "plays";
            default -> "relevant";
        };
    }
}
