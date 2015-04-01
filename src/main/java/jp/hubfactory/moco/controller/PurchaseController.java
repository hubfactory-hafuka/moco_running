package jp.hubfactory.moco.controller;

import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.form.PurchaseGirlForm;
import jp.hubfactory.moco.form.PurchaseVoiceForm;
import jp.hubfactory.moco.service.PurchaseService;
import jp.hubfactory.moco.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/purchase")
public class PurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private UserService userService;

    /**
     * ボイスセット購入処理
     * @param form
     * @return
     */
    @RequestMapping(value = "/voice", method = RequestMethod.POST)
    public ResponseEntity<Boolean> voice(@RequestBody PurchaseVoiceForm form) {

        // ユーザー情報取得
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + form.getUserId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
        boolean execFlg = purchaseService.purchaseVoiceSet(form.getUserId(), form.getSetId());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * ガール購入処理
     * @param form
     * @return
     */
    @RequestMapping(value = "/girl", method = RequestMethod.POST)
    public ResponseEntity<Boolean> girl(@RequestBody PurchaseGirlForm form) {

        // ユーザー情報取得
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + form.getUserId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        // ユーザーガール情報取得
        UserGirl userGirl = userService.getUserGirl(form.getUserId(), form.getGirlId());
        // 既に購入済みの場合
        if (userGirl != null) {
            logger.error("userGirl already exists. userId=" + form.getUserId() + " girlId=" + form.getGirlId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        boolean execFlg = purchaseService.purchaseGirl(form.getUserId(), form.getGirlId());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
