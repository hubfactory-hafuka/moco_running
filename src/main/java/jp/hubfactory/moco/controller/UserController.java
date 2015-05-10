package jp.hubfactory.moco.controller;

import jp.hubfactory.moco.bean.LoginBean;
import jp.hubfactory.moco.bean.UserBean;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.entity.UserTakeover;
import jp.hubfactory.moco.form.BaseForm;
import jp.hubfactory.moco.form.CreateUserForm;
import jp.hubfactory.moco.form.GirlFavoriteForm;
import jp.hubfactory.moco.form.LoginForm;
import jp.hubfactory.moco.form.TakeoverForm;
import jp.hubfactory.moco.service.InformationService;
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
@RequestMapping(value="/user")
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private InformationService informationService;

    /**
     * ユーザー登録
     * @param user
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<LoginBean> create(@Validated @RequestBody CreateUserForm form) {
        LoginBean loginBean = userService.createUser(form.getUuId(), form.getUserName());
        if (loginBean == null) {
            return new ResponseEntity<LoginBean>(loginBean, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<LoginBean>(loginBean, HttpStatus.OK);
    }

    /**
     * ログイン
     * @param form
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginBean> login(@Validated @RequestBody LoginForm form) {
        LoginBean loginBean = userService.login(form.getUserId(), form.getUuId());
        if (loginBean == null) {
            return new ResponseEntity<LoginBean>(loginBean, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<LoginBean>(loginBean, HttpStatus.OK);
    }

    /**
     * ログアウト
     * @param form
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<Boolean> logout(@Validated @RequestBody BaseForm form) {
        boolean logoutFlg = userService.logout(form.getUserId(), form.getToken());
        if (!logoutFlg) {
            return new ResponseEntity<Boolean>(logoutFlg, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Boolean>(logoutFlg, HttpStatus.OK);
    }

    /**
     * ユーザー情報取得
     * @param user
     * @return
     */
    @RequestMapping(value = "/get-user", method = RequestMethod.POST)
    public ResponseEntity<UserBean> getUser(@Validated @RequestBody BaseForm form) {

        UserBean userBean = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<UserBean>(userBean, HttpStatus.UNAUTHORIZED);
        }
        userBean = userService.getUserBean(form.getUserId());
        userBean.setInfoBean(informationService.getInformation());
        return new ResponseEntity<UserBean>(userBean, HttpStatus.OK);
    }

    /**
     * お気に入り登録
     * @param form
     * @return
     */
    @RequestMapping(value = "/upd-favorite", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updFavorite(@RequestBody GirlFavoriteForm form) {

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
        }
        // ガール所持しているかの判定
        UserGirl userGirl = userService.getUserGirl(form.getUserId(), form.getGirlId());
        if (userGirl == null) {
            logger.error("userGirl is null. userId=" + form.getUserId() + " girlId=" + form.getGirlId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        // お気に入り設定処理
        boolean execFlg = userService.updFavoriete(form.getUserId(), form.getGirlId());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(value = "/issue-takeover", method = RequestMethod.POST)
    public ResponseEntity<UserTakeover> issueTakeover(@Validated @RequestBody BaseForm form) {

        UserTakeover userTakeover = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<UserTakeover>(userTakeover, HttpStatus.UNAUTHORIZED);
        }

        userTakeover = userService.issueTakeOverId(form.getUserId());
        if (userTakeover == null) {
            return new ResponseEntity<UserTakeover>(userTakeover, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<UserTakeover>(userTakeover, HttpStatus.OK);

    }

    @RequestMapping(value = "/takeover", method = RequestMethod.POST)
    public ResponseEntity<LoginBean> takeover(@Validated @RequestBody TakeoverForm form) {

        LoginBean loginBean = userService.takeover(form.getUserId(), form.getUuId(), form.getTakeoverId());
        if (loginBean == null) {
            return new ResponseEntity<LoginBean>(loginBean, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<LoginBean>(loginBean, HttpStatus.OK);

    }
}
