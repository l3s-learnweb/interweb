package de.l3s.interwebj.query;


public class ImageResultItem
    extends ResultItem
{
	
	public ImageResultItem(String connectorName)
	{
		super(connectorName);
	}
	

	@Override
	public String asHtml()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<a href=\"");
		sb.append(getUrl());
		sb.append("\" target=\"_blank\" class=\"search_result\"><img src=\"");
		sb.append(getPreviewUrl());
		sb.append("\" /></a>");
		return sb.toString();
	}
}
