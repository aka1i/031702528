import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yjn
 * @creatTime 2019/9/11 - 18:45
 */
public class AddressUtil {

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
        String regex="(?<province>[^省]+自治区|.*?省|.*?行政区)?(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)?(?<county>[^县]+县|.+?区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇|.+街道)?(?<village>.*)";
        Matcher m= Pattern.compile(regex).matcher(address);
        JSONArray addressArray = new JSONArray();
        if (m.find()){
            province=m.group("province");
            city=m.group("city");
            if (province == null && city != null)
                province = city.substring(0,city.length() - 1);

            addressArray.put(province==null?"":province.trim());
            addressArray.put(city==null?"":city.trim());
            county=m.group("county");
            addressArray.put(county==null?"":county.trim());
            town=m.group("town");
            addressArray.put( town==null?"":town.trim());
            village=m.group("village");
            addressArray.put(village==null?"":village.trim());
        }
        jsonObject.put("地址",addressArray);

        return jsonObject;
    }

}
