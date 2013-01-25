package l3s.interwebj.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class Lucene {
	
	private Directory index;
	 
	public IndexWriter getWriter() {
		
		if (writer==null)
			try {
				StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
				IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
				writer= new IndexWriter(index, config);
				
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LockObtainFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return writer;
	}
	public void setWriter(IndexWriter writer) {
		this.writer = writer;
	}
	private IndexWriter writer;
	
	public Lucene(Boolean RAM, File path) {
		
		
		try {
			index = (RAM)? new RAMDirectory(): FSDirectory.open(path);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public void addDocToIndex(Document doc)
	{
		try {
			writer.addDocument(doc);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//clase the index writer after return
	}
	public void addDocsToIndex(List<Document> docs)
	{
		for(Document doc: docs)
			{
				try {
					writer.addDocument(doc);
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		try {
			writer.forceMerge(10);
			writer.close(true);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ArrayList<Document> searchIndex(String querystr, String[] fields, int hitsperpage)
	{
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		Query query = null;
		IndexSearcher searcher = null;
		MultiFieldQueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36,
                fields,
                analyzer);
		queryParser.setDefaultOperator(Operator.AND);
		try {
			query=queryParser.parse(querystr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			IndexReader reader = IndexReader.open(index);
			searcher = new IndexSearcher(reader);
		} catch (CorruptIndexException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsperpage, true);
		try {
			
			searcher.search(query, collector);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		ArrayList<Document> results= new ArrayList<Document>();
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;++i) {
		    int docId = hits[i].doc;
		    float score =hits[i].score;
		    Document d = null;
			try {
				d = searcher.doc(docId);
				d.add(new Field("score", Float.toString(score), Field.Store.NO, Field.Index.NOT_ANALYZED));
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    System.out.println((i + 1) + ". " + d.get("title")+ ". user: " +d.get("user name")+ "   user-  "+d.get("user")+ " id"+d.get("id"));
		    results.add(d);
		}
		try {
			searcher.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}
	
	
	
	
	public Directory getIndex() {
		return index;
	}
	public void setIndex(Directory index) {
		this.index = index;
	}

}
