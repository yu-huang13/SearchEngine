import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by HY on 16/11/4.
 */
public class SearchEngineTest {

    SearchEngine searchEngine;

    @Before
    public void setUp() throws Exception {
        searchEngine = new SearchEngine();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testHandleArgs() throws Exception {

        String[] args = {"Main.java", "-i", "data/index", "-s", "data/source.txt", "-m", "append"};
        searchEngine.handleArgs(args);
        assertEquals(searchEngine.indexDir, "data/index");
        assertEquals(searchEngine.indexMode, IndexWriterConfig.OpenMode.APPEND);
        assertEquals(searchEngine.sourceDir, "data/source.txt");

//        String[] argsHelp = {"Main.java", "-h"};
//        searchEngine.handleArgs(argsHelp);
    }

    @Test
    public void testRun() throws Exception {

    }
}