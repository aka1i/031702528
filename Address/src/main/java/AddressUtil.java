import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yjn
 * @creatTime 2019/9/11 - 18:45
 */
public class AddressUtil {
    private static String pCode;
    private static int pLong;
    private static String cCode;
    private static int cLong;
    private static LinkedHashMap<String,Object> cMap;
    private static String countyCode;
    private static int countyLong;
    private static ArrayList<LinkedHashMap<String,Object>> countyMapList;
    private static String phoneNumber = "";
    private static String province = "";
    private static String city = "";
    private static String county = "";
    private static String town = "";
    private static String village = "";
    public static JSONObject addressResolutionVersion1(String address){
        String phoneNumber = null;
        String province,city,county,town,village;
        JSONObject jsonObject = new JSONObject();
        String phoneRegex = "\\d{11}";
        Matcher a = Pattern.compile(phoneRegex).matcher(address);
        while (a.find()){
            phoneNumber = a.group();
        }
        jsonObject.put("手机",phoneNumber);
        String splitPhone[] = address.split(phoneRegex);    //分割手机号
        address = splitPhone[0] + splitPhone[1];
        String addreOnly[] = address.split(","); //分割姓名
        String name = addreOnly[0];
        address = addreOnly[1];
        jsonObject.put("姓名",name);
//        String regex="(?<province>[^省]+自治区|.*?省|.*?行政区)?(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)?(?<county>[^县]+县|.+?区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇|.+街道)?(?<village>.*)";
//        Matcher m= Pattern.compile(regex).matcher(address);
        JSONArray addressArray = new JSONArray();
//        if (m.find()){
//            province=m.group("province");
//            city=m.group("city");
//            if (province == null && city != null)
//                province = city.substring(0,city.length() - 1);
//
//            addressArray.put(province==null?"":province.trim());
//            addressArray.put(city==null?"":city.trim());
//            county=m.group("county");
//            addressArray.put(county==null?"":county.trim());
//            town=m.group("town");
//            addressArray.put( town==null?"":town.trim());
//            village=m.group("village");
//            addressArray.put(village==null?"":village.trim());
//        }
        jsonObject.put("地址",addressArray);

        return jsonObject;
    }
    public static JSONObject addressResolutionVersion2(String address){
        pCode = "";
        pLong = 0;
        cCode = "";
        cLong = 0;
        cMap = null;
        countyCode = "";
        countyLong = 0;
        countyMapList = null;
        phoneNumber = "";
        province = "";
        city = "";
        county = "";
        town = "";
        String village = "";
        JSONObject jsonObject = new JSONObject();
        String phoneRegex = "\\d{11}";
        Matcher a = Pattern.compile(phoneRegex).matcher(address);
        while (a.find()){
            phoneNumber = a.group();
        }
        jsonObject.put("手机",phoneNumber);
        String splitPhone[] = address.split(phoneRegex);    //分割手机号
        address = splitPhone[0] + splitPhone[1];
        String addreOnly[] = address.split(","); //分割姓名
        String name = addreOnly[0];
        address = addreOnly[1];
        jsonObject.put("姓名",name);
        JSONArray addressArray = new JSONArray();
        province = getProvince(address);
        if (province.charAt(province.length() - 1) == '市')
        {
            pLong = 0;
        }
        address = address.substring(pLong);
        if (province != null){
            city = getCity(address);
            address = address.substring(cLong);
        }else {

        }

        county = getCounty(address);
        address = address.substring(cLong);

        addressArray.put(province);
        addressArray.put(city);
        addressArray.put(county);
        String regex;
        if (county.length() > 0 && county.charAt(county.length() - 1) != '区')
            regex = "(?<town>[^区]+区|.+镇|.+街道)?(?<village>.*)";
        else
            regex = "(?<town>.+镇|.+街道)?(?<village>.*)";
        Matcher m= Pattern.compile(regex).matcher(address);
        if (m.find()){
            town=m.group("town");
            addressArray.put( town==null?"":town.trim());
            village=m.group("village");
            addressArray.put(village==null?"":village.trim());
        }
        jsonObject.put("地址",addressArray);
        return jsonObject;
    }

