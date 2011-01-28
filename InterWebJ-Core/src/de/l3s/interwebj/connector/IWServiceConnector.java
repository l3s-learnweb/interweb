package de.l3s.interwebj.connector;


import java.util.*;

import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;


public interface IWServiceConnector
{
	
	public QueryResult get(Query query);
	

	public String getName();
	

	public void put(Object o, List<String> types, Properties properties);
	
}
