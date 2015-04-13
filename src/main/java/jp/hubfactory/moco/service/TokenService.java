package jp.hubfactory.moco.service;

import jp.hubfactory.moco.util.CryptUtils;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private static final int TOKEN_LENGTH = 8;

    /**
     * トークン取得
     * @return
     */
    String getToken(String uuId) {
        String randomValue = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        try {
            return CryptUtils.encrypt(uuId, randomValue);
        } catch (Exception e) {
            return null;
        }
    }

}
