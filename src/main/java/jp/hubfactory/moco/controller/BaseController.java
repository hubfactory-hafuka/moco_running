package jp.hubfactory.moco.controller;

import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private UserService userService;

    /**
     * ユーザー認証チェック
     * @param userId ユーザーID
     * @param token token
     * @return true:認証OK/false:認証NG
     */
    protected boolean checkAuth(Long userId, String token) {
        if (StringUtils.isEmpty(token)) {
            logger.error("token is null. userId=" + userId);
            return false;
        }
        User user = userService.getUser(userId);
        if (user == null) {
            logger.error("user is null. userId=" + userId);
            return false;
        }
        if (!StringUtils.equals(token, user.getToken())) {
            logger.error("token is invalid. userId=" + userId);
            return false;
        }
        return true;
    }
}
