package jp.hubfactory.moco.controller;

import java.util.List;

import jp.hubfactory.moco.bean.UserBean;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.entity.UserGirlVoiceKey;
import jp.hubfactory.moco.form.GetUserForm;
import jp.hubfactory.moco.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * ユーザー登録
     * @param user
     * @return
     */
    @RequestMapping(value = "/create-user", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Validated @RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * ユーザー情報取得
     * @param user
     * @return
     */
    @RequestMapping(value = "/get-user", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public UserBean getUser(@Validated @RequestBody GetUserForm form) {
        return userService.getUser(form.getUserId());
    }

    /**
     * ユーザーガール音声情報取得
     * @param userGirlVoiceKey
     * @return
     */
    @RequestMapping(value = "/get-user-girl-voice", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public List<UserGirlVoice> getUserGirlVoice(@Validated @RequestBody UserGirlVoiceKey userGirlVoiceKey) {
        System.out.println("#getUserGirlVoice. userId=" + userGirlVoiceKey.getUserId() + " girlId=" + userGirlVoiceKey.getGirlId());
        return userService.findUserGirlVoice(userGirlVoiceKey.getUserId(), userGirlVoiceKey.getGirlId());
    }
}
