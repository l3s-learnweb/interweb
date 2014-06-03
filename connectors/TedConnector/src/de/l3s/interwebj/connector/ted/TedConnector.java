package de.l3s.interwebj.connector.ted;

import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.socialsearch.SocialSearchQuery;
import de.l3s.interwebj.socialsearch.SocialSearchResult;

public class TedConnector extends AbstractServiceConnector {
	
	public TedConnector() {
		super();
	}
	
	public TedConnector(Configuration config) {
		super(config);
	}
	
	

	@Override
	public Parameters authenticate(String callbackUrl) throws InterWebException {
		return null;
	}

	@Override
	public AuthCredentials completeAuthentication(Parameters params) throws InterWebException {
		return null;
	}
	
	@Override
	public boolean isRegistered() {
		return true;
	}
	
	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials) throws InterWebException 
	{		
		notNull(query, "query");
		
		QueryResult qr = new QueryResult(query);
		query.getQuery();
		query.getPage();
		query.getResultCount();
		
		TedClient tc = new TedClient();
		if(query.getParams().get("enabledTitle")=="1")
		    tc.setEnableSearchonTitle(true);
		if(query.getParams().get("enabledDescription")=="1")
			tc.setEnableSearchonTitle(true);
		if(query.getParams().get("enabledTranscripts")=="1")
			tc.setEnableSearchonTranscripts(true);
		//query.getParams().get("langOfTheTranscript");
		
		Pattern pattern=tc.createPattern(query);
		HashMap<String,HashMap<String,String>> hmap=tc.search(query.getQuery(),query.getPage(),query.getResultCount());
		Iterator<String> keySetIterator = hmap.keySet().iterator();
		
		int index=0;
		while(keySetIterator.hasNext()){
			String tedIdKey = keySetIterator.next();
			HashMap<String,String> valueMap = hmap.get(tedIdKey);
			qr.addResultItem(convertWebResult(tedIdKey,valueMap, index,pattern,query.getQuery()));
			
	    	index++;
		}

		return qr;
	}

	@Override
	public UserSocialNetworkResult getUserSocialNetwork(String userid,
			AuthCredentials authCredentials) throws InterWebException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmbedded(AuthCredentials authCredentials, String url,
			int width, int height) throws InterWebException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserId(AuthCredentials userAuthCredentials)
			throws InterWebException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnectorRegistrationDataRequired() {
		return false;
	}

	@Override
	public boolean isUserRegistrationDataRequired() {
		return false;
	}

	@Override
	public boolean isUserRegistrationRequired() {
		return false;
	}

	@Override
	public ResultItem put(byte[] data, String contentType, Parameters params,
			AuthCredentials authCredentials) throws InterWebException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void revokeAuthentication() throws InterWebException {
	
		
	}

	@Override
	public Set<String> getTags(String username, int maxCount)
			throws IllegalArgumentException, IOException {
		return null;
	}

	@Override
	public Set<String> getUsers(Set<String> tags, int maxCount)
			throws IOException, InterWebException {
		return null;
	}

	@Override
	public SocialSearchResult get(SocialSearchQuery query,
			AuthCredentials authCredentials) {
		return null;
	}

	@Override
	public ServiceConnector clone() {
		TedConnector test = new TedConnector(getConfiguration());
		return test;
	}
	
	
	private ResultItem convertWebResult(String tedIdkey,HashMap<String,String> valueMap,int index, Pattern pattern,String input)
	{
		String speaker=valueMap.get("speaker").split("\\^")[0];
		String location=valueMap.get("location").split("\\^")[0];
		String duration=valueMap.get("duration").split("\\^")[0];
		duration=duration.trim();
		String durationArray[] =duration.split(":");
		int min=Integer.parseInt(durationArray[0]);
		int sec=Integer.parseInt(durationArray[1]);
		int dur=(min*60)+sec;
		String description=valueMap.get("description").split("\\^")[0];
		description=description.replaceAll("(?i)"+input, "<b><i>"+input+"</i></b>");
		String snippet="";		
		String additionalDescription=null; //for adding link to the transcripts ,speaker and location
	    String captionString="";		
		
		Iterator<String> keyIterator=valueMap.keySet().iterator();
		while(keyIterator.hasNext())
		{
			String key=keyIterator.next();
			if(key.startsWith("http") && key.contains("?lang=en"))
			{
				String val=valueMap.get(key);
				String snip= timeWherePhraseOccurs(val, input);
				
				snippet=snip;
				if(snip!= "")
			    captionString+="Link to the transcript-"+key+"\n"+snip+"\n";
				
				}
		}
		captionString=captionString.replaceAll("(?i)"+input, "<b><i>"+input+"</i></b>");
		additionalDescription="Speaker-"+speaker+"\n"+"Location-"+location+"\n"+"Duration of The video-"+duration+"\n"+captionString;
		additionalDescription=additionalDescription.replaceAll("(?i)"+input, "<b><i>"+input+"</i></b>");
		description+="\n"+additionalDescription;
		Thumbnail tn =new Thumbnail(valueMap.get("thumbnail"),400,300);
		Set<Thumbnail> thumbnails = new HashSet<Thumbnail>();
		thumbnails.add(tn);
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(Query.CT_VIDEO);
		resultItem.setUrl(valueMap.get("talk").split("\\^")[0]);
		resultItem.setTitle(valueMap.get("title").split("\\^")[0]);
		resultItem.setDescription(description);
		resultItem.setSnippet(snippet);
		resultItem.setDuration(dur);
    	resultItem.setTags(valueMap.get("keywords").split("\\^")[0]);
		resultItem.setThumbnails(thumbnails);
    	resultItem.setDate(valueMap.get("date").split("\\^")[0]);
		resultItem.setViewCount(Integer.parseInt(valueMap.get("totalviews").split("\\^")[0]));
		resultItem.setRank(index);
		resultItem.setEmbeddedSize3("<iframe src=\"http://embed.ted.com/talks/"+valueMap.get("id2").split("\\^")[0]+".html" +"\" width=\"500\" height=\"282\" frameborder=\"0\" scrolling=\"no\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>");	
		resultItem.setEmbeddedSize4("<iframe src=\"http://embed.ted.com/talks/"+ valueMap.get("id2").split("\\^")[0] +".html" +"\" width=\"100%\" height=\"100%\" frameborder=\"0\" scrolling=\"no\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>");	
//		System.out.println(valueMap.get("id2").split("\\^")[0]);
//		System.out.println("URL of the talk"+"\n");
//		System.out.println(resultItem.getUrl()+"\n");
//		System.out.println("Title"+"\n");
//		System.out.println(resultItem.getTitle()+"\n");
//		System.out.println("Description"+"\n");
//		System.out.println(resultItem.getDescription()+"\n");
//		System.out.println("Snippet"+"\n");
//		System.out.println(resultItem.getSnippet()+"\n");
//		System.out.println("Tags"+"\n");
//		System.out.println(resultItem.getTags()+"\n");
//		System.out.println("Date"+"\n");
//		System.out.println(resultItem.getDate()+"\n");
//		System.out.println("Total Views"+"\n");
//		System.out.println(resultItem.getViewCount()+"\n");
		return resultItem;
	}
	
	//function to extract the snippet
	private String timeWherePhraseOccurs(String transcriptValue, String input){
		String snip;
	
	Pattern p= Pattern.compile((input), Pattern.CASE_INSENSITIVE);
	
	Matcher m=p.matcher(transcriptValue);
	int lastIndex1 = 0;
	int beginIndex=0;
	String captionString="";
	while(m.find()){
		
		//System.out.println(m.group());
		lastIndex1= transcriptValue.toLowerCase().indexOf(input.toLowerCase(),lastIndex1);

	       if( lastIndex1 == -1){
	       lastIndex1=transcriptValue.toLowerCase().indexOf(input.toLowerCase());
	       }
	    	   char a, b,c,d,e;
	    		String dummyX=transcriptValue;
	    		StringBuilder timeWhereWordOccurs = new StringBuilder();
	    		
	    		String dummyXSplit=dummyX.substring(beginIndex, lastIndex1);
	    		for(int i=0;i<dummyXSplit.length();i++){
	    		
	    		a=dummyXSplit.charAt(i);
	    		if(a>47 && a<59){
	    			
	    			
	    		    
	    			b=dummyXSplit.charAt(i+1);
	    			if(b>47 && b<59){
	    				
	    		    c=dummyXSplit.charAt(i+2);
	    		    if(c>47 && c<59){
	    				
	    		    d=dummyXSplit.charAt(i+3);
	    		    if(d>47 && d<59){
	    		    	timeWhereWordOccurs.delete(0,timeWhereWordOccurs.length());
	    		    	timeWhereWordOccurs.append(a);
	    		    	timeWhereWordOccurs.append(b);
	    		    	timeWhereWordOccurs.append(c);
	    		    	timeWhereWordOccurs.append(d);
	    		    e=dummyXSplit.charAt(i+4);
	    		    if(e>47 && e<58){
	    		    	
	    		    	
	    		    	timeWhereWordOccurs.append(e);
	    		    }
	    		   
	    		    }
	    		    }
	    		    }
	    			 i=i+4;
	    		}
	    		
	    		
	    	}
	             lastIndex1+=input.length();
	             
	             snip=generateSnippet(lastIndex1,transcriptValue,input);
	             captionString+=timeWhereWordOccurs+"\t"+snip+"\n";
	
	
	       
	}
	        
              return captionString;
	}
	
	
	public String generateSnippet(int index,String transcriptValue,String input){
		
		
		int numberOfWhitespaces=0;
		
		int lastIndex=transcriptValue.length();
		
		String snipIncludingInput = "";
		int indexOfSearchTerm=index;
		  // to obtain 5 words before the input keyword 
	    while(numberOfWhitespaces<6 && indexOfSearchTerm>=0){
	    	if(Character.isWhitespace(transcriptValue.charAt(indexOfSearchTerm))){
	    		numberOfWhitespaces++;
	    	}
	    	
	    		try{
	    		indexOfSearchTerm--;}
	    		catch(Exception e){
	    			e.printStackTrace();
	    			break;
	    		}
	    	
	    }
	    snipIncludingInput+=transcriptValue.substring(++indexOfSearchTerm, index)+" ";
	    numberOfWhitespaces=0;
	    indexOfSearchTerm=transcriptValue.toLowerCase().indexOf(input.toLowerCase());
	    // to obtain 5 words after the input keyword 
	    while(numberOfWhitespaces<6 && indexOfSearchTerm<lastIndex){
	    	if(Character.isWhitespace(transcriptValue.charAt(indexOfSearchTerm))){
	    		numberOfWhitespaces++;
	    	}
	    	
	    		try{
	    		indexOfSearchTerm++;}
	    		catch(Exception e){
	    			e.printStackTrace();
	    			break;
	    		}
	    	
	    }
	    snipIncludingInput+=transcriptValue.substring(transcriptValue.toLowerCase().indexOf(input.toLowerCase())+input.length(), indexOfSearchTerm);
	     
	     
	
	

	return snipIncludingInput;
	
}


	/*public static void main(String args[]){
=======
	
	public static void main(String args[]){
>>>>>>> .r2859
		String input1;
		Scanner scan= new Scanner(System.in);
		System.out.println("Enter search phrase");
		input1=scan.next();
		List<String> sample =new ArrayList<String>();
		sample.add("video");
		Map<String,String> sampleMap=new HashMap<String,String>();
		sampleMap.put("key1", "value1");
		Query query=new Query(input1, input1,sample, sampleMap);
		try {
			TedConnector tcon=new TedConnector();
			QueryResult qresult = tcon.get(query,null);
			System.out.println(qresult);
		} catch (InterWebException e) {
			e.printStackTrace();
		}
<<<<<<< .mine
	} */

	


}
