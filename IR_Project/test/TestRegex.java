import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by HY on 16/12/25.
 */

public class TestRegex {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    void findResult(Matcher m){
        if (m.find())
            System.out.println("FOUND");
        else
            System.out.println("NO MATCH");
        System.out.println("\n");
    }

    @Test
    public void testRegex1() throws Exception{
        String regex = "(\\D*)(\\d+)(.*)";
        String line = "This order was placed for QT3000! OK?";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(line);

        if (m.find( )) {
            System.out.println("Found value: " + m.group(0) );
            System.out.println("Found value: " + m.group(1) );
            System.out.println("Found value: " + m.group(2) );
            System.out.println("Found value: " + m.group(3) );
        } else {
            System.out.println("NO MATCH");
        }
    }

    @Test
    public void testRegex2() throws Exception{
        String regex = "^\\s*$";
        String[] lines = {"This order was placed for QT3000! OK?", "", " ", "  ", "      \n"};
        Boolean[] result = {false, true, true, true, true};

        Pattern p = Pattern.compile(regex);

        for (int i = 0; i < lines.length; ++i){
            String line = lines[i];
            System.out.println("line: **" + lines[i] + "**");
            Matcher m = p.matcher(line);
            assertEquals(result[i], m.find());
        }
    }

    @Test
    public void testRegex3() throws Exception{
        String regex = "\\D*(\\d+)\\D*";
        String[] lines = {"sdfa", "This order was placed for QT3000! OK?", "2013;2013", "2013;", "2013 ", "2013\n", " 2013 \n"};
        Pattern p = Pattern.compile(regex);

        for (int i = 0; i < lines.length; ++i){
            String line = lines[i];
            System.out.println("line: **" + lines[i] + "**");
            Matcher m = p.matcher(line);
            if (m.find()){
                System.out.println("match result:" + m.group(1));
            }
            else{
                System.out.println("NO MATCH");
            }
        }
    }

    @Test
    public void testRegex4() throws Exception{
        String regex = "^\\D*(\\d+)\\D+(\\d+)\\D*$";
        String[] lines = {"2013 2014", "2013 2014\n", "2013      2014", "2013 2014;", "2013; 2014;", "2013 "};
        Pattern p = Pattern.compile(regex);

        for (int i = 0; i < lines.length; ++i){
            String line = lines[i];
            System.out.println("line: **" + lines[i] + "**");
            Matcher m = p.matcher(line);
            if (m.find()){
                System.out.println("match result:" + m.group(1) + ":" + m.group(2));
                assertEquals("2013", m.group(1));
                assertEquals("2014", m.group(2));
            }
            else{
                System.out.println("NO MATCH");
            }
        }

    }
}
