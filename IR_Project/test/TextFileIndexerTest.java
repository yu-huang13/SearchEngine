import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by HY on 16/11/3.
 */
public class TextFileIndexerTest {
    TextFileIndexer indexer;

    SearchEngine searchEngine;

    @Before
    public void setUp() throws Exception {
        System.out.println("before");
        searchEngine = new SearchEngine();
        searchEngine.sourceDir = "/Users/apple/Documents/coding/4up/IR/big_project/small_CNKI.txt";
        searchEngine.indexDir = "/Users/apple/Documents/coding/4up/IR/big_project/IR_Project/data/index";
        searchEngine.analyzer = new StandardAnalyzer();
        searchEngine.indexMode = IndexWriterConfig.OpenMode.CREATE;
        indexer = new TextFileIndexer(searchEngine);
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("After");
    }

    @Test
    public void testCreateIndex() throws Exception{
        indexer.createIndex();
    }

    @Test
    public void testReadFile() throws Exception {
        indexer.readFile(searchEngine.sourceDir);
    }

    @Test
    public void testHandleLine() throws Exception {
        Document doc = new Document();

        indexer.handleLine(doc, "<题名>=基坑开挖对预应力管桩的影响");
        indexer.handleLine(doc, "<英文作者>=YU Xu,ZHANG Yu (Department of Civil Engineering of Anhui University of Architecture,Hefei 230022,China)");
        indexer.handleLine(doc, "< 作者 >  =  余旭;张宇;");

        for (IndexableField field: doc.getFields()){
            System.out.println(field);
        }

    }
}