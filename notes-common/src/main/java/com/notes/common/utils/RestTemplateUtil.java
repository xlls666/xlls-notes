package com.notes.common.utils;

import com.notes.common.utils.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RestTemplateUtil {
    private static final Logger log = LoggerFactory.getLogger(RestTemplateUtil.class);

    private static <T> ResponseEntity<T> handleRequest(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
        try {
            log.info("RestTemplateUtil.handleRequest url:{}", url);
            return SpringUtils.getBean(RestTemplate.class).exchange(url, method, requestEntity, responseType);
        } catch (HttpClientErrorException e) {
            // 客户端错误（4xx）
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (HttpServerErrorException e) {
            // 服务器错误（5xx）
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (ResourceAccessException e) {
            // 资源访问异常（如超时）
            return ResponseEntity.status(504).body(null); // 504 Gateway Timeout
        } catch (RestClientException e) {
            // 其他异常
            return ResponseEntity.status(500).body(null);
        }
    }

    public static <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
        return handleRequest(url, method, requestEntity, responseType);
    }

    public static <T> ResponseEntity<T> getForObject(String url, Class<T> responseType, Map<String,String> params) {
        if (params != null && !params.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(sb.indexOf("?") == -1 ? "?" : "&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            url = sb.toString();
        }
        return handleRequest(url, HttpMethod.GET, new HttpEntity<>(RestTemplateUtil.createDefaultHeaders()), responseType);
    }

    public static <T> ResponseEntity<T> postForObject(String url, Object request, Class<T> responseType) {
        return handleRequest(url, HttpMethod.POST, new HttpEntity<>(request, RestTemplateUtil.createDefaultHeaders()), responseType);
    }

    public static <T> ResponseEntity<T> putForObject(String url, Object request, Class<T> responseType) {
        return handleRequest(url, HttpMethod.PUT, new HttpEntity<>(request, RestTemplateUtil.createDefaultHeaders()), responseType);
    }

    public static ResponseEntity<Void> deleteForObject(String url) {
        return handleRequest(url, HttpMethod.DELETE, new HttpEntity<>(RestTemplateUtil.createDefaultHeaders()), Void.class);
    }

    public static HttpHeaders createDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        // 可以在这里添加默认的请求头
        // headers.set("Authorization", "Bearer " + token);
        return headers;
    }
}
