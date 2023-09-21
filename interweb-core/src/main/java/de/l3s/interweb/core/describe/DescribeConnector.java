package de.l3s.interweb.core.describe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;

public interface DescribeConnector extends Connector {

    Pattern getLinkPattern();

    default String findId(String link) {
        final Matcher vimeoMatcher = getLinkPattern().matcher(link);
        if (vimeoMatcher.find()) {
            for (int i = 1; i <= vimeoMatcher.groupCount(); i++) {
                String group = vimeoMatcher.group(i);
                if (group != null) {
                    return group;
                }
            }
        }
        return null;
    }

    Uni<DescribeResults> describe(DescribeQuery query) throws ConnectorException;
}
