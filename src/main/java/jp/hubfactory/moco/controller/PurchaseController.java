package jp.hubfactory.moco.controller;

import java.util.List;

import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.form.InputForm;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/purchase")
public class PurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/voice", method = RequestMethod.POST)
    public ResponseEntity<Boolean> voice(@RequestBody InputForm inputForm) {
        logger.info("PurchaseController#voice");

        // ユーザー情報取得
        User user = userService.findUserByUserId(inputForm.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + inputForm.getUserId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        boolean execFlg = purchaseService.purchaseVoiceSet(inputForm.getUserId(), inputForm.getSetId(), inputForm.getGirlId());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/chara", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public List<MstGirl> chara() {
        return null;
    }
}
