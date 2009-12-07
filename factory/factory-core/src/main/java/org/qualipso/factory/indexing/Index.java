package org.qualipso.factory.indexing;

/**
 * <p>A class which makes the logic of the indexation and the search.
 * It is the only object which will have a coupling with Luccent.
 * Class implements IndexI</p>
 * @author Benjamin Dreux(benjiiiiii@gmail.com)
 * @author Cynthia FLORENTIN
 * @date 28 oct 2009
 * */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;

public class Index implements IndexI {
	private static Index instance;
	private static String indexFolderName = "data/index/";
	//private Directory indexDirectory;
	private File indexDir;
	private Analyzer analyzer;
	private IndexWriter writer;
	private static Log logger = LogFactory.getLog(Index.class);
	
	private Index() throws Exception {
	
		indexDir = new File(indexFolderName);
		analyzer = new QualipsoAnalyzer();
		/*
		if (indexDir.exists() && indexDir.isDirectory()) {
			indexDirectory = FSDirectory.getDirectory(indexFolderName);
		} else {
			indexDir.mkdirs();
			indexDirectory = FSDirectory.getDirectory(indexFolderName);

			// Build the index
			writer = new IndexWriter(indexDirectory, analyzer, true);
			writer.close();
		}*/
		if (indexDir.exists()) {
			 writer = new IndexWriter(FSDirectory.open(indexDir), analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
	     }else{
	    	 writer = new IndexWriter(FSDirectory.open(indexDir), analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
	     }
	}

	public static Index getInstance() throws IndexingServiceException {
		if (instance == null) {
			try {
				instance = new Index();
			} catch (Exception e) {
				logger.error("can't get instance of index " , e);
				throw new IndexingServiceException("Can't get instance of index");
			}
		}
		return instance;
	}

	@Override
	public void index(IndexableDocument doc) throws IndexingServiceException {
	logger.info("index(...) called");
    logger.debug("params : IndexableDocument=" + doc);
		try {
			synchronized (this) {
				//IndexWriter writer = null;

				try {
					//writer = new IndexWriter(indexDirectory, analyzer, false);
					writer.addDocument(doc.getDocument());
					writer.optimize();
				} finally {
					writer.close();
				}
			}
		} catch (IOException e) {
			logger.error("unable to index document " + doc, e);
			throw new IndexingServiceException("Can't index a document", e);
		}

	}

	@Override
	public void reindex(String path, IndexableDocument doc) throws IndexingServiceException {
   	logger.info("reindex(...) called");
    logger.debug("params :path=" + path +" doc="+doc);
		remove(path);
		index(doc);

	}

	@Override
	public void remove(String path) throws IndexingServiceException {
	logger.info("remove(...) called");
    logger.debug("params :path=" + path);
		try {
			synchronized (this) {
				Term term = new Term("path", path);
				//IndexReader reader = IndexReader.open(indexDirectory);
				IndexReader reader = IndexReader.open(FSDirectory.open(indexDir), true);
				reader.deleteDocuments(term);
				reader.close();
			}
		} catch (IOException e) {
			logger.error("unable to remove resource " + path +" from index", e);
			throw new IndexingServiceException("Can't remove resource" + path +" from index", e);
		}

	}

	@Override
	public ArrayList<SearchResult> search(String queryString)
			throws IndexingServiceException {
		try {

			QueryParser queryParser = new QueryParser("CONTENT", analyzer);
			queryParser.parse(queryString);

			Query query = queryParser.parse(queryString);

			//Searcher searcher = new IndexSearcher(indexDirectory);
			//IndexReader reader = IndexReader.open(indexDirectory);
			IndexReader reader = IndexReader.open(FSDirectory.open(indexDir), true); 
            IndexSearcher searcher=new IndexSearcher(reader);

			Hits hits = searcher.search(query);

			ArrayList<SearchResult> listRs = new ArrayList<SearchResult>(hits
					.length());
			SimpleHTMLFormatter formatter = new SimpleHTMLFormatter(
					"<highlighted>", "</highlighted>");
			QueryScorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(formatter, scorer);

			for (int i = 0; i < hits.length(); i++) {
				String fri = hits.doc(i).get("FRI");
				float score = hits.score(i);
				String higlighteText = highlighter.getBestFragment(analyzer,
						"CONTENT", hits.doc(i).get("CONTENT"));
				String name = hits.doc(i).get("NAME");
				String type = hits.doc(i).get("SERVICE") + "/"
						+ hits.doc(i).get("TYPE");
				SearchResult sr = new SearchResult();
				sr.setScore(score);
				sr.setName(name);
				sr.setIdentifier(fri);

				sr.setExplain(higlighteText);
				sr.setType(type);

				listRs.add(sr);

			}
			reader.close();

			searcher.close();

			return listRs;
		} catch (Exception e) {
			logger.error("unable search in index using " + queryString, e);
			throw new IndexingServiceException("Can't search in index using '" + queryString+ "'\n", e);
		}

	}

}
