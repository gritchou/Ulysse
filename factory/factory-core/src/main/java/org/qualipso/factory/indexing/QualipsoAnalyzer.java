package org.qualipso.factory.indexing;
/**
 * @author Benjamin Dreux(benjiiiiii@gmail.com)
 * @date 02 nov 2009
 * */
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ISOLatin1AccentFilter;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class QualipsoAnalyzer extends Analyzer {

	@SuppressWarnings("deprecation")
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		
		
		TokenStream ts = new LowerCaseTokenizer(reader);
		return  new ISOLatin1AccentFilter(ts);
        
	}

}
