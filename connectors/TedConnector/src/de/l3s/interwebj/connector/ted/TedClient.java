package de.l3s.interwebj.connector.ted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class TedClient {

	
		public String input;
		public Pattern p;
		public String captionString;
		public String combinedDescription;
		public String description;
		public List<String> idList=new ArrayList<String>();
		
		Scanner scan= new Scanner(System.in);
		
		public String langOfTranscript="fr";
		public String getLangOfTranscript() {
			return langOfTranscript;
		}

		public void setLangOfTranscript(String langOfTranscript) {
			this.langOfTranscript = langOfTranscript;
		}

		boolean enableSearchonTitle=true;
		boolean enableSearchonTranscripts=true;
		boolean enableSearchonDescription=true;
		
		public boolean isEnableSearchonTitle() {
		
			return enableSearchonTitle;
		}

		public void setEnableSearchonTitle(boolean enableSearchonTitle) {
			this.enableSearchonTitle = enableSearchonTitle;
		}

		public boolean isEnableSearchonTranscripts() {
			return enableSearchonTranscripts;
		}

		public void setEnableSearchonTranscripts(boolean enableSearchonTranscripts) {
			this.enableSearchonTranscripts = enableSearchonTranscripts;
		}

		public boolean isEnableSearchonDescription() {
			return enableSearchonDescription;
		}

		public void setEnableSearchonDescription(boolean enableSearchonDescription) {
			this.enableSearchonDescription = enableSearchonDescription;
		}

		String querystring=null;
		

		public HashMap<String, HashMap<String, String>> search(String query, int pageNumber, int resultcount){ 
		
		int offset;
		//input=query.toString();
		input=query;
		//input=" "+input+" ";
		p = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)("+input+")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
		
		String listOfIdsToQuery = null;
//		String superQueryString="select ?id COUNT(?transcript) FROM <"+Config.GRAPH_NAME+"> WHERE {?talk <http://www.w3.org/ns/ma-ont#title> ?title ."+
//				"?talk <http://www.ted.com/id> ?id ."+
//				"?talk <http://www.w3.org/ns/ma-ont#description> ?description ."+
//                "?transcript <http://purl.org/ontology/bibo/transcriptOf> ?talk .?transcript <http://purl.org/dc/terms/language> ?lang. ?transcript <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?value . ";
		String superQueryString="select ?id COUNT(?transcript) FROM <"+Config.GRAPH_NAME+"> WHERE {?talk <http://www.w3.org/ns/ma-ont#title> ?title ."+
				"?talk <http://data.linkededucation.org/resource/ted/id> ?id ."+
				"?talk <http://www.w3.org/ns/ma-ont#description> ?description ."+
                "?transcript <http://purl.org/ontology/bibo/transcriptOf> ?talk .?transcript <http://purl.org/dc/terms/language> ?lang. ?transcript <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?value . ";
                
		
		if(isEnableSearchonTitle() && !isEnableSearchonDescription() && !isEnableSearchonTranscripts()){
			querystring=superQueryString+
					" FILTER((bif:contains(?title,\"'"+input+"'\")))} ";}
		if(isEnableSearchonDescription() && !isEnableSearchonTitle() && !isEnableSearchonTranscripts()){
			querystring=superQueryString+
	                "FILTER((bif:contains(?description,\"'"+input+"'\")))} ";
		}
		
		if(isEnableSearchonTranscripts() && !isEnableSearchonTitle() && !isEnableSearchonDescription()){
			querystring=superQueryString+
	                " FILTER((bif:contains(?value,\"'"+input+"'\")))}  ";
		}
		
		if(isEnableSearchonTitle() && isEnableSearchonDescription() && !isEnableSearchonTranscripts()){
			querystring=superQueryString+
	                " FILTER((bif:contains(?description,\"'"+input+"'\") || bif:contains(?title,\"'"+input+"'\")))}  ";
		}
		
		if(isEnableSearchonTranscripts() && isEnableSearchonDescription() && !isEnableSearchonTitle()){
			querystring=superQueryString+
	                " FILTER((bif:contains(?description,\"'"+input+"'\") || bif:contains(?value,\"'"+input+"'\")))} ";
		}
		
		if(isEnableSearchonTitle() && isEnableSearchonTranscripts() && !isEnableSearchonDescription()){
			querystring=superQueryString+
	                " FILTER((bif:contains(?title,\"'"+input+"'\") || bif:contains(?value,\"'"+input+"'\")))}  ";
		}
		
		if(isEnableSearchonDescription() && isEnableSearchonTitle() && isEnableSearchonTranscripts()){
			querystring=superQueryString+
					" FILTER((bif:contains(?title,\"'"+input+"'\") || bif:contains(?description,\"'"+input+"'\") || bif:contains(?value,\"'"+input+"'\"))) } ";
		}
		
		
		
		
		QueryExecution qexecc = new QueryEngineHTTP(Config.QUERY_ENDPOINT, querystring);
		ResultSet resultss = qexecc.execSelect();
		
		ArrayList<String> listOfIds= new ArrayList<String>();
		
		while(resultss.hasNext()){
			QuerySolution qs = resultss.next();
			String temp=qs.get("id").toString();
			String forPaging  = toNormalizeQuery(temp);
			
			listOfIds.add(forPaging);
		  
		}
		
		//to filter out the unique elements from the retrieved results
		if(listOfIds!=null){
		HashSet<String> hs=new HashSet<String>();
		hs.addAll(listOfIds);
		listOfIds.clear();
		listOfIds.addAll(hs);
		}
		
		int listSize=listOfIds.size();
		offset=(pageNumber-1)*resultcount;
		
		if(offset==0)
		{
			if(listSize<resultcount)
				listOfIdsToQuery= listOfIds.toString().replaceAll("\\[", "(").replaceAll("\\]", ")");
			else
				listOfIdsToQuery=listOfIds.subList(offset, offset+resultcount).toString().replaceAll("\\[", "(").replaceAll("\\]", ")");
				
		}
			
		else
		{
			if(offset<listSize)
			{
				
				if((listSize-offset)<resultcount)
					listOfIdsToQuery=listOfIds.subList(offset, offset+(listSize-offset)).toString().replaceAll("\\[", "(").replaceAll("\\]", ")");
				else
					listOfIdsToQuery=listOfIds.subList(offset, offset+resultcount).toString().replaceAll("\\[", "(").replaceAll("\\]", ")");
					
			}
			else{
				
				listOfIdsToQuery=null;
			}
		}
		
		
			
		
		if(listOfIdsToQuery!=null){

		
		String commomQueryString="select ?title ?description ?transcript ?keywords ?thumbnail ?date ?totalviews "
				+ "?talk ?speaker ?location ?duration ?value ?id ?lang ?transcriptUrl "
				+ "FROM <"+Config.GRAPH_NAME+"> WHERE {?talk <http://www.w3.org/ns/ma-ont#title> ?title ."+
				"?talk <http://data.linkededucation.org/resource/ted/id> ?id ."+
				"?talk <http://www.w3.org/ns/ma-ont#description> ?description ."+
                "?talk <http://www.w3.org/ns/dcat#keyword> ?keywords ."+
                "?talk <http://purl.org/dc/terms/date> ?date ."+
                "?talk <https://schema.org/image> ?thumbnail ."+
                "?talk <http://learnweb.l3s.uni-hannover.de/new/lw/openData-schema/totalViews> ?totalviews ."+
                "?talk <http://www.w3.org/ns/ma-ont#hasContributor> ?speaker ."+
                "?talk <http://www.w3.org/ns/ma-ont#location> ?location ."+
               "?talk <http://www.w3.org/ns/ma-ont#duration> ?duration ."+
               "?transcript <http://purl.org/ontology/bibo/transcriptOf> ?talk .?transcript <http://purl.org/dc/terms/language> ?lang. "
                + "?transcript <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?value .?transcript <https://schema.org/url> ?transcriptUrl. FILTER (?id IN "+listOfIdsToQuery+") }";
	
		
		QueryExecution qexec = new QueryEngineHTTP(Config.QUERY_ENDPOINT, commomQueryString);
		ResultSet results = qexec.execSelect();
		
		HashMap<String,HashMap<String,String>> hmap= new HashMap<String,HashMap<String,String>>();
		
		 while(results.hasNext()){
	    	 com.hp.hpl.jena.query.QuerySolution qs= results.next();
				HashMap<String,String> valueMap=new HashMap<String,String>();
				
				if(hmap.containsKey(qs.get("id").toString())){
					HashMap<String,String> temp=hmap.get(qs.get("id").toString());
					if(qs.get("transcript").toString()!=null){
						temp.put( qs.get("transcript").toString(), qs.get("value").toString());
					}
					else
						valueMap.put("transcriptUrl", "NA");
					if(qs.get("transcriptUrl").toString()!=null){
						valueMap.put("transcriptUrl", qs.get("transcriptUrl").toString());
					}
					 else
							valueMap.put("transcriptUrl", "NA");
				}
			
				
			else{
				valueMap.put("id2", qs.get("id").toString());	
			if(qs.get("speaker").toString()!=null){
				
				valueMap.put("speaker", qs.get("speaker").toString());
			}
			else
				valueMap.put("speaker", "NA");
			
			if(qs.get("thumbnail").toString()!=null){
				valueMap.put("thumbnail", qs.get("thumbnail").toString());
			}
			else
				valueMap.put("thumbnail", "NA");
			
			if(qs.get("location").toString()!=null){ 
				valueMap.put("location", qs.get("location").toString());
			}
			else
				valueMap.put("location", "NA");
			
			if(qs.get("duration").toString()!=null){
				valueMap.put("duration", qs.get("duration").toString());
			}
			else
				valueMap.put("duration", "NA");
			
			 if(qs.get("description").toString()!=null)
				 valueMap.put("description", qs.get("description").toString());
			 else
					valueMap.put("description", "NA");
			
			if(qs.get("talk").toString()!=null){
				valueMap.put("talk", qs.get("talk").toString());
			}
			 else
					valueMap.put("talk", "NA");
			
			if(qs.get("title").toString()!=null){
				valueMap.put("title", qs.get("title").toString());
			}
			 else
					valueMap.put("title", "NA");
			
			if(qs.get("date").toString()!=null){
				valueMap.put("date", qs.get("date").toString());
			}
			 else
					valueMap.put("date", "NA");
			
			if(qs.get("totalviews").toString()!=null){
				valueMap.put("totalviews", qs.get("totalviews").toString());
			}
			 else
					valueMap.put("totalviews", "NA");
			
			if(qs.get("keywords").toString()!=null){
				valueMap.put("keywords", qs.get("keywords").toString());
			}
			 else
					valueMap.put("keywords", "NA");
			if(qs.get("transcript").toString()!=null){
				
				valueMap.put( qs.get("transcript").toString(), qs.get("value").toString());
			}
			 else
					valueMap.put("value", "NA");
			
			if(qs.get("transcriptUrl").toString()!=null){
				valueMap.put("transcriptUrl", qs.get("transcriptUrl").toString());
			}
			 else
					valueMap.put("transcriptUrl", "NA");
			

			
			
			hmap.put(qs.get("id").toString(), valueMap);
			}
			  
			
		
		
		}
		  
			return hmap;  
		
		  
	     } 
		else
			return null;
		}
		
	
		
		
		public Pattern createPattern(de.l3s.interwebj.query.Query query){
			input=query.getQuery();
			//Pattern p = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)("+input+")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
			Pattern p=Pattern.compile("[^.][\\w]*+input+[\\w]*[.]");
			return p;
		} 
	   
		public String toNormalizeQuery(String temp){
			temp="\""+temp;
     		temp = temp.substring(0, temp.indexOf("^^")) + "\"" + temp.substring(temp.indexOf("^^"), temp.length());
     		temp=  temp.substring(0,temp.indexOf("http"))+"<"+temp.substring(temp.indexOf("http"),temp.length())+">";
     		return temp;
		}

}
