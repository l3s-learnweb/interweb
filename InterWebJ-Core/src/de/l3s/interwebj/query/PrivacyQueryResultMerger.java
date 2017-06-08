package de.l3s.interwebj.query;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Philipp
 *
 */
public class PrivacyQueryResultMerger implements QueryResultMerger
{
    private int originalResultSize;
    private int $estimated_public_results;
    private int $estimated_private_results;

    public PrivacyQueryResultMerger(int originalResultSize, int estimatedPrivateResults, int estimatedPublicResults)
    {
	super();
	this.originalResultSize = originalResultSize;
	this.$estimated_private_results = estimatedPrivateResults;
	this.$estimated_public_results = estimatedPublicResults;
    }

    private class PrivacyCompatator implements Comparator<ResultItem>
    {
	@Override
	public int compare(ResultItem o1, ResultItem o2)
	{
	    return Double.compare(o1.getPrivacy(), o2.getPrivacy());
	}

    }

    @Override
    public QueryResult merge(QueryResult queryResult)
    {
	//reset the number of results
	queryResult.getQuery().setResultCount(originalResultSize);

	List<ResultItem> resultItems = queryResult.getResultItems();

	if(resultItems.size() <= originalResultSize)
	    return queryResult;

	Collections.sort(resultItems, new PrivacyCompatator());

	int $private_results = 0;
	int $public_results = resultItems.size();
	for(ResultItem item : resultItems)
	{
	    if(0f < item.getPrivacy())
	    {
		$private_results++;
		$public_results--;
	    }
	    else if(0f == item.getPrivacy())
		$public_results--;

	}
	System.out.println($estimated_private_results + " - " + $estimated_public_results);

	//System.out.println($private_results+" - "+$public_results);

	// if we don't get as much {public,private} results as estimated, take more of the other kind
	if($private_results < $estimated_private_results || $public_results < $estimated_public_results && ($private_results > $estimated_private_results || $public_results > $estimated_public_results))
	{
	    while($private_results < $estimated_private_results && $public_results > $estimated_public_results)
	    {
		$estimated_private_results--;
		$estimated_public_results++;
	    }

	    while($public_results < $estimated_public_results && $private_results > $estimated_private_results)
	    {
		$estimated_private_results++;
		$estimated_public_results--;
	    }
	}
	else if($private_results < $estimated_private_results && $public_results < $estimated_public_results)
	{
	    $estimated_private_results = $private_results;
	    $estimated_public_results = $public_results;
	}
	//System.out.println($estimated_private_results+" - "+$estimated_public_results);

	Iterator<ResultItem> iterator = resultItems.iterator();
	int i = 0;
	int rightBoundary = resultItems.size() - $estimated_private_results;
	while(iterator.hasNext())
	{
	    ResultItem item = iterator.next();
	    System.out.print(item.getPrivacy() + " - " + item.getTitle());
	    if(i >= $estimated_public_results && i < rightBoundary)
	    {
		iterator.remove();
		//System.out.print(" | removed");
	    }
	    //System.out.println();
	    i++;
	}

	/*
	
	$selected_ids = array();
	for(int $i=0; $i<$estimated_private_results; $i++)
		$selected_ids[] = $ids[$i];
	
	for($i=count($ids)-$estimated_public_results; $i<count($ids); $i++)
		$selected_ids[] = $ids[$i];	
		
	#if(DEBUG) echo $estimated_private_results.' - '.$estimated_public_results."<br>\n" ;
	
	sort($selected_ids);
	$selected_results = array();
	
	foreach($selected_ids as $id)
	{
	$selected_results[] = $fetched_results[$id];
	}
	$fetched_results = $selected_results;
	
	}*/

	return queryResult;
    }

}
