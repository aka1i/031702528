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
    public static JSONObject addressResolutionVersion2(String address,int type){
        if (AreaCodeConvert.map.size() == 0){
            try {
                AreaCodeConvert.Convert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        village = "";
        JSONObject jsonObject = new JSONObject();
        String phoneRegex = "\\d{11}";
        Matcher a = Pattern.compile(phoneRegex).matcher(address);
        while (a.find()){
            phoneNumber = a.group();
        }
        jsonObject.put("手机",phoneNumber);
        String splitPhone[] = address.split(phoneRegex);    //分割手机号
        if (splitPhone.length > 1)
            address = splitPhone[0] + splitPhone[1];
        else
            address = splitPhone[0];
        String addreOnly[] = address.split(","); //分割姓名
        String name = addreOnly[0];
        if (addreOnly.length == 1)
            address = "";
        else
            address = addreOnly[1];
        jsonObject.put("姓名",name);
        JSONArray addressArray = new JSONArray();
        province = getProvince(address);
        if (province.length() > 0 && province.charAt(province.length() - 1) == '市')
        {
            province = province.substring(0,province.length() - 1);
            pLong = 0;
        }
        address = address.substring(pLong);
        if (!province.equals("")){
            city = getCity(address);
            address = address.substring(cLong);
        }else {
            city = getCityWithoutProvince(address);
            address = address.substring(cLong);
        }

        if (!province.equals("") && city.equals("")){
            county = getCountyWithoutCity(address);
        }else if (province.equals("") && city.equals("")){
            county = getCountyWithoutProvinceAndCity(address);
        }else
            county = getCounty(address);



        address = address.substring(countyLong);
        addressArray.put(province);
        addressArray.put(city);
        addressArray.put(county);
        if (type == 1){
            String regex;
            if (county.length() > 0 && county.charAt(county.length() - 1) != '区')
                regex = "(?<town>[^区]+?区|.+?镇|.+?街道|.+?乡)?(?<village>.*)";
            else
                regex = "(?<town>.+?镇|.+?街道|.+?乡)?(?<village>.*)";
            Matcher m= Pattern.compile(regex).matcher(address);
            if (m.find()){
                town=m.group("town");
                addressArray.put( town==null?"":town.trim());
                village=m.group("village");
                addressArray.put(village==null?"":village.trim());
            }
        }else {
            String detailRegex;
            if (county.length() > 0 && county.charAt(county.length() - 1) != '区')
                detailRegex  = "(?<town>[^区]+区|.+?镇|.+?街道|.+?乡)?(?<village1>.+?街|.+?路|.+?巷)?(?<village2>[\\d]+?号|[\\d]+.?道)?(?<village3>.*)";
            else
                detailRegex = "(?<town>.+?镇|.+?街道|.+?乡)?(?<village1>.+?街|.+?路|.+?巷)?(?<village2>[\\d]+?号|[\\d]+.?道)?(?<village3>.*)";
            Matcher m= Pattern.compile(detailRegex).matcher(address);
            if (m.find()){
                town=m.group(1);
                addressArray.put( town==null?"":town.trim());
                String village1=m.group(2);
                addressArray.put(village1==null?"":village1.trim());
                String village2=m.group(3);
                addressArray.put(village2==null?"":village2.trim());
                String village3=m.group(4);
                addressArray.put(village3==null?"":village3.trim());
            }

        }

        jsonObject.put("地址",addressArray);

        return jsonObject;
    }

    private static String getProvince(String address){
        for (Map.Entry<String,Object> entry: AreaCodeConvert.map.entrySet()){
            for (int i = 1; i <= address.length(); i++){
                LinkedHashMap<String,Object> pMap = (LinkedHashMap<String,Object>)entry.getValue();
                String pName = (String) pMap.get("name");
                String lastStr = pName.substring(pName.length() - 1);
                if (lastStr.equals("省") || lastStr.equals("市")){
                    if ((pName.substring(0,pName.length() - 1)).equals(address.substring(0,i))){
                        pCode = (String) pMap.get("code");
                        if (address.length() >= i + 1 && (address.charAt(i) == '省' || address.charAt(i) == '市'))
                            pLong = i + 1;
                        else
                            pLong = i;
                        return pName;
                    }
                }
                else if (lastStr.equals("区")){
                    if (pName.substring(pName.length() - 3).equals("自治区") && (pName.substring(0,pName.length() - 3)).equals(address.substring(0,i)))
                    {
                        pCode = (String) pMap.get("code");
                        if (address.length() >= i + 3 && address.substring(i,i + 3).equals("自治区"))
                            pLong = i + 3;
                        else
                            pLong = i;
                        return pName;
                    }
                    else if (pName.substring(pName.length() - 5).equals("特别行政区") && (pName.substring(0,pName.length() - 5)).equals(address.substring(0,i)))
                    {
                        pCode = (String) pMap.get("code");
                        if (address.length() >= i + 5 && address.substring(i,i + 5).equals("特别行政区"))
                            pLong = i + 5;
                        else
                            pLong = i;
                        return pName;
                    }
                }
            }
        }
        return "";
    }

    private static String getCity(String address){
        cMap = ((LinkedHashMap<String,Object>)(((LinkedHashMap<String,Object>)AreaCodeConvert.map.get(pCode.substring(0,2))).get("children")));
        if (cMap == null)
            return "";
        for (Map.Entry<String,Object> entry: cMap.entrySet()){
            for (int i = 1; i <= address.length(); i++){
                LinkedHashMap<String,Object> cMap = (LinkedHashMap<String,Object>)(entry.getValue());
                String cName = (String) cMap.get("name");
                String lastStr = cName.substring(cName.length() - 1);
                if (lastStr.equals("市") || lastStr.equals("盟") || lastStr.equals("县")){
                    if ((cName.substring(0,cName.length() - 1)).equals(address.substring(0,i))){
                        cCode = (String) cMap.get("code");
                        if (address.length() >= i + 1 && (address.charAt(i) == '市' || address.charAt(i) == '盟' || address.charAt(i) == '县'))
                            cLong = i + 1;
                        else
                            cLong = i;
                        return cName;
                    }
                }else if (lastStr.equals("区")){
                    if ((cName.substring(0,cName.length() - 2)).equals(address.substring(0,i))){
                        cCode = (String) cMap.get("code");
                        if (address.length() >= i + 2 && address.substring(i,i + 2).equals("地区"))
                            cLong = i + 2;
                        else
                            cLong = i;
                        return cName;
                    }

                }else if (lastStr.equals("州")){
                    if ((cName.substring(0,cName.length() - 3)).equals(address.substring(0,i))){
                        cCode = (String) cMap.get("code");
                        if (address.length() >= i + 3 && address.substring(i,i + 3).equals("自治州"))
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
            for (int i = 1; i <= address.length(); i++){
                if (lastStr.equals("区") || lastStr.equals("市") || lastStr.equals("旗") || lastStr.equals("岛") ||  lastStr.equals("县")){
                    if ((countyName.substring(0,countyName.length() - 1)).equals(address.substring(0,i))){
                        cCode = (String) cMap.get("code");
                        if (address.length() >= i + 1 && ( address.charAt(i) == '区' || address.charAt(i) == '市' || address.charAt(i) == '旗' || address.charAt(i) == '岛' || address.charAt(i) == '县'))
                            countyLong = i + 1;
                        else
                            countyLong = i;
                        return countyName;
                    }
                }else if (address.length() >= i + 2 && lastStr.equals("域")){
                    if ((countyName.substring(0,countyName.length() - 2)).equals(address.substring(0,i))){
                        cCode = (String) cMap.get("code");
                        if (address.substring(i,i + 2) == "海域")
                            countyLong = i + 2;
                        else
                            countyLong = i;
                        return countyName;
                    }
                }
            }
        }
        return "";
    }

    private static String getCityWithoutProvince(String address){
        for (Map.Entry<String,Object> e: AreaCodeConvert.map.entrySet()){
            cMap = ((LinkedHashMap<String,Object>)(((LinkedHashMap<String,Object>)e.getValue())).get("children"));
            if (cMap == null)
            {
                continue;
            }

            for (Map.Entry<String,Object> entry: cMap.entrySet()){
                for (int i = 1; i <= address.length(); i++){
                    LinkedHashMap<String,Object> cMap = (LinkedHashMap<String,Object>)(entry.getValue());
                    String cName = (String) cMap.get("name");
                    String lastStr = cName.substring(cName.length() - 1);
                    if (lastStr.equals("市") || lastStr.equals("盟") || lastStr.equals("县")){
                        if ((cName.substring(0,cName.length() - 1)).equals(address.substring(0,i))){
                            cCode = (String) cMap.get("code");
                            if (address.length() >= i + 1 && (address.charAt(i) == '市' || address.charAt(i) == '盟' || address.charAt(i) == '县'))
                                cLong = i + 1;
                            else
                                cLong = i;
                            return cName;
                        }
                    }else if (lastStr.equals("区")){
                        if ((cName.substring(0,cName.length() - 2)).equals(address.substring(0,i))){
                            cCode = (String) cMap.get("code");
                            if (address.length() >= i + 2 && address.substring(i,i + 2).equals("地区"))
                                cLong = i + 2;
                            else
                                cLong = i;
                            return cName;
                        }

                    }else if (lastStr.equals("州")){
                        if ((cName.substring(0,cName.length() - 3)).equals(address.substring(0,i))){
                            cCode = (String) cMap.get("code");
                            if (address.length() >= i + 3 && address.substring(i,i + 3).equals("自治州"))
                                cLong = i + 3;
                            else
                                cLong = i;
                            return cName;
                        }
                    }

                }
            }
        }

        return "";
    }

    private static String getCountyWithoutProvinceAndCity(String address){
        for (Map.Entry<String,Object> e: AreaCodeConvert.map.entrySet()){
            cMap = ((LinkedHashMap<String,Object>)(((LinkedHashMap<String,Object>)e.getValue())).get("children"));
            if (cMap == null)
                continue;
            for (Map.Entry<String,Object> entry: cMap.entrySet()){
                countyMapList = (ArrayList<LinkedHashMap<String,Object>>)(((LinkedHashMap<String,Object>)entry.getValue())).get("children");
                if (countyMapList == null)
                    continue;
                for (LinkedHashMap<String,Object> map : countyMapList){
                    String countyName = (String) map.get("name");
                    String lastStr = countyName.substring(countyName.length() - 1);
                    for (int i = 1; i <= address.length(); i++){
                        if (lastStr.equals("区") || lastStr.equals("市") || lastStr.equals("旗") || lastStr.equals("岛") ||  lastStr.equals("县")){
                            if ((countyName.substring(0,countyName.length() - 1)).equals(address.substring(0,i))){
                                cCode = (String) cMap.get("code");
                                if (address.length() >= i + 1 && (address.charAt(i) == '区' || address.charAt(i) == '市' || address.charAt(i) == '旗' || address.charAt(i) == '岛' || address.charAt(i) == '县'))
                                    countyLong = i + 1;
                                else
                                    countyLong = i;
                                return countyName;
                            }
                        }else if (lastStr.equals("域")){
                            if ((countyName.substring(0,countyName.length() - 2)).equals(address.substring(0,i))){
                                cCode = (String) cMap.get("code");
                                if (address.length() >= i + 2 && address.substring(i,i + 2) == "海域")
                                    countyLong = i + 2;
                                else
                                    countyLong = i;
                                return countyName;
                            }
                        }
                    }
                }
            }
        }

        return "";
    }
    private static String getCountyWithoutCity(String address){
        cMap = ((LinkedHashMap<String,Object>)(((LinkedHashMap<String,Object>)AreaCodeConvert.map.get(pCode.substring(0,2)))).get("children"));
        if (cMap == null)
            return "";
        for (Map.Entry<String,Object> entry: cMap.entrySet()){
            countyMapList = (ArrayList<LinkedHashMap<String,Object>>)(((LinkedHashMap<String,Object>)entry.getValue())).get("children");
            if (countyMapList == null)
                return "";
            for (LinkedHashMap<String,Object> map : countyMapList){
                String countyName = (String) map.get("name");
                String lastStr = countyName.substring(countyName.length() - 1);
                for (int i = 1; i <= address.length(); i++){
                    if (lastStr.equals("区") || lastStr.equals("市") || lastStr.equals("旗") || lastStr.equals("岛") ||  lastStr.equals("县")){
                        if ((countyName.substring(0,countyName.length() - 1)).equals(address.substring(0,i))){
                            cCode = (String) cMap.get("code");
                            if (address.length() >= i + 1 && (address.charAt(i) == '区' || address.charAt(i) == '市' || address.charAt(i) == '旗' || address.charAt(i) == '岛' || address.charAt(i) == '县'))
                                countyLong = i + 1;
                            else
                                countyLong = i;
                            return countyName;
                        }
                    }else if (lastStr.equals("域")){
                        if ((countyName.substring(0,countyName.length() - 2)).equals(address.substring(0,i))){
                            cCode = (String) cMap.get("code");
                            if (address.length() >= i + 2 && address.substring(i,i + 2) == "海域")
                                countyLong = i + 2;
                            else
                                countyLong = i;
                            return countyName;
                        }
                    }
                }
            }

        }

        return "";
    }
}
