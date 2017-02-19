import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by HY on 16/11/3.
 */


public class TextFileIndexer {
    String sourceDir;
    String indexDir;
    IndexWriterConfig.OpenMode indexMode;

    Directory indexDirectory;
    Analyzer analyzer;
    IndexWriterConfig config;
    IndexWriter iwriter;

    Set<String> storedSet;
    Set<String> intSet;

    Pattern fieldPattern = Pattern.compile("<(.*)>.*?=(.*)");
    Pattern numPattern = Pattern.compile("\\D*(\\d+)\\D*");


    TextFileIndexer(SearchEngine context){
        sourceDir = context.sourceDir;
        indexDir = context.indexDir;
        indexMode = context.indexMode;
        analyzer = context.analyzer;
        storedSet = context.storedSet;
        intSet = context.intSet;
        initIwriter();
    }

    void initIwriter(){
        try {
            indexDirectory = FSDirectory.open(Paths.get(this.indexDir));
            config = new IndexWriterConfig(this.analyzer);
            config.setOpenMode(this.indexMode);
            iwriter = new IndexWriter(indexDirectory, config);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    void createIndex() throws Exception{
        long beginTime = new Date().getTime();
        System.out.println("indexing...");
        readFile(sourceDir);
        long endTime = new Date().getTime();
        System.out.println("index done, takes time: " + ((endTime - beginTime) / 1000.0) + "s");

        iwriter.close();
    }

    void readFile(String fileDir) throws Exception{
        Scanner in = new Scanner(new FileInputStream(fileDir));
        Document doc = null;

        String lastLine = in.nextLine(), line;
        int count = 0;
        while(in.hasNext()){
            line = in.nextLine();
            if (line.charAt(0) != '<'){
                lastLine += line;
            }
            else{//field读取完毕
                if (lastLine.equals("<REC>")){
                    if (doc != null) {
                        iwriter.addDocument(doc);
                        if (++count % 100 == 0){
                            System.out.println("已索引文章数: " + count);
                        }
                    }
                    doc = new Document();
                }
                else{
                    handleLine(doc, lastLine);
                }
                lastLine = line;
            }
        }
        if (doc != null){   //收尾
            handleLine(doc, lastLine);
            iwriter.addDocument(doc);
        }
    }

    void addField(Document doc, String fieldName, String content){
        Field.Store storeType = Field.Store.NO;
        if (storedSet.contains(fieldName))
            storeType = Field.Store.YES;

        if (intSet.contains(fieldName)) {
            int value = Integer.parseInt(pickupNum(content));
            doc.add(new IntPoint(fieldName, value));
            if (storeType == Field.Store.YES)
                doc.add(new StoredField(fieldName, value));
        }
        else
            doc.add(new TextField(fieldName, content, storeType));
    }

    String pickupNum(String s){
        Matcher m = numPattern.matcher(s);
        if (m.find())
            return m.group(1);
        return "";
    }

    void handleLine(Document doc, String line){
        Matcher m = fieldPattern.matcher(line);
        String fieldName = null, content = null;
        if (m.find()){
            fieldName = m.group(1).trim();
            content = m.group(2).trim();
            addField(doc, fieldName, content);
        }
    }

}
