package de.l3s.interwebj.query;


public class ImageResultItem
    extends ResultItem
{
	
	private static final long serialVersionUID = -2958282961556835071L;
	

	public ImageResultItem(String connectorName)
	{
		super(connectorName);
		setType(Query.CT_IMAGE);
	}
	

	@Override
	public String asHtml()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<a href=\"");
		sb.append(getUrl());
		sb.append("\" target=\"_blank\" class=\"search_result\"><img src=\"");
		sb.append(getImageUrl());
		sb.append("\" /></a>");
		return sb.toString();
	}
}
