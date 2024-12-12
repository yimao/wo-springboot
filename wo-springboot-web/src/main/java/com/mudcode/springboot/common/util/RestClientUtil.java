package com.mudcode.springboot.common.util;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class RestClientUtil {

    private static final RestClient restClient = RestClient.create();

    private RestClientUtil() {
    }

    public static String get(String url, Map<String, String> params) {
        MultiValueMap<String, String> _params = new LinkedMultiValueMap<>();
        Optional.ofNullable(params).ifPresent(_params::setAll);
        UriComponents _url = UriComponentsBuilder.fromHttpUrl(url).encode().queryParams(_params).build();
        return restClient.get()
                .uri(_url.toUri())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new IOException("response code is " + response.getStatusCode().value());
                })
                .body(String.class);
    }

    public static String postForm(String url, Map<String, String> params) {
        MultiValueMap<String, String> _params = new LinkedMultiValueMap<>();
        Optional.ofNullable(params).ifPresent(_params::setAll);
        UriComponents _url = UriComponentsBuilder.fromHttpUrl(url).encode().build();
        return restClient.post()
                .uri(_url.toUri())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(_params)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new IOException("response code is " + response.getStatusCode().value());
                })
                .body(String.class);
    }

}
