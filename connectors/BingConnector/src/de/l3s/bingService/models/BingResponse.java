package de.l3s.bingService.models;

import java.util.List;

import com.google.gson.annotations.JsonAdapter;

import de.l3s.bingService.adapters.BingResponseAdapter;

@JsonAdapter(BingResponseAdapter.class)
public class BingResponse {

    private ImageHolder images;
    private VideoHolder videos;
    private WebPagesHolder webPages;

    private List<Error> errors;
    private String jsonResponse;

    public WebPagesHolder getWebPages() {
        return webPages;
    }

    public void setWebPages(WebPagesHolder webPages) {
        this.webPages = webPages;
    }

    public ImageHolder getImages() {
        return images;
    }

    public void setImages(ImageHolder images) {
        this.images = images;
    }

    public VideoHolder getVideos() {
        return videos;
    }

    public void setVideos(VideoHolder videos) {
        this.videos = videos;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(final List<Error> errors) {
        this.errors = errors;
    }

    public String getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }
}
