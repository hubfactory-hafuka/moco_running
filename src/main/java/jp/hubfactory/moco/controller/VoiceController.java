package jp.hubfactory.moco.controller;

import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.bean.UserGirlVoiceBean;
import jp.hubfactory.moco.form.UserGirlVoiceForm;
import jp.hubfactory.moco.repository.MstVoiceRepository;
import jp.hubfactory.moco.service.VoiceService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
public class VoiceController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(VoiceController.class);

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private MstVoiceRepository mstVoiceRepository;

    /**
     * ユーザーガールボイス一覧取得
     * @param form
     * @return
     */
    @RequestMapping(value = "/get-user-girl-voice", method = RequestMethod.POST)
    public ResponseEntity<List<UserGirlVoiceBean>> voice(@Validated @RequestBody UserGirlVoiceForm form) {

        List<UserGirlVoiceBean> userGirlVoiceBeanList = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<List<UserGirlVoiceBean>>(userGirlVoiceBeanList, HttpStatus.UNAUTHORIZED);
        }

        // ユーザー情報取得
        userGirlVoiceBeanList = voiceService.getUserGirlVoiceBeanList(form.getUserId(), form.getGirlId());
        if (CollectionUtils.isEmpty(userGirlVoiceBeanList)) {
            logger.error("userGirlVoiceBeanList is empty. userId=" + form.getUserId());
            return new ResponseEntity<List<UserGirlVoiceBean>>(userGirlVoiceBeanList, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List<UserGirlVoiceBean>>(userGirlVoiceBeanList, HttpStatus.OK);
    }

    /**
     * 走る時のユーザーボイス一覧取得
     * @param form
     * @return
     */
    @RequestMapping(value = "/get-user-run-voice", method = RequestMethod.POST)
    public ResponseEntity<Map<Integer, List<UserGirlVoiceBean>>> runVoice(@Validated @RequestBody UserGirlVoiceForm form) {

        Map<Integer, List<UserGirlVoiceBean>> situationVoiceListMap = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<Map<Integer, List<UserGirlVoiceBean>>>(situationVoiceListMap, HttpStatus.UNAUTHORIZED);
        }

        // ユーザー情報取得
        situationVoiceListMap = voiceService.getRunVoiceList(form.getUserId(), form.getGirlId());
        if (MapUtils.isEmpty(situationVoiceListMap)) {
            logger.error("situationVoiceListMap is empty. userId=" + form.getUserId() + " girlId=" + form.getGirlId());
            return new ResponseEntity<Map<Integer, List<UserGirlVoiceBean>>>(situationVoiceListMap, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Map<Integer, List<UserGirlVoiceBean>>>(situationVoiceListMap, HttpStatus.OK);
    }
}
