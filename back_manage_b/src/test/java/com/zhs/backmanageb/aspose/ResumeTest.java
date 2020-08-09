package com.zhs.backmanageb.aspose;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * @author: zhs
 * @date: 2020/8/9 8:33
 */
public class ResumeTest {
    @Test
    public void test(){
        RestTemplate restTemplate = new RestTemplate();
        JSONObject json = new JSONObject();
        json.put("text","get测试");
        String jsonObject = restTemplate.postForObject("http://resume.carltrip.com/api/resume/resumeTest", "\"赵文泉，男，汉族，1963年11月出生，安徽长丰人，1986年7月参加工作，1994年11月入党， 本科学历（1986年07毕业于新疆大学中文系），学士学位。1982.09 新疆大学汉语言文学专业学习（大学）1986.07 吐鲁番地区劳动人事处科技干部科科员1991.05 吐鲁番地区劳动人事处办公室副主任\\n\" +\n" +
                "                \"　　1992.09 吐鲁番地区政协工委办公室主任科员\\n\" +\n" +
                "                \"　　1993.08 吐鲁番地委组织部电教中心副主任（正科）\\n\" +\n" +
                "                \"　　1995.03 吐鲁番地委组织部调研室主任\\n\" +\n" +
                "                \"　　1996.05 吐鲁番地委办公室副主任\\n\" +\n" +
                "                \"　　2002.06 吐鲁番地委副秘书长、办公室主任\\n\" +\n" +
                "                \"　　2004.11 鄯善县委书记（鄯善人大党组书记、鄯善工业园区党工委书记）\\n\" +\n" +
                "                \"　　2011.07 吐鲁番地委委员、宣传部长\\n\" +\n" +
                "                \"　　2015.02 吐鲁番地委委员、行署常务副专员、宣传部长\\n\" +\n" +
                "                \"　　2015.04 吐鲁番市委常委、行署常务副专员\\n\" +\n" +
                "                \"　　2015.07 吐鲁番市委常委，市政府常务副市长\\n\" +\n" +
                "                \"　　2016.07 吐鲁番市委副书记，市政府常务副市长\\n\" +\n" +
                "                \"　　2017.02 吐鲁番市政协主席\\n\" +\n" +
                "                \"　　2017.03 克拉玛依市委书记 \"", String.class);
        System.out.println(jsonObject);
    }
    @Test
    public void testcv(){
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("user-id", "test20200605");
        requestHeaders.add("user-password", "test20200601");
        //body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        String encode = Base64.encode(new File("D:\\test.docx"));
        requestBody.add("cv_text", encode);
        requestBody.add("file_name", "docx");
        requestBody.add("is_return_txt", "1");
        //HttpEntity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        //post
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://test.cvhr.cn/cvparse_base64_v20200601", requestEntity, String.class);
        System.out.println(responseEntity.getBody());
    }
}
