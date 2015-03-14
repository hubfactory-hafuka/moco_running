package jp.hubfactory.moco.service;

import java.util.List;

import jp.hubfactory.moco.entity.MstVoiceSet;
import jp.hubfactory.moco.repository.MstVoiceSetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MstVoiceSetService {

    @Autowired
    private MstVoiceSetRepository mstVoiceSetRepository;

    public List<MstVoiceSet> findMstVoiceSetList(Integer setId) {
        return mstVoiceSetRepository.findBySetId(setId);
    }

}
