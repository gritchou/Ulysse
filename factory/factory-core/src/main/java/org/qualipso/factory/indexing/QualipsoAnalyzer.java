package org.qualipso.factory.indexing;
/**
 * <p>Class which extends a Lucene class used for preparing text for indexing.
 * An Analyzer builds TokenStreams, which analyze text. 
 * It thus represents a policy for extracting index terms from text.
 * Typical implementations first build a Tokenizer, 
 * which breaks the stream of characters from the Reader into raw Tokens.
 * One or more TokenFilters may then be applied to the output of the Tokenizer.</p>
 * @author Benjamin Dreux(benjiiiiii@gmail.com)
 * @author cynthia FLORENTIN 
 * @date 22 nov 2009
 * */
import java.io.Reader;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.TokenStream;

public class QualipsoAnalyzer extends Analyzer {

	/**
	 * <p>
	 * Method which build a tokenStream. 
	 * Creates a TokenStream which tokenizes all the text in the provided Reader. 
	 * </p>
	 * @param String fieldName
	 * @param Reader reader
	 * @return TokenStream
	 */
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {		
		TokenStream ts = new LowerCaseTokenizer(reader);
		return new ASCIIFoldingFilter(ts);   
	}

}
