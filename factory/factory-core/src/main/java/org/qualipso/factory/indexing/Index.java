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
import java.io.StringReader;
import java.util.ArrayList;

import org.qualipso.factory.FactoryResourceIdentifier;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

@SuppressWarnings("deprecation")
public class Index implements IndexI {
	private static Index instance;
	private static String indexFolderName = "data/index/";
	private Directory indexDirectory;
	private Analyzer analyzer;
	private IndexWriter writer;

	private Index() throws Exception {
		File indexDir = new File(indexFolderName);
		analyzer = new QualipsoAnalyzer();
		if (indexDir.exists() && indexDir.isDirectory()) {
			indexDirectory = FSDirectory.getDirectory(indexFolderName);
		} else {
			indexDir.mkdirs();
			indexDirectory = FSDirectory.getDirectory(indexFolderName);

			// Build the index
			writer = new IndexWriter(indexDirectory, analyzer, true);
			writer.close();
		}
	}

	public static Index getInstance() throws IndexingServiceException {
		if (instance == null) {
			try {
				instance = new Index();
			} catch (Exception e) {
				throw new IndexingServiceException(
						"Can't get instance of index class");
			}
		}
		return instance;
	}

	@Override
	public void index(IndexableDocument doc) throws IndexingServiceException {
		try {
			synchronized (this) {
				IndexWriter writer = null;

				try {
					writer = new IndexWriter(indexDirectory, analyzer, false);
					writer.addDocument(doc.getDocument());
					writer.optimize();
				} finally {
					writer.close();
				}
			}
		} catch (IOException e) {
			throw new IndexingServiceException("Can't index a document", e);
		}

	}

	@Override
	public void reindex(FactoryResourceIdentifier fri, IndexableDocument doc)
			throws IndexingServiceException {
		remove(fri);
		index(doc);

	}

	@Override
	public void remove(FactoryResourceIdentifier fri)
			throws IndexingServiceException {
		try {
			synchronized (this) {
				Term term = new Term("FRI", fri.toString());
				IndexReader reader = IndexReader.open(indexDirectory);
				reader.deleteDocuments(term);
				reader.close();
			}
		} catch (IOException e) {
			throw new IndexingServiceException("Can't remove a document", e);
		}

	}

	@Override
	public ArrayList<SearchResult> search(String queryString)
			throws IndexingServiceException {
		try {

			QueryParser queryParser = new QueryParser("CONTENT", analyzer);
			queryParser.parse(queryString);

			Query query = queryParser.parse(queryString);

			Searcher searcher = new IndexSearcher(indexDirectory);
			IndexReader reader = IndexReader.open(indexDirectory);

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
			throw new IndexingServiceException("Can't search " + queryString
					+ "\n", e);
		}

	}

}
