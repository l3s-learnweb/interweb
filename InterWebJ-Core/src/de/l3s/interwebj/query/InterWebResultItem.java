package de.l3s.interwebj.query;


public class InterWebResultItem
    extends ResultItem
{
	
	private static final long serialVersionUID = -1049898089517241548L;
	

	public InterWebResultItem(String connectorName)
	{
		super(connectorName);
	}
	

	@Override
	public String asHtml()
	{
		StringBuilder sb = new StringBuilder();
		if (Query.CT_IMAGE.equals(getType()))
		{
			sb.append("<a href=\"");
			sb.append(getUrl());
			sb.append("\" target=\"_blank\" class=\"search_result\"><img src=\"");
			sb.append(getImageUrl());
			sb.append("\" /></a>");
		}
		return sb.toString();
	}
	
}
