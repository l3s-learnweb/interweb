package de.l3s.interwebj.bean;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import l3s.interwebj.lucene.Lucene;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermFreqVector;

import com.mysql.jdbc.Connection;
import com.sun.istack.internal.NotNull;

import de.l3s.interwebj.core.Engine;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.InterWebPrincipal;
import de.l3s.interwebj.query.UserSocialNetworkCollector;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.webutil.FacesUtils;


@ManagedBean
@ViewScoped
public class SocialNetworkBean
    
{
	
	
	
	@NotNull
	private String userid;
	
	UserSocialNetworkResult result;
	
	public UserSocialNetworkResult getResult() {
		return result;
	}





	public void setResult(UserSocialNetworkResult result) {
		this.result = result;
	}





	public String getUserid() {
		return userid;
	}





	public void setUserid(String userid) {
		this.userid = userid;
	}





	public SocialNetworkBean()
	{
		init();
	}
	

	
	

	public void init()
	{
		Engine engine = Environment.getInstance().getEngine();
		
	}
	

	public void save()
	{
	}
	public void saveTerms() throws SQLException
	{
		//String path= "C:\\Users\\singh\\FacebookIndex\\"+"test";
		String path= "/home/singh/learnweb/FacebookIndex/"+"crawltestfinalwithlinks2";
		System.out.println(userid);
		File gg= new File(path);
		Lucene index= new Lucene(false, gg);
		HashSet<String> usertermset= new HashSet<String>();
		try {
			IndexReader reader= IndexReader.open(index.getIndex());
			Term term= new Term("user", userid.trim());
			TermDocs docs = reader.termDocs(term);
			while(docs.next())
			{
				TermFreqVector[] vecs = reader.getTermFreqVectors(docs.doc());
				for(TermFreqVector v: vecs)
				{
					String[] terms = v.getTerms();
					for(String t: terms)
						usertermset.add(t);
				}
			}
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("phase 1: selecting the terms");
		HashSet<String> uterms= new HashSet<String>();
		ResultSet rs =null;
		try {
			System.out.println("connecting to DB.....");
			java.sql.Connection dbConnection = DriverManager.getConnection("jdbc:mysql://mysql.l3s.uni-hannover.de/flickrcrawl?characterEncoding=utf8",
			       "zerr",
			        "aGSbXmmJuseznzm7");
			System.out.println("querying db");
			PreparedStatement queryselect = dbConnection.prepareStatement("SELECT term FROM google_unique_terms WHERE 1");
			rs = queryselect.executeQuery();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(rs.next())
		{
			uterms.add(rs.getString(1));
		}
		uterms.retainAll(usertermset);
		//writeCSV(uterms, userid);
		System.out.println("phase 2: selecting the queries");
		try {
			System.out.println("connecting to DB.....");
			java.sql.Connection dbConnection = DriverManager.getConnection("jdbc:mysql://mysql.l3s.uni-hannover.de/flickrcrawl?characterEncoding=utf8",
			       "zerr",
			        "aGSbXmmJuseznzm7");
			System.out.println("querying db");
			PreparedStatement queryselect = dbConnection.prepareStatement("SELECT query FROM study_queryset WHERE 1");
			rs = queryselect.executeQuery();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashSet<String> queriesselected= new HashSet<String>();
		while(rs.next())
		{
			String[] words = rs.getString(1).split(" ");
			Boolean usethisquery=true;
			for(String word: words)
			{
				usethisquery=uterms.contains(word);
				if(!usethisquery)
					break;
			}
			if(!usethisquery)
				continue;
			queriesselected.add(rs.getString(1));
		}
		writeCSV(queriesselected, userid+"q");
		System.out.println("done");
	}
	
	private static void writeCSV(HashSet<String> rows, String userid)
	{
		
		
		File fbq= new File("/home/singh/learnweb/"+userid+"terms.csv");
		
			
		
		try {
			System.out.println("write to file.....");
			writeBuffered(rows, 8192, fbq);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
	}
	

	private static void writeBuffered(HashSet<String> records, int bufSize,
			File file) throws IOException {
		try {
	        FileWriter writer = new FileWriter(file);
	        BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);

	        System.out.print("Writing buffered (buffer size: " + bufSize + ")... ");
	        write(records, bufferedWriter);
	    } finally {
	    	
	      System.out.println("written to file.");
	    }
		
	}

	private static void write(HashSet<String> records,
			BufferedWriter writer) throws IOException 
	{
		long start = System.currentTimeMillis();
		Iterator<String> recs = records.iterator();
		while(recs.hasNext())
		{
			writer.write(recs.next()+"\r\n");
			
		}
	   
	    writer.flush();
	    writer.close();
	    long end = System.currentTimeMillis();
	    System.out.println((end - start) / 1000f + " seconds");
	}
	
	public String search()
	{
		
		Engine engine = Environment.getInstance().getEngine();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		
		try
		{
			UserSocialNetworkCollector collector = engine.getSocialNetworkOf("me", principal, "Facebook");
			//result = collector.retrieve();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
		}
		
		
		return "success";
	}
	

}
