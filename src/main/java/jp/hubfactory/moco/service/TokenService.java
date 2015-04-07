package jp.hubfactory.moco.service;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private static final int TOKEN_LENGTH = 8;

    /**
     * トークン取得
     * @return
     */
    String getToken() {
        return RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
    }

}
