package de.l3s.bingService.models;

import java.util.List;

public class New extends WebPageBase {

	private Image image;

	private String description;
	
	private List<About> about;

	private List<Provider> provider;

	private String datePublished;

	private String category;

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<About> getAbout() {
		return about;
	}

	public void setAbout(List<About> about) {
		this.about = about;
	}

	public List<Provider> getProviders() {
		return provider;
	}

	public void setProviders(List<Provider> provider) {
		this.provider = provider;
	}

	public String getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(String datePublished) {
		this.datePublished = datePublished;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
