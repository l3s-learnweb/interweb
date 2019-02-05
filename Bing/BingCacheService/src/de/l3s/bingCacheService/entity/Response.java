package de.l3s.bingCacheService.entity;

import java.time.LocalDateTime;

public class Response
{
    private long responseId;
    private final String response;
    private final int queryId;
    private final int clientId;
    private final LocalDateTime timestamp;

    public Response(long responseId, int queryId, int clientId, String response, LocalDateTime timestamp)
    {
        super();
        this.responseId = responseId;
        this.queryId = queryId;
        this.clientId = clientId;
        this.response = response;
        this.timestamp = timestamp;
    }

    public Response(String response, int queryId, int clientId)
    {
        super();
        this.response = response;
        this.queryId = queryId;
        this.clientId = clientId;
        this.timestamp = LocalDateTime.now();
    }

    public long getResponseId()
    {
        return responseId;
    }

    public void setResponseId(long responseId)
    {
        this.responseId = responseId;
    }

    public String getResponse()
    {
        return response;
    }

    public int getQueryId()
    {
        return queryId;
    }

    public int getClientId()
    {
        return clientId;
    }

    public LocalDateTime getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String toString()
    {
        return "Response [responseId=" + responseId + ", queryId=" + queryId + ", clientId=" + clientId + ", timestamp=" + timestamp + ", response=" + response + "]";
    }

}
