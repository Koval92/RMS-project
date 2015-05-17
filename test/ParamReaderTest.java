package test;

import org.junit.Assert;
import org.junit.Test;
import pathfinder.ParamReader;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ParamReaderTest {
    @Test
    public void testReadAlgorithmFile() throws Exception {
        String fileName = "params/singleAlgorithmTest.txt";
        Map<String, String> params = ParamReader.getParamsForSingleAlgorithm(new File(fileName));

        Assert.assertEquals(5, params.size());
        Assert.assertEquals("LeftToRight", params.get("algorithm_name"));
        Assert.assertEquals("param1 value", params.get("param_1"));
        Assert.assertEquals("param_2_value", params.get("param_2"));
        Assert.assertEquals("param 3 value", params.get("param 3"));
        Assert.assertEquals("param 4 value", params.get("param_4"));
    }

    @Test
    public void testReadTestFile() throws Exception {
        String fileName = "params/test1.txt";
        List<Map<String, String>> paramList = ParamReader.getParamsForMultipleAlgorithms(new File(fileName));

        Assert.assertEquals(2, paramList.size());

        Map<String, String> params;

        params = paramList.get(0);
        Assert.assertEquals("Snake", params.get("algorithm_name"));

        params = paramList.get(1);
        Assert.assertEquals("LeftToRight", params.get("algorithm_name"));
    }

    @Test
    public void testReadTestFileWithSingleAlgorithm() throws Exception {
        String fileName = "params/singleAlgorithmTest.txt";
        List<Map<String, String>> paramList = ParamReader.getParamsForMultipleAlgorithms(new File(fileName));

        Assert.assertEquals(1, paramList.size());
    }
}