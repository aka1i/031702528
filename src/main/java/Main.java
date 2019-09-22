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
            File file=new File(args[1]);
            f.createNewFile();
            FileOutputStream fos =new FileOutputStream(file,true);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            String l = null;
//            File f2 = new File("answer.txt");
//            BufferedReader r2 = new BufferedReader(new InputStreamReader(new FileInputStream(f2), "utf-8"));
//            String l2 = "";
//            String l3 = "";
//            double count = 0;
//            int level1 = 0;
//            int level2 = 0;
//            int level3 = 0;
//            while ((l2 = r2.readLine()) != null && !l2.equals("")) {
//                l3 += l2.trim();
//            }
            JSONArray array = new JSONArray();
            String[] strs = new String[4096000];
            int position = 0;
            while ((l = r.readLine()) != null && !l.equals("")) {
                strs[position] = l;
                position++;
            }
            r.close();
            for (int i = 0;i < position; i++){
                String address = strs[i].replace(".","");
                String split[] = address.split("!");
                if (split[0].equals("1")){
                    array.put(AddressUtil.addressResolutionVersion2(split[1],1));
                }else if (split[0].equals("2")){
                    array.put(AddressUtil.addressResolutionVersion2(split[1],2));
                }else{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("姓名","");
                    jsonObject.put("手机","");
                    jsonObject.put("地址",new JSONArray());
                    array.put(jsonObject);
                }
            }
//            JSONArray jsonArray2 = new JSONArray(l3);
//            for (int i = 0; i < array.length(); i++){
//                JSONObject jsonObject = array.getJSONObject(i);
//                JSONObject jsonObject1 = jsonArray2.getJSONObject(i);
//                double aa = 0;
//                if (!jsonObject1.toString().equals("{}") && jsonObject1.getInt("level") == 1){
//                    level1++;
//                    aa = 0.08;
//                }else if (!jsonObject1.toString().equals("{}") && jsonObject1.getInt("level") == 2)
//                {
//                    level2++;
//                    aa = 0.2;
//                }else
//                {
//                    level3++;
//                }
//                if (!jsonObject.toString().equals("{}") && !jsonObject1.toString().equals("{}"))
//                if (jsonObject.get("地址").toString().equals(jsonObject1.get("地址").toString())){
//                    count += aa ;
//                }else{
//                    System.out.println(jsonObject.get("地址").toString());
//                    System.out.println(jsonObject1.get("地址").toString());
//                }
//            }
//            System.out.println(level1);
//            System.out.println(level2);
//            System.out.println(level3);
//            System.out.println(count);
            out.write(array.toString());
            out.flush();
            out.close();
    //        r2.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
