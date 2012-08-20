package de.l3s.interwebj.connector.facebook;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class LuceneTest {
	public static void main(String[] args) {
		String path= "C:\\Users\\singh\\workspaceinterweb\\FacebookConnector\\fbindex";
		Lucene phototaggedbase= new Lucene(false, new File(path+"\\photostaggedin"));
		File t = new File("/FacebookConnector/fbindex/eventsbase.txt");
		System.out.println(t.isFile());
		Lucene eventsbase= new Lucene(false, new File("/FacebookConnector/fbindex/eventsbase.txt"));
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		IndexWriter taggedphotoswriter = null;
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
		 try {
		 taggedphotoswriter = new IndexWriter(phototaggedbase.getIndex(), config);
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
		 
		 Document doc= new Document();
		Field field = new Field("type", "test", Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field = new Field("id", "1", Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		try {
			taggedphotoswriter.addDocument(doc);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			taggedphotoswriter.forceMerge(10);
			taggedphotoswriter.close(true);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
