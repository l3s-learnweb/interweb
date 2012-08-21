package de.l3s.interwebj.connector.facebook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class Lucene {
	
	private Directory index;
	public IndexWriter getWriter() {
		return writer;
	}
	public void setWriter(IndexWriter writer) {
		this.writer = writer;
	}
	private IndexWriter writer;
	
	public Lucene(Boolean RAM, File path) {
		
		
		try {
			index = (RAM)? new RAMDirectory(): FSDirectory.open(path);
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
			writer= new IndexWriter(index, config);
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
	public ArrayList<Document> searchIndex(Query query)
	{
		int hitsPerPage = 10;
		IndexReader reader = null;
		try {
			reader = IndexReader.open(index);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
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
		    Document d = null;
			try {
				d = searcher.doc(docId);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    System.out.println((i + 1) + ". " + d.get("title"));
		    results.add(d);
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
