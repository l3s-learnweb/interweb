package de.l3s.bingCacheService.entity;

import java.util.Date;

public class Response {
	private String text;
	private boolean isExpired;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


	public Response(String text, boolean isExpired) {
		this.text = text;
		this.setExpired(isExpired);
	}

	public boolean isExpired() {
		return isExpired;
	}

	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}


}
