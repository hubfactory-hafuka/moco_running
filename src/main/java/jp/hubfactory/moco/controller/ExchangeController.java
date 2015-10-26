package jp.hubfactory.moco.controller;

import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.form.ExchangeForm;
import jp.hubfactory.moco.service.ExchangeService;
import jp.hubfactory.moco.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/exchange")
public class ExchangeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeController.class);

    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private UserService userService;

    /**
     * ガール交換処理
     * @param form
     * @return
     */
    @RequestMapping(value = "/girl", method = RequestMethod.POST)
    public ResponseEntity<Boolean> girl(@Validated @RequestBody ExchangeForm form) {

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
        }

        // ユーザーガール情報取得
        UserGirl userGirl = userService.getUserGirl(form.getUserId(), form.getGirlId());
        // 既に購入済みの場合
        if (userGirl != null) {
            logger.error("userGirl already exists. userId=" + form.getUserId() + " girlId=" + form.getGirlId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        boolean execFlg = exchangeService.exchangeGirl(form.getUserId(), form.getGirlId());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * ボイスセット交換処理
     * @param form
     * @return
     */
    @RequestMapping(value = "/voice", method = RequestMethod.POST)
    public ResponseEntity<Boolean> voice(@Validated @RequestBody ExchangeForm form) {

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
        }

        boolean execFlg = exchangeService.exchangeVoiceSet(form.getUserId(), form.getSetId());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
