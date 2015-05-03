package test;

import org.junit.Test;
import production.ParamReader;

import java.io.File;
import java.util.Map;

public class ParamReaderTest {

    @Test
    public void testReadAlgorithmFile() throws Exception {
        Map<String, String> params = ParamReader.readAlgorithmFile(new File("params/singleAlgorithmTest.txt"));

        System.out.println("Start");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}