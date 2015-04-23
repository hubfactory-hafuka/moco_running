package jp.hubfactory.moco.controller;

import java.util.List;

import jp.hubfactory.moco.bean.GirlBean;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.form.BaseForm;
import jp.hubfactory.moco.form.GetGirlForm;
import jp.hubfactory.moco.service.GirlService;
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
@RequestMapping(value="/girl")
public class GirlController {

    private static final Logger logger = LoggerFactory.getLogger(GirlController.class);

    @Autowired
    private GirlService girlService;
    @Autowired
    private UserService userService;

    /**
     * 対象ガール取得
     * @param form
     * @return
     */
    @RequestMapping(value = "/get-one", method = RequestMethod.POST)
    public ResponseEntity<GirlBean> getOne(@Validated @RequestBody GetGirlForm form) {

        // ガール情報取得
        GirlBean girlBean = girlService.selectMstGirl(form.getUserId(), form.getGirlId());
        if (girlBean == null) {
            logger.error("mstGirl is null. girlId=" + form.getGirlId());
            return new ResponseEntity<GirlBean>(girlBean, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<GirlBean>(girlBean, HttpStatus.OK);
    }

    /**
     * ガール一覧取得
     * @param form
     * @return
     */
    @RequestMapping(value = "/get-list", method = RequestMethod.POST)
    public ResponseEntity<List<GirlBean>> getList(@Validated @RequestBody BaseForm form) {

        List<GirlBean> girlList = null;

        // ユーザー情報取得
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + form.getUserId());
            return new ResponseEntity<List<GirlBean>>(girlList, HttpStatus.BAD_REQUEST);
        }

        girlList = girlService.selectActiveGirls(form.getUserId());

        return new ResponseEntity<List<GirlBean>>(girlList, HttpStatus.OK);
    }
}
