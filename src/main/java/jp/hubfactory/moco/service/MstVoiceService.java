package jp.hubfactory.moco.service;

import java.util.List;

import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.MstVoiceKey;
import jp.hubfactory.moco.repository.MstVoiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MstVoiceService {

    @Autowired
    private MstVoiceRepository mstVoiceRepository;

    public List<MstVoice> findAll() {
        return mstVoiceRepository.findAll();
    }

    public MstVoice findOne(Integer girlId, Integer voiceId) {
        MstVoiceKey key = new MstVoiceKey(girlId, voiceId);
        return mstVoiceRepository.findOne(key);
    }

    public List<MstVoice> findByGirlId(Integer girlId) {
        return mstVoiceRepository.findByMstVoiceKeyGirlId(girlId);
    }
}
