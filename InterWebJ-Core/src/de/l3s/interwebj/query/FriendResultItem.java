package de.l3s.interwebj.query;


public class FriendResultItem
    extends ResultItem
{
	
	private static final long serialVersionUID = 6210217200543587461L;
	

	public FriendResultItem(String connectorName)
	{
		super(connectorName);
		setType(Query.CT_FRIEND);
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
