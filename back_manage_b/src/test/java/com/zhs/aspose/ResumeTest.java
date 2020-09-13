package com.zhs.aspose;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspose.words.Document;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhs
 * @date: 2020/8/9 8:33
 */
@Slf4j
public class ResumeTest {
    @Test
    public void test() throws Exception {
        Document nodes = new Document("D:\\陈本宇.docx");

        String text = nodes.getText();
        log.info(text);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型，这里用json所以是MediaType.APPLICATION_JSON
        headers.setContentType(MediaType.APPLICATION_JSON);
        //RestTemplate带参传的时候要用HttpEntity<?>对象传递
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("resumeText", text);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> entity = restTemplate.postForEntity("http://resume.carltrip.com/api/resume/index", request, String.class);
        //获取3方接口返回的数据通过entity.getBody();它返回的是一个字符串；
        String body = entity.getBody();
        System.out.println(body);
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

    @Test
    public void testSplit(){
        String s = "1,2";
        String[] split = s.split(",");
        System.out.println(split.length);
        for (String s1 : split) {
            System.out.println(s1);
        }
    }
}
