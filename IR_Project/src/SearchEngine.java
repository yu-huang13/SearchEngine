import org.ansj.lucene6.AnsjAnalyzer;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;

import java.util.*;

/**
 * Created by HY on 16/11/4.
 */

public class SearchEngine {
    Options options;
    String sourceDir = null;
    String indexDir = null;
    String word2vecDir = null;
    IndexWriterConfig.OpenMode indexMode = IndexWriterConfig.OpenMode.CREATE;
    Analyzer analyzer;

    TextFileIndexer indexer = null;
    TextSearcher searcher = null;

    Set<String> storedSet = new HashSet<String>(Arrays.asList("题名", "作者", "关键词", "出版单位", "出版日期", "摘要", "专题名称", "分类名称"));

    Set<String> intSet = new HashSet<String>(Arrays.asList("年", "期", "被引年"));

    String[] allFields = new String[] {
            "题名", "英文篇名", "作者", "英文作者", "第一责任人", "单位", "来源", "出版单位", "关键词", "英文关键词",
            "摘要", "英文摘要", "年", "期", "专辑代码", "专题代码", "专题子栏目代码", "专题名称", "分类号", "分类名称", "文件名",
            "语种", "引证文献", "共引文献", "二级引证文献", "表名", "出版日期", "引证文献数量", "二级参考文献数量", "二级引证文献数量",
            "共引文献数量", "同被引文献数量", "英文刊名", "ISSN", "CN", "被引年", "参考文献"
    };

    String[] limitField = new String[] {"题名", "作者", "出版单位", "年"};
    Map<String, String> fieldFormatMap = new HashMap<String, String >(){{
        put("年", "(起始年份 结束年份)");
    }};

    SearchEngine(){
        options = new Options();
        options.addOption("h", "help", false, "Print usage information");

        options.addOption(createArgOption("i", "index", "Set directory of index.", "Directory"));
        options.addOption(createArgOption("s", "source", "Set path of source file and create index. You can set mode by using option -m.", "File"));
        options.addOption(createArgOption("m", "mode", "Index create mode(default mode: create). args: create | append", "Mode"));
        options.addOption(createArgOption("a", "analyzer", "Set analyzer(default Analyzer: AnsjAnalyzer). args: standard | ansj | cjk", "Analyzer"));
        options.addOption(createArgOption("w", "word2vec", "Set path of word2vec model and turn on the word association function.", "Directory"));

        BasicConfigurator.configure();
        analyzer = new AnsjAnalyzer();
    }

    void handleArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try{
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption('h')){
                printUsage();
                System.exit(0);
            }
            if (commandLine.hasOption('i')){
                indexDir = commandLine.getOptionValue('i');
            }
            if (commandLine.hasOption('s')){
                sourceDir = commandLine.getOptionValue('s');
            }
            if (commandLine.hasOption("w")){
                word2vecDir = commandLine.getOptionValue('w');
            }
            if (commandLine.hasOption('m')){
                switch(commandLine.getOptionValue('m')){
                    case "create" : indexMode = IndexWriterConfig.OpenMode.CREATE; break;
                    case "append" : indexMode = IndexWriterConfig.OpenMode.APPEND; break;
                    default: handleCommandError(); break;
                }
            }
            if (commandLine.hasOption("a")){
                switch(commandLine.getOptionValue('a')){
                    case "standard" : analyzer = new StandardAnalyzer(); break;
                    case "ansj" : break;
                    case "cjk" : analyzer = new CJKAnalyzer(); break;
                    default: handleCommandError(); break;
                }
            }

            if (!legalCommand())
                handleCommandError();
        }

        catch(Exception e){
            handleCommandError();
        }
    }

    boolean legalCommand(){
        if (indexDir == null) return false;
        return true;
    }

    void handleCommandError(){
        printUsage();
        System.exit(0);
    }

    void printUsage(){
        System.out.println("Usage: java Main.java [-options] -i directory");

        System.out.println("Options:");
        Collection allOpts = options.getOptions();
        for (Iterator it = allOpts.iterator(); it.hasNext();){
            Option opt = (Option)(it.next());
            System.out.println(optionToString(opt));
        }
    }

    String optionToString(Option opt){
        return "-" + opt.getOpt() + " -" + opt.getLongOpt() + " " + (opt.hasArg()? opt.getArgName() : "") + "   :  " + opt.getDescription();
    }

    Option createArgOption(String opt, String longOpt, String description, String argName){
        Option option = new Option(opt, longOpt, true, description);
        option.setArgName(argName);
        return option;
    }

    void outputDocs(Document[] docs){
        System.out.println(docs.length + " results:");
        for (Document doc: docs){
            for (IndexableField field : doc.getFields())
                System.out.println(field.name() + ": " + doc.get(field.name()));
            System.out.println("");
        }
        System.out.println("");
    }

    String genPromptString(String fieldName){
        if (fieldFormatMap.containsKey(fieldName))
            return fieldName + fieldFormatMap.get(fieldName);
        else
            return fieldName;
    }

    void run() throws Exception{

        if (sourceDir != null){//建立索引
            indexer = new TextFileIndexer(this);
            indexer.createIndex();
        }
        searcher = new TextSearcher(this);

        String input;
        Map<String, String> limitMap = new HashMap<String, String>();
        Scanner in = new Scanner(System.in);
        while(true){
            System.out.print("input query: ");
            input = in.nextLine();

            for (String field: limitField){
                System.out.print(genPromptString(field) + ":");
                limitMap.put(field, in.nextLine());
            }

            outputDocs(searcher.query(input, limitMap));
        }
    }
}
