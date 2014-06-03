package de.l3s.interwebj.connector.ted;

import java.util.ArrayList;
import java.util.List;

public class SnippetTest {
	
	public static void main(String args[]){
		String transcriptValue="0:12\thello this is a tes for cello\n 7:88\t hello i am here to test cello\n 9:77\t jkgdfukwgkjfgwkgfvu\n";
		String input="cello";
		int flag=0; //flag : to decide to add to the list of time or paragraphs
		String captionString="";
		String snipIncludingInput = "";
		
		String dummyValue=transcriptValue;
		String[] transcriptParts=dummyValue.split("\\t|\\n");
		List<String> timeVals=new ArrayList<String>();
		List<String> paraVals=new ArrayList<String>();
		
		for(String str:transcriptParts){
			if(flag==0){
				flag=1;
				timeVals.add(str);
			}
			else{
				flag=0;
				paraVals.add(str);
			}
				
		}
		
		for(String para: paraVals){
		System.out.println(para);
			if(para.contains(input)){
				int numberOfWhitespaces=0;
				int index=paraVals.indexOf(para);
				int lastIndex=para.length();
				String time=timeVals.get(index); // to get corresponding value stored in the list TimeVals
			    
				int indexOfSearchTerm=para.indexOf(input);
				  // to obtain 5 words before the input keyword 
			    while(numberOfWhitespaces<6 && indexOfSearchTerm>=index){
			    	if(Character.isWhitespace(para.charAt(indexOfSearchTerm))){
			    		numberOfWhitespaces++;
			    	}
			    	
			    		try{
			    		indexOfSearchTerm--;}
			    		catch(Exception e){
			    			e.printStackTrace();
			    			break;
			    		}
			    	
			    }
			    snipIncludingInput+=para.substring(++indexOfSearchTerm, para.indexOf(input))+input;
			    numberOfWhitespaces=0;
			    indexOfSearchTerm=para.indexOf(input);
			    // to obtain 5 words after the input keyword 
			    while(numberOfWhitespaces<6 && indexOfSearchTerm<lastIndex){
			    	if(Character.isWhitespace(para.charAt(indexOfSearchTerm))){
			    		numberOfWhitespaces++;
			    	}
			    	
			    		try{
			    		indexOfSearchTerm++;}
			    		catch(Exception e){
			    			e.printStackTrace();
			    			break;
			    		}
			    	
			    }
			    snipIncludingInput+=para.substring(para.indexOf(input)+input.length(), indexOfSearchTerm);
				captionString+=time+"\t"+snipIncludingInput+"\n";
			}
			
		}
		    System.out.println(captionString);
		
		
	/*	Matcher m=p.matcher(transcriptValue);
    	String captionString="";
    	while(m.find()){
			char a, b,c,d,e;
    		String sentence=m.group();
    		System.out.println(sentence);
    		String dummyX=transcriptValue;
    		StringBuilder timeWhereWordOccurs = new StringBuilder();
    		String dummyXSplit[]=dummyX.split(sentence);
    		for(int i=0;i<dummyXSplit[0].length();i++){
    		
    		a=dummyXSplit[0].charAt(i);
    		if(a>47 && a<59){
    			
    			
    		    
    			b=dummyXSplit[0].charAt(i+1);
    			if(b>47 && b<59){
    				
    		    c=dummyXSplit[0].charAt(i+2);
    		    if(c>47 && c<59){
    				
    		    d=dummyXSplit[0].charAt(i+3);
    		    if(d>47 && d<59){
    		    	timeWhereWordOccurs.delete(0,timeWhereWordOccurs.length());
    		    	timeWhereWordOccurs.append(a);
    		    	timeWhereWordOccurs.append(b);
    		    	timeWhereWordOccurs.append(c);
    		    	timeWhereWordOccurs.append(d);
    		    e=dummyXSplit[0].charAt(i+4);
    		    if(e>47 && e<58){
    		    	
    		    	
    		    	timeWhereWordOccurs.append(e);
    		    }
    		   
    		    }
    		    }
    		    }
    		}
    		 i=i+4;
    		
    	}
    		
                  captionString+=timeWhereWordOccurs+" "+sentence+"\n";
                  System.out.println(captionString);
    	
		} 
		
    	if(captionString!="")
		return (captionString);
    	else
    	return(""); */
		
	}

}
