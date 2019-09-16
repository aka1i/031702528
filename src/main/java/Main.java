import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author yjn
 * @creatTime 2019/9/11 - 15:58
 */
public class Main {
    public static void main(String[] args) {
        try {
            File f = new File(args[0]);
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
            BufferedWriter out = new BufferedWriter(new FileWriter(args[1]));
            String l = null;
            JSONArray array = new JSONArray();
            while ((l = r.readLine()) != null && !l.equals("")) {
                String address = l.replace(".","");
                String split[] = address.split("!");
//                System.out.println(AddressUtil.addressResolutionVersion2(split[1],1));
                if (split[0].equals("1")){
                    array.put(AddressUtil.addressResolutionVersion2(split[1],1));
                }else if (split[0].equals("2")){
                    array.put(AddressUtil.addressResolutionVersion2(split[1],2));
                }else
                    array.put(new JSONObject());

            }
            out.write(array.toString());
            out.close();
            r.close();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
