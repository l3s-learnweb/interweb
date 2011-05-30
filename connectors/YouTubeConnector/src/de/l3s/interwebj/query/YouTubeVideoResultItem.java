package de.l3s.interwebj.query;


public class YouTubeVideoResultItem
    extends ResultItem
{
	
	private static final long serialVersionUID = -1049898089517241548L;
	public static final int DEFAULT_EMBEDDED_WIDTH = 240;
	public static final int DEFAULT_EMBEDDED_HEIGHT = 180;
	

	public YouTubeVideoResultItem(String connectorName)
	{
		super(connectorName);
		setType(Query.CT_VIDEO);
	}
	
	//	@Override
	//	public String getEmbeddedSnippet(Dimension maxDimension)
	//	{
	//		StringBuilder sb = new StringBuilder();
	//		sb.append("<a href=\"");
	//		sb.append(getUrl());
	//		sb.append("\" target=\"_blank\" class=\"search_result\"><img src=\"");
	//		sb.append(getImageUrl());
	//		sb.append("\" /></a>");
	//		return sb.toString();
	//	}
	
	//	@Override
	//	public String asHtml()
	//	{
	//		StringBuilder sb = new StringBuilder();
	//		sb.append("<a href=\"");
	//		sb.append(getUrl());
	//		sb.append("\" target=\"_blank\" class=\"search_result\"><img src=\"");
	//		sb.append(getImageUrl());
	//		sb.append("\" /></a>");
	//		return sb.toString();
	//	}
	
}
