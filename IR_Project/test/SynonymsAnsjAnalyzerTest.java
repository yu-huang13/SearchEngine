import org.apache.lucene.analysis.Analyzer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by HY on 16/12/26.
 */
public class SynonymsAnsjAnalyzerTest {
    Word2VEC word2vec;
    Analyzer analyzer;

    @Before
    public void setUp() throws Exception {
        word2vec = new Word2VEC();
        word2vec.loadModel("data/model/word2vec/vectors_s200_w8_iter15.bin");

        analyzer = new SynonymsAnsjAnalyzer(word2vec);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAnalyzer() throws Exception{
        String text = "电脑";
        AnalyzerUtils.displayTokens(analyzer, text);
    }



}
