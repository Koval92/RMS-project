package production;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamReader {

    public static Map<String, String> readAlgorithmFile(File file) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert stream != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        Map<String, String> params = new HashMap<>();
        int lineNumber = 0;

        try {
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.length() == 0 || (line.charAt(0) == '#')) {
                    continue;
                }
                line = line.split("#", 2)[0];
                String[] entry = line.split(": ");
                if (entry.length != 2) {
                    System.out.println("invalid line " + lineNumber + ": "+ line);
                    continue;
                }
                params.put(entry[0].trim(), entry[1].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }

    public static List<Map<String, String>> readTestFile(File file) {
        List<Map<String, String>> paramList = new ArrayList<>();

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert stream != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        Map<String, String> params = new HashMap<>();
        boolean added = false;
        int lineNumber = 0;

        try {
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.length() == 0 || (line.charAt(0) == '#')) {
                    continue;
                }
                line = line.split("#", 2)[0];

                if (line.substring(0, 3).equals("***")) {
                    if(params.get("algorithm_name") != null) {
                        paramList.add(params);
                    }
                    added = true;
                    params = new HashMap<>();
                    continue;
                }

                String[] entry = line.split(": ");
                if (entry.length != 2) {
                    System.out.println("invalid line " + lineNumber + ": " + line);
                    continue;
                }
                params.put(entry[0].trim(), entry[1].trim());
                added = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!added && params.get("algorithm_name") != null)
            paramList.add(params);

        return paramList;
    }
}
