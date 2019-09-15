import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @author yjn
 * @creatTime 2019/9/12 - 20:29
 */
public class AreaCodeConvert {
    public  static Map<String, Object> map = new LinkedHashMap<String, Object>();
    @SuppressWarnings("unchecked")
    public static void Convert() throws Exception {
        // 到政府网站复制列表，保存到txt中，我找到的2017年的
        // http://www.mca.gov.cn/article/sj/tjbz/a/2017/20178/201709251028.html
        // 直辖市和特别行政区比较讨厌，只有省的数据，没有市，或者没有区县，自己手动添加
        // 需要手动添加的有：北京，天津，上海，重庆，台湾，香港，澳门
        File f = new File(AreaCodeConvert.class.getClassLoader().getResource("address.txt").getFile());
        // 注意转码
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));

        String l = null;



        while ((l = r.readLine()) != null) {
            boolean isProvince = false, isCity = false, isRegion = false;
            // 左代码，右名称

            String[] split = StringUtils.split(l);
            String code = split[0];
            String name = split[1];
            // 代码规律：省级的都是xx0000,市级都是xxyy00
            isProvince = "0000".equals(StringUtils.substring(code, 2, 6));
            if (!isProvince) {
                isCity = "00".equals(StringUtils.substring(code, 4, 6));
            }
            if (!isCity) {
                isRegion = true;
            }
            if (isProvince) {
                // 存省级
                Map<String, Object> pMap = new LinkedHashMap<String, Object>();
                map.put(StringUtils.substring(code, 0, 2), pMap);
                pMap.put("code", code);
                pMap.put("name", name);
            } else if (isCity) {
                // 市级存到升级的children中
                Map<String, Object> pMap = (Map<String, Object>) map.get(StringUtils.substring(code, 0, 2));
                Map<String, Object> cMap = (Map<String, Object>) pMap.get("children");
                if (cMap == null) {
                    cMap = new LinkedHashMap<String, Object>();
                    pMap.put("children", cMap);
                }
                Map<String, Object> ccMap = new LinkedHashMap<String, Object>();
                cMap.put(StringUtils.substring(code, 0, 4), ccMap);
                ccMap.put("code", code);
                ccMap.put("pCode", StringUtils.substring(code, 0, 2) + "0000");
                ccMap.put("name", name);
            } else if (isRegion) {
                // 区级存到市级的children中
                Map<String, Object> pMap = (Map<String, Object>) map.get(StringUtils.substring(code, 0, 2));
                Map<String, Object> cMap = (Map<String, Object>) pMap.get("children");
                Map<String, Object> ccMap = (Map<String, Object>) cMap.get(StringUtils.substring(code, 0, 4));
                // 坑爹的情况是有些是县级市，有些没有对应市的县，不过根据列表发现只是找到上一个就好了
                if (ccMap == null) {
                    List<Map.Entry<String, Object>> cList = new ArrayList<Map.Entry<String, Object>>(cMap.entrySet());
                    ccMap = (Map<String, Object>) cList.get(cList.size() - 1).getValue();
                }
                List<Map<String, Object>> rList = (List<Map<String, Object>>) ccMap.get("children");
                if (rList == null) {
                    rList = new ArrayList<Map<String, Object>>();
                    ccMap.put("children", rList);
                }
                Map<String, Object> rMap = new LinkedHashMap<String, Object>();
                rMap.put("code", code);
                rMap.put("pCode", StringUtils.substring(code, 0, 4) + "00");
                rMap.put("name", name);
                rList.add(rMap);
            }
        }
//        // Map不好看，转成List格式的
//        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>((Collection<? extends Map<String, Object>>) map.values());
//        for (Map<String, Object> m : result) {
//            Map<String, Object> c = (Map<String, Object>) m.get("children");
//            if (Emptys.isNotEmpty(c)) {// 台湾、香港、澳门就一个地方
//                m.put("children", new ArrayList<Map<String, Object>>((Collection<? extends Map<String, Object>>) c.values()));
//            }
//        }
//        ObjectMapper jsonMapper = new ObjectMapper();
//        jsonMapper.setSerializationInclusion(Include.NON_DEFAULT);
//        jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//        System.out.println(jsonMapper.writeValueAsString(result));
        // 关闭，也懒得写try...catch了
        r.close();
    }

}
