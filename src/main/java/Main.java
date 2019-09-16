import org.apache.commons.lang3.StringUtils;

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
        Scanner scanner = new Scanner(System.in);
        scanner.next();
        try {
            File f = new File(args[0]);
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
            BufferedWriter out = new BufferedWriter(new FileWriter(args[1]));
            String l = null;

            while ((l = r.readLine()) != null) {
                String address = l.replace(".","");
                String split[] = address.split("!");
//                System.out.println(AddressUtil.addressResolutionVersion2(split[1],1));
                if (split[0].equals("1")){
                    out.write(AddressUtil.addressResolutionVersion2(split[1],1).toString() + "\r\n");
                }else if (split[0].equals("2")){
                    out.write(AddressUtil.addressResolutionVersion2(split[1],2).toString() + "\r\n");
                }

            }

            out.close();
            r.close();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scanner.next();
    }

}
