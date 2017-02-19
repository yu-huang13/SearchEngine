import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by HY on 16/11/4.
 */
public class TextSearcherTest {

    SearchEngine searchEngine;
    TextSearcher searcher;

    @Before
    public void setUp() throws Exception {
        searchEngine = new SearchEngine();
        searchEngine.indexDir = "/Users/apple/Documents/coding/4up/IR/big_project/IR_Project/data/index";
        searchEngine.analyzer = new StandardAnalyzer();

        searcher = new TextSearcher(searchEngine);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testQuery() throws Exception {
        String input;

        input = "Foundation pit";
        System.out.println("input: " + input);
        outputDocs(searcher.query(input));

        input = "aseismatic performance";
        System.out.println("input: " + input);
        outputDocs(searcher.query(input));

        input = "SGJS201305009";
        System.out.println("input: " + input);
        outputDocs(searcher.query(input));
    }

    @Test
    public void testValidQry() throws Exception{
        String[] inputList = new String[] {"机器学习", "张海新", "", " ", "\n", "      "};
        boolean[] result = new boolean[] {true, true, false, false, false, false};
        for (int i = 0; i < inputList.length; ++i){
            System.out.println(searcher.validQry(inputList[i]));
            assertEquals(result[i], searcher.validQry(inputList[i]));
        }
    }

    void outputDocs(Document[] docs){
        System.out.println("results:");
        for (Document doc: docs){
            System.out.println("题名:" + doc.get("题名"));
        }
        System.out.println("");
    }




}