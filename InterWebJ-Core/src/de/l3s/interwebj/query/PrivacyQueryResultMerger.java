package de.l3s.interwebj.query;

public class PrivacyQueryResultMerger implements QueryResultMerger {

	@Override
	public QueryResult merge(QueryResult queryResult) {
		
		/*
		 if($params['private'] != -1 && count($privacy) != 0)
				{
					//reset the number of results
					$saved_query->setNumberOfResults($params['number_of_results']);
					if($params['private'] < 0) $params['private'] = 0;
					if($params['private'] > 1) $params['private'] = 1;
#if(DEBUG) echo $estimated_private_results.' - '.$estimated_public_results."<br>\n";

					array_multisort($privacy, SORT_DESC, $ids, SORT_ASC);
						
					$private_results = 0;
					$public_results = count($privacy);
					for($i=0; $i<count($privacy); $i++)
					{
						if(0 < $privacy[$i])
						{
							$private_results++;
							$public_results--;
						}
					}						
						
#if(DEBUG) echo $private_results.' - '.$public_results."<br>\n";
						
					// if we don't get as much {public,private} results as estimated, take more of the other kind
					if($private_results < $estimated_private_results || $public_results < $estimated_public_results &&
						($private_results > $estimated_private_results || $public_results > $estimated_public_results))
					{
						while($private_results < $estimated_private_results && $public_results > $estimated_public_results)
						{
							$estimated_private_results--;
							$estimated_public_results++;
						}
						
						while($public_results <$estimated_public_results  &&  $private_results> $estimated_private_results)
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
					
					$selected_ids = array();
					for($i=0; $i<$estimated_private_results; $i++)
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

				}
		 */
		return null;
	}

}
