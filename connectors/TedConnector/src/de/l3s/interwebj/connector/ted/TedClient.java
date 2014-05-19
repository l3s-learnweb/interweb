package de.l3s.interwebj.connector.ted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
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
			System.out.println();
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
		

		public HashMap<String, HashMap<String, String>> search(String query){ 
		
		//input=query.toString();
		input=query;
		input=" "+input+" ";
		p = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)("+input+")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
		
		String commomQueryString="select ?title ?description ?transcript ?keywords ?thumbnail ?date ?totalviews ?talk ?speaker ?location ?duration ?value ?id ?lang FROM <http://data-observatory.org/ted_talks> WHERE {?talk <http://www.w3.org/ns/ma-ont#title> ?title ."+
				"?talk <http://www.ted.com/id> ?id ."+
				"?talk <http://www.w3.org/ns/ma-ont#description> ?description ."+
                "?talk <http://www.w3.org/ns/dcat#keyword> ?keywords ."+
                "?talk <http://purl.org/dc/terms/date> ?date ."+
                "?talk <http://purl.org/ontology/bibo/Image> ?thumbnail ."+
                "?talk <http://http://learnweb.l3s.uni-hannover.de/new/lw/openData-schema/totalViews> ?totalviews ."+
                "?talk <http://www.w3.org/ns/ma-ont#hasContributor> ?speaker ."+
                "?talk <http://www.w3.org/ns/ma-ont#location> ?location ."+
                "?talk <http://www.w3.org/ns/ma-ont#duration> ?duration ."+
                "?transcript <http://purl.org/ontology/bibo/transcriptOf> ?talk .?transcript <http://purl.org/dc/terms/language> ?lang. ?transcript <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?value . ";
		
	/*	if(isEnableSearchonTitle() && !isEnableSearchonDescription() && !isEnableSearchonTranscripts()){
			querystring=commomQueryString+
					" FILTER(regex(?title,\""+input+"\",\"i\") && (regex(?lang,\"en\",\"i\") || regex(?lang,\""+langOfTranscript+"\")))} ORDER BY ?id LIMIT 10";}
		if(isEnableSearchonDescription() && !isEnableSearchonTitle() && !isEnableSearchonTranscripts()){
			querystring=commomQueryString+
	                "FILTER(regex(?description,\""+input+"\",\"i\") && (regex(?lang,\"en\",\"i\") || regex(?lang,\""+langOfTranscript+"\")))} ORDER BY ?id LIMIT 10";
		}
		
		if(isEnableSearchonTranscripts() && !isEnableSearchonTitle() && !isEnableSearchonDescription()){
			querystring=commomQueryString+
	                " FILTER(regex(?value,\""+input+"\",\"i\") && (regex(?lang,\"en\",\"i\") || regex(?lang,\""+langOfTranscript+"\")))} ORDER BY ?id LIMIT 10";
		}
		
		if(isEnableSearchonTitle() && isEnableSearchonDescription() && !isEnableSearchonTranscripts()){
			querystring=commomQueryString+
	                " FILTER((regex(?title,\""+input+"\",\"i\") || regex(?descrption,\""+input+"\",\"i\")) && (regex(?lang,\"en\",\"i\") || regex(?lang,\""+langOfTranscript+"\")))} ORDER BY ?id LIMIT 10";
		}
		
		if(isEnableSearchonTranscripts() && isEnableSearchonDescription() && !isEnableSearchonTitle()){
			querystring=commomQueryString+
	                " FILTER((regex(?value,\""+input+"\",\"i\") || regex(?descrption,\""+input+"\",\"i\")) && (regex(?lang,\"en\") || regex(?lang,\""+langOfTranscript+"\")))} ORDER BY ?id LIMIT 10";
		}
		
		if(isEnableSearchonTitle() && isEnableSearchonTranscripts() && !isEnableSearchonDescription()){
			querystring=commomQueryString+
	                " FILTER((regex(?title,\""+input+"\",\"i\") || regex(?value,\""+input+"\",\"i\")) && (regex(?lang,\"en\") || regex(?lang,\""+langOfTranscript+"\")))} ORDER BY ?id LIMIT 10";
		}
		
		if(isEnableSearchonDescription() && isEnableSearchonTitle() && isEnableSearchonTranscripts()){
			querystring=commomQueryString+
	                " FILTER((regex(?title,\""+input+"\",\"i\") || regex(?description,\""+input+"\",\"i\") || regex(?value,\""+input+"\",\"i\")) && (regex(?lang,\"en\") || regex(?lang,\""+langOfTranscript+"\")))} ORDER BY ?id LIMIT 10";
		} */
		
		if(isEnableSearchonTitle() && !isEnableSearchonDescription() && !isEnableSearchonTranscripts()){
			querystring=commomQueryString+
					" FILTER((bif:contains(?title,\"'"+input+"'\")))} ORDER BY ?id ";}
		if(isEnableSearchonDescription() && !isEnableSearchonTitle() && !isEnableSearchonTranscripts()){
			querystring=commomQueryString+
	                "FILTER((bif:contains(?description,\"'"+input+"'\")))} ORDER BY ?id ";
		}
		
		if(isEnableSearchonTranscripts() && !isEnableSearchonTitle() && !isEnableSearchonDescription()){
			querystring=commomQueryString+
	                " FILTER((bif:contains(?value,\"'"+input+"'\")))} ORDER BY ?id ";
		}
		
		if(isEnableSearchonTitle() && isEnableSearchonDescription() && !isEnableSearchonTranscripts()){
			querystring=commomQueryString+
	                " FILTER((bif:contains(?description,\"'"+input+"'\") || bif:contains(?title,\"'"+input+"'\")))} ORDER BY ?id ";
		}
		
		if(isEnableSearchonTranscripts() && isEnableSearchonDescription() && !isEnableSearchonTitle()){
			querystring=commomQueryString+
	                " FILTER((bif:contains(?description,\"'"+input+"'\") || bif:contains(?value,\"'"+input+"'\")))} ORDER BY ?id ";
		}
		
		if(isEnableSearchonTitle() && isEnableSearchonTranscripts() && !isEnableSearchonDescription()){
			querystring=commomQueryString+
	                " FILTER((bif:contains(?title,\"'"+input+"'\") || bif:contains(?value,\"'"+input+"'\")))} ORDER BY ?id ";
		}
		
		if(isEnableSearchonDescription() && isEnableSearchonTitle() && isEnableSearchonTranscripts()){
			querystring=commomQueryString+
	                " FILTER((bif:contains(?title,\"'"+input+"'\") || bif:contains(?description,\"'"+input+"'\") || bif:contains(?value,\"'"+input+"'\")))} ORDER BY ?id ";
		}
		
		
		
		

		
        //com.hp.hpl.jena.query.Query sparqlquery = QueryFactory.create(querystring);
		
		//QueryExecution qexec = QueryExecutionFactory.sparqlService("http://meco.l3s.uni-hannover.de:8890/sparql", sparqlquery);
		QueryExecution qexec = new QueryEngineHTTP("http://meco.l3s.uni-hannover.de:8890/sparql", querystring);
		ResultSet results = qexec.execSelect();
		
		HashMap<String,HashMap<String,String>> hmap= new HashMap<String,HashMap<String,String>>();
		
		
		
	     while(results.hasNext()){
	    	 com.hp.hpl.jena.query.QuerySolution qs= results.nextSolution();
				HashMap<String,String> valueMap=new HashMap<String,String>();
				
				if(hmap.containsKey(qs.get("id").toString())){
					HashMap<String,String> temp=hmap.get(qs.get("id").toString());
					if(qs.get("transcript").toString()!=null){
						temp.put( qs.get("transcript").toString(), qs.get("value").toString());
					}
				}
			
				
			else{
				valueMap.put("id2", qs.get("id").toString());	
			if(qs.get("speaker").toString()!=null){
				
				valueMap.put("speaker", qs.get("speaker").toString());
			}
			
			if(qs.get("thumbnail").toString()!=null){
				valueMap.put("thumbnail", qs.get("thumbnail").toString());
			}
			
			if(qs.get("location").toString()!=null){ 
				valueMap.put("location", qs.get("location").toString());
			}
			
			if(qs.get("duration").toString()!=null){
				valueMap.put("duration", qs.get("duration").toString());
			}
			
			 if(qs.get("description").toString()!=null)
				 valueMap.put("description", qs.get("description").toString());
			
			
			if(qs.get("talk").toString()!=null){
				valueMap.put("talk", qs.get("talk").toString());
			}
			
			if(qs.get("title").toString()!=null){
				valueMap.put("title", qs.get("title").toString());
			}
			
			if(qs.get("date").toString()!=null){
				valueMap.put("date", qs.get("date").toString());
			}
			
			if(qs.get("totalviews").toString()!=null){
				valueMap.put("totalviews", qs.get("totalviews").toString());
			}
			
			if(qs.get("keywords").toString()!=null){
				valueMap.put("keywords", qs.get("keywords").toString());
			}
			
			if(qs.get("transcript").toString()!=null){
				valueMap.put( qs.get("transcript").toString(), qs.get("value").toString());
			}
			
			
			
			hmap.put(qs.get("id").toString(), valueMap);
			}
			  
			
		
		
		}
		  System.out.println(hmap.size());
			return hmap;  
		  
	     } 
		
	
		
		
		public Pattern createPattern(de.l3s.interwebj.query.Query query){
			input=query.getQuery();
			Pattern p = Pattern.compile("([A-Z][^.?!()]*?)?(?<!\\w)(?i)("+input+")(?!\\w)[^.?!()]*?[.?!()]{1,2}\"?");
			return p;
		} 
	   

}
