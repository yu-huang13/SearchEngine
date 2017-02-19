import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.Stack;

/**
 * Created by HY on 16/12/26.
 */
public class SynonymFilter extends TokenFilter {
    Word2VEC word2vec;
    private Stack<String> synonymStack;
    private AttributeSource.State current;
    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncrAtt;

    SynonymFilter(TokenStream input, Word2VEC word2vec){
        super(input);
        this.word2vec = word2vec;
        synonymStack = new Stack<String>();
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException{
        if (synonymStack.size() > 0) { // Pop buffered synonyms
            String syn = synonymStack.pop();
            restoreState(current);
            termAtt.copyBuffer(syn.toCharArray(), 0, syn.length());
            posIncrAtt.setPositionIncrement(0); // Set position increment to 0
            return true;
        }

        if (!input.incrementToken()) // Read next token
            return false;

        if (addAliasesToStack()) { // Push synonyms onto stack
            current = captureState(); // Save current token
        }

        return true; // #Return current token
    }

    private boolean addAliasesToStack() throws IOException {
        String[] synonyms = word2vec.getNearestWord(termAtt.toString()); // Retrieve synonyms

        if (synonyms == null) {
            return false;
        }
        for (String synonym : synonyms) { // Push synonyms onto stack
            synonymStack.push(synonym);
        }
        return true;
    }


}
