package jp.hubfactory.moco.logger;

import jp.hubfactory.moco.enums.PurchaseType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MocoLogger {

    /**
     * 課金ログ
     * @param userId
     * @param purchaseType
     * @param id
     */
    public static void purchaseLog(Long userId, PurchaseType purchaseType, Integer id) {
        Logger logger = LoggerFactory.getLogger("purchaseLog");
        Object[] logs = {userId, purchaseType, id};
        String log = StringUtils.join(logs, "\t");
        logger.info(log);
    }

    /**
     * ユーザー登録ログ
     * @param userId
     */
    public static void createUserLog(Long userId, String userName) {
        Logger logger = LoggerFactory.getLogger("createUserLog");
        Object[] logs = {userId, userName};
        String log = StringUtils.join(logs, "\t");
        logger.info(log);
    }
}
