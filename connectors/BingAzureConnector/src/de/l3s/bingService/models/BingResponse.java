package de.l3s.bingService.models;

public class BingResponse extends Entity {

	private String queryId;

	private String clientId;

	private Instrumentation instrumentation;

	private WebPagesMainHolder webPages;

	private ImageHolder images;

	private NewsHolder news;

	private VideoHolder videos;

	private String jsonContent;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String type) {
		this.clientId = type;
	}

	public Instrumentation getInstrumentation() {
		return instrumentation;
	}

	public void setInstrumentation(Instrumentation instrumentation) {
		this.instrumentation = instrumentation;
	}

	public WebPagesMainHolder getWebPages() {
		return webPages;
	}

	public void setWebPages(WebPagesMainHolder webPages) {
		this.webPages = webPages;
	}

	public ImageHolder getImages() {
		return images;
	}

	public void setImages(ImageHolder images) {
		this.images = images;
	}

	public NewsHolder getNews() {
		return news;
	}

	public void setNews(NewsHolder news) {
		this.news = news;
	}

	public VideoHolder getVideos() {
		return videos;
	}

	public void setVideos(VideoHolder videos) {
		this.videos = videos;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public String getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}

}
