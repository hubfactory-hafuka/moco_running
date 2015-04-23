package jp.hubfactory.moco.service;

import jp.hubfactory.moco.util.MD5Utils;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

//    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
//
    private static final int TOKEN_LENGTH = 6;

    /**
     * トークン取得
     * @return
     */
    String getToken(String uuId) {
        String cryptValue = uuId + RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        return MD5Utils.crypt(cryptValue);
//
//
//        String randomValue = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
//        try {
//            return CryptUtils.encrypt(randomValue, uuId);
//        } catch (Exception e) {
//            logger.error("token生成エラー");
//            e.printStackTrace();
//            return null;
//        }
    }

}
