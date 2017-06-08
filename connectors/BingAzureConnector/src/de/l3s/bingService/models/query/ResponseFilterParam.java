package de.l3s.bingService.models.query;

public enum ResponseFilterParam {

	IMAGES("images"), VIDEOS("videos"), NEWS("news"), WEB_PAGES("webpages"), COMPUTATION("computation"),
	RELATEDSEARCHES("relatedsearches"), SPELLSUGGESTIONS("spellsuggestions"), TIMEZONE("TIMEZONE");

	private String value;

	ResponseFilterParam(String value) {
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value.toLowerCase();
	}

}
