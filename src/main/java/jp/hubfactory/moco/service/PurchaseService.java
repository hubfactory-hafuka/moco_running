package jp.hubfactory.moco.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hubfactory.moco.entity.MstVoiceSet;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.entity.UserGirlVoiceKey;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.repository.MstVoiceSetRepository;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.util.DateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PurchaseService {

    @Autowired
    private MstVoiceSetRepository mstVoiceSetRepository;
    @Autowired
    private UserGirlVoiceRepository userGirlVoiceRepository;

    public boolean purchaseVoiceSet(Long userId, Integer setId, Integer girlId) {

        // ボイスセットリスト取得
        List<MstVoiceSet> mstVoiceSetList = mstVoiceSetRepository.findBySetId(setId);
        if (CollectionUtils.isEmpty(mstVoiceSetList)) {
            return false;
        }

        // ボイスIDのSetオブジェクト生成
        Set<Integer> voiceIdSet = new HashSet<Integer>();
        for (MstVoiceSet mstVoiceSet : mstVoiceSetList) {
            voiceIdSet.add(mstVoiceSet.getMstVoiceSetKey().getVoiceId());
        }

        // ユーザーのボイス情報取得
        List<UserGirlVoice> userGirlVoiceList = userGirlVoiceRepository.findByUserGirlVoiceKeyUserIdAndUserGirlVoiceKeyGirlId(userId, girlId);
        if (CollectionUtils.isNotEmpty(userGirlVoiceList)) {

            for (UserGirlVoice userGirlVoice : userGirlVoiceList) {

                if (voiceIdSet.contains(userGirlVoice.getUserGirlVoiceKey().getVoiceId())) {
                    userGirlVoice.setStatus(UserVoiceStatus.OF.getKey());
                }
            }
        } else {

            Date nowDate = DateUtils.getNowDate();
            for (MstVoiceSet mstVoiceSet : mstVoiceSetList) {
                UserGirlVoiceKey key = new UserGirlVoiceKey(userId, girlId, mstVoiceSet.getMstVoiceSetKey().getVoiceId());
                UserGirlVoice record = new UserGirlVoice(key, UserVoiceStatus.OF.getKey(), nowDate,nowDate);
                userGirlVoiceRepository.save(record);
            }
        }

        return true;
    }

    public boolean purchaseCharactor(Long userId, Integer charactorId) {




        return false;
    }

}
