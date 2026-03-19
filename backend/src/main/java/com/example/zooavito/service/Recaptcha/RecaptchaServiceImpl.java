package com.example.zooavito.service.Recaptcha;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecaptchaServiceImpl implements RecaptchaService {

    @Value("${google.recaptcha.secret}")
    private String secretKey;

    private final RestTemplate restTemplate;

    @Override
    public boolean verifyToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("secret", secretKey);
            map.add("response", token);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            ResponseEntity<com.example.zooavito.response.RecaptchaResponse> response = restTemplate.postForEntity(
                    "https://www.google.com/recaptcha/api/siteverify", entity, com.example.zooavito.response.RecaptchaResponse.class);

            if (response.getBody() != null && response.getBody().isSuccess()) {
                log.info("Google reCAPTCHA verification successful. Score: {}", response.getBody().getScore());
                return true;
            } else {
                log.warn("Google reCAPTCHA failed: {}",
                        response.getBody() != null ? response.getBody().getErrorCodes() : "no body");
                return false;
            }
        } catch (Exception e) {
            log.error("Error verifying Google reCAPTCHA token", e);
            return false;
        }
    }
}
