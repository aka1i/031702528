import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yjn
 * @creatTime 2019/9/11 - 15:58
 */
public class Main {
    public static void main(String[] args) {
        try {
            AreaCodeConvert.Convert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(AddressUtil.addressResolutionVersion2("李四,福建省福州13756899511市鼓楼区鼓西街道湖滨路110号湖滨大厦一层"));
        System.out.println(AddressUtil.addressResolutionVersion2("张三,福建福州市闽13599622362侯县上街镇福州大学10#111"));
        System.out.println(AddressUtil.addressResolutionVersion2("王五,福建省福州市鼓楼18960221533区五一北路123号福州鼓楼医院"));
        System.out.println(AddressUtil.addressResolutionVersion2("小美,北京市东15822153326城区交道口东大街1号北京市东城区人民法院"));
        System.out.println(AddressUtil.addressResolutionVersion2("小陈,广东省东莞市凤岗13965231525镇凤平路13号"));
    }



}
