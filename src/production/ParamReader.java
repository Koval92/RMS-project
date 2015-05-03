package production;

import java.io.*;
import java.util.HashMap;
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

        try {
            while ((line = br.readLine()) != null) {
                if (line.length() == 0 || (line.charAt(0) == '#')) {
                    continue;
                }
                line = line.split("#", 2)[0];
                String[] entry = line.split(": ");
                if (entry.length != 2) {
                    System.out.println("invalid line: " + line);
                    continue;
                }
                params.put(entry[0], entry[1]);
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
}
