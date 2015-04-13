package jp.hubfactory.moco.controller;

import jp.hubfactory.moco.bean.UserBean;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.form.CreateUserForm;
import jp.hubfactory.moco.form.GetUserForm;
import jp.hubfactory.moco.form.GirlFavoriteForm;
import jp.hubfactory.moco.form.LoginForm;
import jp.hubfactory.moco.form.LogoutForm;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * ユーザー登録
     * @param user
     * @return
     */
    @RequestMapping(value = "/add-user", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Validated @RequestBody CreateUserForm form) {
        return userService.createUser(form.getLoginId(), form.getPassword(), form.getServiceId(), form.getUuId(), form.getUserName());
    }

    /**
     * ログイン
     * @param form
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Boolean> login(@Validated @RequestBody LoginForm form) {
        boolean loginFlg = userService.login(form.getLoginId(), form.getPassword(), form.getServiceId(), form.getUuId());
        if (!loginFlg) {
            return new ResponseEntity<Boolean>(loginFlg, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Boolean>(loginFlg, HttpStatus.OK);
    }

    /**
     * ログアウト
     * @param form
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<Boolean> logout(@Validated @RequestBody LogoutForm form) {
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
    @ResponseStatus(HttpStatus.OK)
    public UserBean getUser(@Validated @RequestBody GetUserForm form) {
        return userService.getUserBean(form.getUserId());
    }

    /**
     * お気に入り登録
     * @param form
     * @return
     */
    @RequestMapping(value = "/upd-favorite", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updFavorite(@RequestBody GirlFavoriteForm form) {
        // ユーザー情報取得
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + form.getUserId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
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
}
