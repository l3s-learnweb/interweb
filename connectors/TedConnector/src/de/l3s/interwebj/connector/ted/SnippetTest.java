package de.l3s.interwebj.connector.ted;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class SnippetTest {
	
	public static void main(String args[]){
		
		String queryString="SELECT ?value FROM  <"+Config.GRAPH_NAME+"> WHERE {?transcript <http://purl.org/ontology/bibo/transcriptOf> <http://data.linkededucation.org/resource/ted/talks/frank_gehry_as_a_young_rebel>.?transcript <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?value. ?transcript <http://purl.org/dc/terms/language> \"en\"^^<http://www.w3.org/2001/XMLSchema#string>}";
		String transcriptValue="1:29 Now, planning a complex social system is a very hard thing to do, and let me tell you a story. Back in 1989, when the Berlin Wall fell, an urban planner in London got a phone call from a colleague in Moscow saying, basically, Hi, this is Vladimir. I'd like to know, who's in charge of London's bread supply?" ;
		QueryExecution qexec = new QueryEngineHTTP(Config.QUERY_ENDPOINT, queryString);
		ResultSet results = qexec.execSelect();
	
		 while(results.hasNext()){
	    	 com.hp.hpl.jena.query.QuerySolution qs= results.next();
	    	String value= qs.get("value").toString();
	    	 String input="berlin";
	    	 Pattern p = Pattern.compile((input),Pattern.CASE_INSENSITIVE);
            
	 		Matcher m = p.matcher(value);
	 		boolean a=m.find();
	 		System.out.println(a);
	 		
		 }
		
		
		
		
//		int flag=0; //flag : to decide to add to the list of time or paragraphs
//		String captionString="";
//		String snipIncludingInput = "";
//		
//		String dummyValue=transcriptValue;
//		String[] transcriptParts=dummyValue.split("\\t|\\n");
//		List<String> timeVals=new ArrayList<String>();
//		List<String> paraVals=new ArrayList<String>();
//		
//		for(String str:transcriptParts){
//			if(flag==0){
//				flag=1;
//				timeVals.add(str);
//			}
//			else{
//				flag=0;
//				paraVals.add(str);
//			}
//				
//		}
//		
//		for(String para: paraVals){
//		System.out.println(para);
//			if(para.contains(input)){
//				int numberOfWhitespaces=0;
//				int index=paraVals.indexOf(para);
//				int lastIndex=para.length();
//				String time=timeVals.get(index); // to get corresponding value stored in the list TimeVals
//			    
//				int indexOfSearchTerm=para.indexOf(input);
//				  // to obtain 5 words before the input keyword 
//			    while(numberOfWhitespaces<6 && indexOfSearchTerm>=index){
//			    	if(Character.isWhitespace(para.charAt(indexOfSearchTerm))){
//			    		numberOfWhitespaces++;
//			    	}
//			    	
//			    		try{
//			    		indexOfSearchTerm--;}
//			    		catch(Exception e){
//			    			e.printStackTrace();
//			    			break;
//			    		}
//			    	
//			    }
//			    snipIncludingInput+=para.substring(++indexOfSearchTerm, para.indexOf(input))+input;
//			    numberOfWhitespaces=0;
//			    indexOfSearchTerm=para.indexOf(input);
//			    // to obtain 5 words after the input keyword 
//			    while(numberOfWhitespaces<6 && indexOfSearchTerm<lastIndex){
//			    	if(Character.isWhitespace(para.charAt(indexOfSearchTerm))){
//			    		numberOfWhitespaces++;
//			    	}
//			    	
//			    		try{
//			    		indexOfSearchTerm++;}
//			    		catch(Exception e){
//			    			e.printStackTrace();
//			    			break;
//			    		}
//			    	
//			    }
//			    snipIncludingInput+=para.substring(para.indexOf(input)+input.length(), indexOfSearchTerm);
//				captionString+=time+"\t"+snipIncludingInput+"\n";
//			}
//			
//		}
//		    System.out.println(captionString);
//		
//		
//	/*	Matcher m=p.matcher(transcriptValue);
//    	String captionString="";
//    	while(m.find()){
//			char a, b,c,d,e;
//    		String sentence=m.group();
//    		System.out.println(sentence);
//    		String dummyX=transcriptValue;
//    		StringBuilder timeWhereWordOccurs = new StringBuilder();
//    		String dummyXSplit[]=dummyX.split(sentence);
//    		for(int i=0;i<dummyXSplit[0].length();i++){
//    		
//    		a=dummyXSplit[0].charAt(i);
//    		if(a>47 && a<59){
//    			
//    			
//    		    
//    			b=dummyXSplit[0].charAt(i+1);
//    			if(b>47 && b<59){
//    				
//    		    c=dummyXSplit[0].charAt(i+2);
//    		    if(c>47 && c<59){
//    				
//    		    d=dummyXSplit[0].charAt(i+3);
//    		    if(d>47 && d<59){
//    		    	timeWhereWordOccurs.delete(0,timeWhereWordOccurs.length());
//    		    	timeWhereWordOccurs.append(a);
//    		    	timeWhereWordOccurs.append(b);
//    		    	timeWhereWordOccurs.append(c);
//    		    	timeWhereWordOccurs.append(d);
//    		    e=dummyXSplit[0].charAt(i+4);
//    		    if(e>47 && e<58){
//    		    	
//    		    	
//    		    	timeWhereWordOccurs.append(e);
//    		    }
//    		   
//    		    }
//    		    }
//    		    }
//    		}
//    		 i=i+4;
//    		
//    	}
//    		
//                  captionString+=timeWhereWordOccurs+" "+sentence+"\n";
//                  System.out.println(captionString);
//    	
//		} 
//		
//    	if(captionString!="")
//		return (captionString);
//    	else
//    	return(""); */
//		
//	}

}}
