package jp.hubfactory.moco.controller;

import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.form.GetGirlForm;
import jp.hubfactory.moco.service.GirlService;
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
@RequestMapping(value="/girl")
public class GirlController {

    private static final Logger logger = LoggerFactory.getLogger(GirlController.class);

    @Autowired
    private GirlService girlService;
    @Autowired
    private UserService userService;

//    @RequestMapping(value = "/get-all", method = RequestMethod.POST)
//    public ResponseEntity<List<MstGirl>> getAll() {
//        logger.info("GirlController#getAll");
//        List<MstGirl> mstGirlList = girlService.selectMstGirls();
//        return new ResponseEntity<List<MstGirl>>(mstGirlList, HttpStatus.OK);
//    }

    @RequestMapping(value = "/get-one", method = RequestMethod.POST)
    public ResponseEntity<MstGirl> getOne(@RequestBody GetGirlForm form) {

        MstGirl mstGirl = null;

        // ユーザー情報取得
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + form.getUserId());
            return new ResponseEntity<MstGirl>(mstGirl, HttpStatus.BAD_REQUEST);
        }

        // ガール所持しているか判定
        UserGirl userGirl = userService.getUserGirl(form.getUserId(), form.getGirlId());
        if (userGirl == null) {
            logger.error("girl not purchase. userId=" + form.getUserId() + " girlId=" + form.getGirlId());
            return new ResponseEntity<MstGirl>(mstGirl, HttpStatus.BAD_REQUEST);
        }

        // ガール情報取得
        mstGirl = girlService.selectMstGirl(form.getGirlId());
        if (mstGirl == null) {
            logger.error("mstGirl is null. girlId=" + form.getGirlId());
            return new ResponseEntity<MstGirl>(mstGirl, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<MstGirl>(mstGirl, HttpStatus.OK);
    }
}
