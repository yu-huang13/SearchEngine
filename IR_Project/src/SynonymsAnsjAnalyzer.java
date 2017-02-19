//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.nlpcn.commons.lang.util.IOUtil;
import org.nlpcn.commons.lang.util.StringUtil;
import org.ansj.lucene6.AnsjAnalyzer;

public class SynonymsAnsjAnalyzer extends Analyzer {
    private Set<String> filter;
    private AnsjAnalyzer.TYPE type;
    Word2VEC word2vec;

    public SynonymsAnsjAnalyzer(AnsjAnalyzer.TYPE type, Set<String> filter, Word2VEC word2vec) {
        this.type = type;
        this.filter = filter;
        this.word2vec = word2vec;
    }

    public SynonymsAnsjAnalyzer(AnsjAnalyzer.TYPE type, String stopwordsDir, Word2VEC word2vec) {
        this.type = type;
        this.filter = this.filter(stopwordsDir);
        this.word2vec = word2vec;
    }

    public SynonymsAnsjAnalyzer(AnsjAnalyzer.TYPE type, Word2VEC word2vec) {
        this.type = type;
        this.word2vec = word2vec;
    }

    public SynonymsAnsjAnalyzer(Word2VEC word2vec) {
        this.type = AnsjAnalyzer.TYPE.query;
        this.word2vec = word2vec;
    }

    private Set<String> filter(String stopwordsDir) {
        if(StringUtil.isBlank(stopwordsDir)) {
            return null;
        } else {
            try {
                List e = IOUtil.readFile2List(stopwordsDir, "utf-8");
                return new HashSet(e);
            } catch (Exception var3) {
                System.err.println("not foun stop word path by " + (new File(stopwordsDir)).getAbsolutePath());
                var3.printStackTrace();
                return null;
            }
        }
    }

    protected TokenStreamComponents createComponents(String text) {
        BufferedReader reader = new BufferedReader(new StringReader(text));
        Tokenizer tokenizer = null;
        tokenizer = AnsjAnalyzer.getTokenizer(reader, this.type, this.filter);
        TokenStream tokenStream = new SynonymFilter(tokenizer, word2vec);
        return new TokenStreamComponents(tokenizer, tokenStream);
    }


    public static enum TYPE {
        index,
        query,
        to,
        dic,
        user,
        search;

        private TYPE() {
        }
    }
}


