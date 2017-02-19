import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HY on 16/11/4.
 */

public class TextSearcher {
    String indexDir;
    String word2vecDir;

    Directory indexDirectory;
    Analyzer analyzer;
    Analyzer synonymsAnalyzer = null;
    Word2VEC word2vec;
    String[] allFields;
    Set<String> intSet;

    DirectoryReader ireader;
    IndexSearcher isearcher;

    Pattern emptyPattern = Pattern.compile("^\\s*$");
    Pattern rangePattern = Pattern.compile("^\\D*(\\d+)\\D+(\\d+)\\D*$");

    final int TOP_N = 10;

    TextSearcher(SearchEngine context){
        indexDir = context.indexDir;
        word2vecDir = context.word2vecDir;
        analyzer = context.analyzer;
        allFields = context.allFields;
        intSet = context.intSet;

        try{
            indexDirectory = FSDirectory.open(Paths.get(indexDir));
            ireader = DirectoryReader.open(indexDirectory);
            isearcher = new IndexSearcher(ireader);
            if (word2vecDir != null){
                word2vec = new Word2VEC();
                word2vec.setTopNSize(1);
                word2vec.loadModel(word2vecDir);
                synonymsAnalyzer = new SynonymsAnsjAnalyzer(word2vec);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    String[] stringSetToArray(HashSet<String> s){
        return s.toArray(new String[1]);
    }

    HashSet<String> stringArrayToSet(String []s){
        return new HashSet<String>(Arrays.asList(s));
    }

    Boolean validQry(String s){
        return !emptyPattern.matcher(s).find();
    }

    Query buildQuery(String fieldName, String content) throws Exception{
        if (intSet.contains(fieldName))
            return buildIntRangeQuery(fieldName, content);
        return buildStringQuery(fieldName, content, analyzer);
    }

    Query buildStringQuery(String fieldName, String content, Analyzer a) throws Exception{
        QueryParser parser = new QueryParser(fieldName, a);
        Query query = parser.parse(content);
        return query;
    }

    Query buildIntRangeQuery(String fieldName, String content) throws Exception{
        Matcher m = rangePattern.matcher(content);
        int lower, upper;
        if (m.find()){
            lower = Integer.parseInt(m.group(1));
            upper = Integer.parseInt(m.group(2));
            return IntPoint.newRangeQuery(fieldName, lower, upper);
        }
        else{
            throw new Exception("年份输入格式有误");
        }
    }


    Document[] query(String input, Map<String, String> limitMap) throws Exception{
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

        //处理限制搜索框, MUST
        Set<String> mustFieldSet = new HashSet<String>();
        for (Map.Entry<String, String> entry: limitMap.entrySet()){
            if (validQry(entry.getValue())){
                Query query = buildQuery(entry.getKey(), entry.getValue());
                BooleanClause booleanClause = new BooleanClause(query, BooleanClause.Occur.MUST);
                queryBuilder.add(booleanClause);
                mustFieldSet.add(entry.getKey());
            }
        }

        //处理主搜索框, SHOULD
        if (validQry(input)){
            for (String fieldName: allFields){
                if (!mustFieldSet.contains(fieldName)){
                    Query query = buildStringQuery(fieldName, input, synonymsAnalyzer == null? analyzer : synonymsAnalyzer);
                    BooleanClause booleanClauses = new BooleanClause(query, BooleanClause.Occur.SHOULD);
                    queryBuilder.add(booleanClauses);
                }
            }
        }

        ScoreDoc[] hits = isearcher.search(queryBuilder.build(), TOP_N, Sort.RELEVANCE).scoreDocs;
        Document[] docs = new Document[hits.length];
        for (int i = 0; i < hits.length; ++i)
            docs[i] = isearcher.doc(hits[i].doc);
        return docs;
    }

    Document[] query(String input) throws Exception{
        return query(input, new HashMap<String, String>());
    }

//    Document[] query(String input) throws Exception{
//        QueryParser parser = new MultiFieldQueryParser(allFields, analyzer);
//        Query query = parser.parse(input);
//        ScoreDoc[] hits = isearcher.search(query, TOP_N, Sort.RELEVANCE).scoreDocs;
//        Document[] docs = new Document[hits.length];
//        for (int i = 0; i < hits.length; ++i)
//            docs[i] = isearcher.doc(hits[i].doc);
//        return docs;
//    }
}
