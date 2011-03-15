package de.l3s.interwebj.query;


import java.util.*;


public class ResultSet
{
	
	private List<ResultItem> resultItems;
	

	public void addResultItem(ResultItem resultItem)
	{
		resultItems.add(resultItem);
	}
	

	public List<ResultItem> getResultItems()
	{
		return resultItems;
	}
}
