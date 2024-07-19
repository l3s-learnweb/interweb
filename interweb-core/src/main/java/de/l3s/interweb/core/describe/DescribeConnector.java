package de.l3s.interweb.core.describe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;

public interface DescribeConnector extends Connector {

    Pattern getLinkPattern();

    default String findId(String link) {
        if (link.length() > 1000) {
            throw new IllegalArgumentException("Input too long");
        }

        final Matcher matcher = getLinkPattern().matcher(link);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null) {
                    return group;
                }
            }
        }
        return null;
    }

    Uni<DescribeResults> describe(DescribeQuery query) throws ConnectorException;
}
