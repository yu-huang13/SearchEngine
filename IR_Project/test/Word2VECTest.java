import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by HY on 16/12/25.
 */
public class Word2VECTest {
    Word2VEC word2vec;

    @Before
    public void setUp() throws Exception {
        word2vec = new Word2VEC();
        word2vec.loadModel("data/model/word2vec/vectors_s200_w8_iter15.bin");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDistance() throws Exception {
        Set<Word2VEC.WordEntry> result = word2vec.distance("梁兴琦");
        if (result != null){
            for (Word2VEC.WordEntry entry: result){
                System.out.println(entry.name + ":" + entry.score);
            }
        }
    }

    @Test
    public void testGetNearestWord() throws Exception {
        String[] result = word2vec.getNearestWord("电脑");
        for (String word: result){
            System.out.println(word);
        }
    }

    @Test
    public void testAnalogy() throws Exception {
        Set<Word2VEC.WordEntry> result = word2vec.analogy("中华民国", "中华人民共和国", "毛泽东");
        for (Word2VEC.WordEntry entry: result){
            System.out.println(entry.name + ":" + entry.score);
        }
    }

}