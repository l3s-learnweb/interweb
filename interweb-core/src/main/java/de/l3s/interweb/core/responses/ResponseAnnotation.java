package de.l3s.interweb.core.responses;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAnnotation {
    /**
     * The type of the annotation.
     * Always present.
     */
    @JsonProperty("type")
    private String type;

    /**
     * The ID of the file.
     * Present in: file_citation, container_file_citation, file_path.
     */
    @JsonProperty("file_id")
    private String fileId;

    /**
     * The filename of the file cited.
     * Present in: file_citation, container_file_citation.
     */
    @JsonProperty("filename")
    private String filename;

    /**
     * The index of the file in the list of files.
     * Present in: file_citation, file_path.
     */
    @JsonProperty("index")
    private Integer index;

    /**
     * The index of the first character of the citation in the message.
     * Present in: url_citation, container_file_citation.
     */
    @JsonProperty("start_index")
    private Integer startIndex;

    /**
     * The index of the last character of the citation in the message.
     * Present in: url_citation, container_file_citation.
     */
    @JsonProperty("end_index")
    private Integer endIndex;

    /**
     * The title of the web resource.
     * Present in: url_citation.
     */
    @JsonProperty("title")
    private String title;

    /**
     * The URL of the web resource.
     * Present in: url_citation.
     */
    @JsonProperty("url")
    private String url;

    /**
     * The ID of the container file.
     * Present in: container_file_citation.
     */
    @JsonProperty("container_id")
    private String containerId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }
}