    private static String getProvince(String address){
        for (int i = 1; i < address.length(); i++){
            for (Map.Entry<String,Object> entry: AreaCodeConvert.map.entrySet()){
                LinkedHashMap<String,Object> pMap = (LinkedHashMap<String,Object>)entry.getValue();
                String pName = (String) pMap.get("name");
                String lastStr = pName.substring(pName.length() - 1);
                if (lastStr.equals("省") || lastStr.equals("市")){
                    if ((pName.substring(0,pName.length() - 1)).equals(address.substring(0,i))){
                        pCode = (String) pMap.get("code");
                        if (address.charAt(i) == '省' || address.charAt(i) == '市')
                            pLong = i + 1;
                        else
                            pLong = i;
                        return pName;
                    }
                }

                else if (lastStr.equals("区")){
                    if (pName.length() > 3 && (pName.substring(0,pName.length() - 3)).equals(address.substring(0,i)))
                    {
                        pCode = (String) pMap.get("code");
                        pLong = i + 3;
                        return pName;
                    }
                    if (pName.length() > 5 && (pName.substring(0,pName.length() - 5)).equals(address.substring(0,i)))
                    {
                        pCode = (String) pMap.get("code");
                        pLong = i + 5;
                        return pName;
                    }
                }
            }
        }
        return "";
    }

    private static String getCity(String address){
        for (int i = 1; i < address.length(); i++){
            cMap = ((LinkedHashMap<String,Object>)(((LinkedHashMap<String,Object>)AreaCodeConvert.map.get(pCode.substring(0,2))).get("children")));
            for (Map.Entry<String,Object> entry: cMap.entrySet()){
                LinkedHashMap<String,Object> cMap = (LinkedHashMap<String,Object>)(entry.getValue());
                String cName = (String) cMap.get("name");
                String lastStr = cName.substring(cName.length() - 1);
                if (lastStr.equals("市") || lastStr.equals("盟")){
                    if ((cName.substring(0,cName.length() - 1)).equals(address.substring(0,i))){
                        cCode = (String) cMap.get("code");
                        if (address.charAt(i) == '市' || address.charAt(i) == '盟')
                            cLong = i + 1;
                        else
                            cLong = i;
                        return cName;
                    }
                }else if (lastStr.equals("区")){
                    if ((cName.substring(0,cName.length() - 2)).equals(address.substring(0,i))){
                        cCode = (String) cMap.get("code");
                        if (address.substring(i,i + 2).equals("地区"))
                            cLong = i + 2;
                        else
                            cLong = i;
                        return cName;
                    }

                }else if (lastStr.equals("州")){
                    if ((cName.substring(0,cName.length() - 3)).equals(address.substring(0,i))){
                        cCode = (String) cMap.get("code");
                        if (address.substring(i,i + 3).equals("自治州"))
                            cLong = i + 3;
                        else
                            cLong = i;
                        return cName;
                    }
                }

            }
        }
        return "";
    }

    private static String getCounty(String address){
        countyMapList = (ArrayList<LinkedHashMap<String,Object>>)(((LinkedHashMap<String,Object>)cMap.get(cCode.substring(0,4)))).get("children");
        if (countyMapList == null)
            return "";
        for (LinkedHashMap<String,Object> map : countyMapList){
            String countyName = (String) map.get("name");
            String lastStr = countyName.substring(countyName.length() - 1);
            for (int i = 1; i < address.length(); i++){
                if (lastStr.equals("区") || lastStr.equals("市") || lastStr.equals("旗") || lastStr.equals("岛")){
                    if ((countyName.substring(0,countyName.length() - 1)).equals(address.substring(0,i))){
                        cCode = (String) cMap.get("code");
                        if (address.charAt(i) == '区' || address.charAt(i) == '市' || address.charAt(i) == '旗' || address.charAt(i) == '岛')
                            cLong = i + 1;
                        else
                            cLong = i;
                        return countyName;
                    }
                }else if (lastStr.equals("域")){
                    if ((countyName.substring(0,countyName.length() - 2)).equals(address.substring(0,i))){
                        cCode = (String) cMap.get("code");
                        if (address.substring(i,i + 2) == "海域")
                            cLong = i + 2;
                        else
                            cLong = i;
                        return countyName;
                    }
                }
            }
        }
        return "";
    }
}
