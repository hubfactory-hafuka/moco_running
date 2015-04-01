package jp.hubfactory.moco.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hubfactory.moco.cache.MstGirlCache;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.cache.MstVoiceSetCache;
import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.MstVoiceSet;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.entity.UserGirlVoiceKey;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.enums.VoiceType;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PurchaseService {

    @Autowired
    private MstVoiceSetCache mstVoiceSetCache;
    @Autowired
    private MstGirlCache mstGirlCache;
    @Autowired
    private MstVoiceCache mstVoiceCache;
    @Autowired
    private UserGirlVoiceRepository userGirlVoiceRepository;
    @Autowired
    private UserService userService;

    /**
     * ボイスセット購入処理
     * @param userId
     * @param setId
     * @param girlId
     * @return
     */
    public boolean purchaseVoiceSet(Long userId, Integer setId) {

        // ボイスセットリスト取得
        List<MstVoiceSet> mstVoiceSetList = mstVoiceSetCache.getVoiceSet(setId);
        if (CollectionUtils.isEmpty(mstVoiceSetList)) {
            return false;
        }

        // ガールＩＤ
        Integer girlId = mstVoiceSetList.get(0).getMstVoiceSetKey().getGirlId();

        // ボイスIDのSetオブジェクト生成
        Set<Integer> voiceIdSet = new HashSet<Integer>();
        for (MstVoiceSet mstVoiceSet : mstVoiceSetList) {
            voiceIdSet.add(mstVoiceSet.getMstVoiceSetKey().getVoiceId());
        }

        // ユーザーのボイス情報取得
        List<UserGirlVoice> userGirlVoiceList = userGirlVoiceRepository.findByKeyUserIdAndKeyGirlId(userId, girlId);
        if (CollectionUtils.isNotEmpty(userGirlVoiceList)) {

            for (UserGirlVoice userGirlVoice : userGirlVoiceList) {

                if (voiceIdSet.contains(userGirlVoice.getKey().getVoiceId())) {
                    userGirlVoice.setStatus(UserVoiceStatus.ON.getKey());
                }
            }
        } else {

            Date nowDate = MocoDateUtils.getNowDate();
            for (MstVoiceSet mstVoiceSet : mstVoiceSetList) {
                UserGirlVoiceKey key = new UserGirlVoiceKey(userId, girlId, mstVoiceSet.getMstVoiceSetKey().getVoiceId());
                UserGirlVoice record = new UserGirlVoice(key, UserVoiceStatus.ON.getKey(), nowDate,nowDate);
                userGirlVoiceRepository.save(record);
            }
        }

        return true;
    }

    /**
     * ガール購入処理
     * @param userId
     * @param girlId
     * @return
     */
    public boolean purchaseGirl(Long userId, Integer girlId) {

        Date nowDate = MocoDateUtils.getNowDate();

        // ガール情報取得
        MstGirl mstGirl = mstGirlCache.getGirl(girlId);
        if (mstGirl == null) {
            return false;
        }
        // 台詞一覧取得
        List<MstVoice> mstVoiceList = mstVoiceCache.getVoiceList(girlId);
        if (CollectionUtils.isEmpty(mstVoiceList)) {
            return false;
        }

        // ユーザーガール情報登録処理
        userService.insertUserGirl(userId, girlId);

        // ユーザーガールボイス情報登録
        List<UserGirlVoice> insertRecords = new ArrayList<>();
        for (MstVoice mstVoice : mstVoiceList) {
            if (VoiceType.NORMAL.getKey().equals(mstVoice.getType())) {
                continue;
            }
            UserGirlVoiceKey key = new UserGirlVoiceKey(userId, girlId, mstVoice.getKey().getVoiceId());
            UserGirlVoice record = new UserGirlVoice();
            record.setKey(key);
            record.setStatus(UserVoiceStatus.OFF.getKey());
            record.setUpdDatetime(nowDate);
            record.setInsDatetime(nowDate);
            insertRecords.add(record);
        }
        // バルクインサート
        userGirlVoiceRepository.save(insertRecords);

        return true;
    }

}
