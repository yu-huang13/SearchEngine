import org.ansj.splitWord.analysis.ToAnalysis;

public class Main {

    public static void main(String[] args) throws Exception{

        SearchEngine searchEngine = new SearchEngine();
        searchEngine.handleArgs(args);
        searchEngine.run();

//        String words = "程晓杰;李美龄;";
//        System.out.println(ToAnalysis.parse(words));

    }
}
