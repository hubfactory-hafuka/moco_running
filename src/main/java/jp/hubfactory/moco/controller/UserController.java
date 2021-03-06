package jp.hubfactory.moco.controller;

import java.io.IOException;

import jp.hubfactory.moco.bean.LoginBean;
import jp.hubfactory.moco.bean.UserBean;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.entity.UserTakeover;
import jp.hubfactory.moco.form.BaseForm;
import jp.hubfactory.moco.form.CreateUserForm;
import jp.hubfactory.moco.form.GirlFavoriteForm;
import jp.hubfactory.moco.form.LoginForm;
import jp.hubfactory.moco.form.TakeoverForm;
import jp.hubfactory.moco.form.UpdateNameForm;
import jp.hubfactory.moco.form.UpdateProfileForm;
import jp.hubfactory.moco.service.InformationService;
import jp.hubfactory.moco.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
        userBean = userService.getUserBean(form.getUserId(), true);
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

    /**
     * ニックネーム更新登録
     * @param form
     * @return
     */
    @RequestMapping(value = "/upd-name", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updName(@RequestBody UpdateNameForm form) {

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
        }
        // ユーザー情報存在チェック判定
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + form.getUserId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        // ニックネーム変更処理
        boolean execFlg = userService.updName(form.getUserId(), form.getName());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * 引継コード発行
     * @param form
     * @return
     */
    @RequestMapping(value = "/issue-takeover", method = RequestMethod.POST)
    public ResponseEntity<UserTakeover> issueTakeover(@Validated @RequestBody BaseForm form) {

        UserTakeover userTakeover = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<UserTakeover>(userTakeover, HttpStatus.UNAUTHORIZED);
        }

        userTakeover = userService.issueTakeOverCode(form.getUserId());
        if (userTakeover == null) {
            return new ResponseEntity<UserTakeover>(userTakeover, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<UserTakeover>(userTakeover, HttpStatus.OK);

    }

    /**
     * 引継処理
     * @param form
     * @return
     */
    @RequestMapping(value = "/takeover", method = RequestMethod.POST)
    public ResponseEntity<LoginBean> takeover(@Validated @RequestBody TakeoverForm form) {

        LoginBean loginBean = userService.takeover(form.getUserId(), form.getUuId(), form.getTakeoverCode());
        if (loginBean == null) {
            return new ResponseEntity<LoginBean>(loginBean, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<LoginBean>(loginBean, HttpStatus.OK);

    }

    /**
     * 身長更新登録
     * @param form
     * @return
     */
    @RequestMapping(value = "/upd-height", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updHeight(@RequestBody UpdateProfileForm form) {

        if (StringUtils.isEmpty(form.getHeight())) {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
        }
        // ユーザー情報存在チェック判定
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + form.getUserId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        // 体重更新処理
        boolean execFlg = userService.updHeight(form.getUserId(), form.getHeight());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * 体重更新登録
     * @param form
     * @return
     */
    @RequestMapping(value = "/upd-weight", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updWeight(@RequestBody UpdateProfileForm form) {

        if (StringUtils.isEmpty(form.getWeight())) {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
        }
        // ユーザー情報存在チェック判定
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + form.getUserId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        // 体重更新処理
        boolean execFlg = userService.updWeight(form.getUserId(), form.getWeight());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }


    /**
     * プロフィール情報取得
     * @param user
     * @return
     */
    @RequestMapping(value = "/get-profile", method = RequestMethod.POST)
    public ResponseEntity<UserBean> getProfileInfo(@Validated @RequestBody BaseForm form) {

        UserBean userBean = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<UserBean>(userBean, HttpStatus.UNAUTHORIZED);
        }
        userBean = userService.getUserBean(form.getUserId(), false);
        userBean.setInfoBean(informationService.getInformation());
        return new ResponseEntity<UserBean>(userBean, HttpStatus.OK);
    }

    /**
     * プロフィール画像アップロード
     * @param form
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/upd-profile-image", method = RequestMethod.POST)
    public ResponseEntity<Boolean> upload(@RequestBody UpdateProfileForm form) throws IOException {

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
        }
        // ユーザー情報存在チェック判定
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + form.getUserId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        // プロフ画像データ更新処理
        boolean execFlg = userService.updProfileImage(form.getUserId(), form.getImageData());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * ポイント更新処理
     * @param form
     * @return
     */
    @RequestMapping(value = "/upd-point", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updPoint(@RequestBody UpdateProfileForm form) {

        if (form.getPoint() == null || form.getPoint().longValue() <= 0) {
            return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
        }

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
        }
        // ユーザー情報存在チェック判定
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            logger.error("user is null. userId=" + form.getUserId());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        // ポイント更新処理
        boolean execFlg = userService.updUserPoint(form.getUserId(), form.getPoint());
        return new ResponseEntity<Boolean>(execFlg, execFlg == true ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }


}
