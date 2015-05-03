package test;

import org.junit.Test;
import production.ParamReader;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ParamReaderTest {

    @Test
    public void testReadAlgorithmFile() throws Exception {
        Map<String, String> params = ParamReader.readAlgorithmFile(new File("params/singleAlgorithmTest.txt"));

        System.out.println("Single algorithm");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    @Test
    public void testReadTestFile() throws Exception {
        List<Map<String, String>> paramList = ParamReader.readTestFile(new File("params/test1.txt"));
        System.out.println();
        System.out.println("Test file");
        System.out.println(paramList.size());
        for (Map<String, String> params : paramList) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
            System.out.println();
        }
    }
}