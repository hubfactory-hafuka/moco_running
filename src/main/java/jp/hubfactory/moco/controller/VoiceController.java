package jp.hubfactory.moco.controller;

import java.util.List;

import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.MstVoiceKey;
import jp.hubfactory.moco.service.MstVoiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api/voice")
public class VoiceController {

    @Autowired
    private MstVoiceService mstVoiceService;

    /**
     * ガールの音声一覧取得
     * @param girlId ガールID
     * @return
     */
    @RequestMapping(value = "/get-girl-user", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public List<MstVoice> getGirlVoice(@Validated @RequestBody MstVoiceKey mstVoiceKey) {
        System.out.println("param girlId:" + mstVoiceKey.getGirlId());
        return mstVoiceService.findByGirlId(mstVoiceKey.getGirlId());
    }
}
