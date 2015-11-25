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
     * 交換ログ
     * @param userId
     * @param purchaseType
     * @param id
     * @param usePoint
     * @param beforePoint
     * @param afterPoint
     */
    public static void exchangeLog(Long userId, PurchaseType purchaseType, Integer id, Long usePoint, Long beforePoint, Long afterPoint) {
        Logger logger = LoggerFactory.getLogger("exchangeLog");
        Object[] logs = {userId, purchaseType, id, usePoint, beforePoint, afterPoint};
        String log = StringUtils.join(logs, "\t");
        logger.info(log);
    }
}
