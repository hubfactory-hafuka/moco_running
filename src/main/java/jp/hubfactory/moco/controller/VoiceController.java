package jp.hubfactory.moco.controller;

import java.util.List;

import jp.hubfactory.moco.bean.UserGirlVoiceBean;
import jp.hubfactory.moco.form.UserGirlVoiceForm;
import jp.hubfactory.moco.service.VoiceService;

import org.apache.commons.collections.CollectionUtils;
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
@RequestMapping(value="/voice")
public class VoiceController {

    private static final Logger logger = LoggerFactory.getLogger(VoiceController.class);

    @Autowired
    private VoiceService voiceService;

    @RequestMapping(value = "/get-user-girl-voice", method = RequestMethod.POST)
    public ResponseEntity<List<UserGirlVoiceBean>> voice(@Validated @RequestBody UserGirlVoiceForm form) {
        // ユーザー情報取得
        List<UserGirlVoiceBean> userGirlVoiceBeanList = voiceService.getUserGirlVoiceBeanList(form.getUserId(), form.getGirlId());
        if (CollectionUtils.isEmpty(userGirlVoiceBeanList)) {
            logger.error("userGirlVoiceBeanList is empty. userId=" + form.getUserId());
            return new ResponseEntity<List<UserGirlVoiceBean>>(userGirlVoiceBeanList, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List<UserGirlVoiceBean>>(userGirlVoiceBeanList, HttpStatus.OK);
    }
}
