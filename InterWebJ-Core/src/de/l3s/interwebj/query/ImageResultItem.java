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
}
