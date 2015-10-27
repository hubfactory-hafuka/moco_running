package jp.hubfactory.moco.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hubfactory.moco.cache.MstGirlCache;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.cache.MstVoiceSetCache;
import jp.hubfactory.moco.cache.MstVoiceSetDetailCache;
import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.MstVoiceSet;
import jp.hubfactory.moco.entity.MstVoiceSetDetail;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.enums.PurchaseType;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.enums.VoiceType;
import jp.hubfactory.moco.logger.MocoLogger;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.util.MocoDateUtils;
import jp.hubfactory.moco.util.TableSuffixGenerator;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExchangeService {

    @Autowired
    private MstGirlCache mstGirlCache;
    @Autowired
    private MstVoiceCache mstVoiceCache;
    @Autowired
    private MstVoiceSetCache mstVoiceSetCache;
    @Autowired
    private MstVoiceSetDetailCache mstVoiceSetDetailCache;
    @Autowired
    private UserGirlVoiceRepository userGirlVoiceRepository;
    @Autowired
    private UserService userService;

    /**
     * ガールポイント交換処理
     * @param user
     * @param girlId
     * @return
     */
    public boolean exchangeGirl(Long userId, Integer girlId) {

        User user = userService.getUser(userId);
        if (user == null) {
            return false;
        }

        // ガール情報取得
        MstGirl mstGirl = mstGirlCache.getGirl(girlId);
        if (mstGirl == null || !MocoDateUtils.isWithin(mstGirl.getStartDatetime(), mstGirl.getEndDatetime()) || mstGirl.getPoint().longValue() > user.getPoint().longValue()) {
            return false;
        }
        // 台詞一覧取得
        List<MstVoice> mstVoiceList = mstVoiceCache.getVoiceList(girlId);
        if (CollectionUtils.isEmpty(mstVoiceList)) {
            return false;
        }

        // ユーザーガール情報登録処理
        userService.insertUserGirl(user.getUserId(), girlId);

        // ユーザーガールボイス情報登録
        for (MstVoice mstVoice : mstVoiceList) {
            if (VoiceType.NORMAL.getKey().equals(mstVoice.getType())) {
                continue;
            }
            userGirlVoiceRepository.insert(TableSuffixGenerator.getUserIdSuffix(userId), userId, girlId, mstVoice.getKey().getVoiceId(), UserVoiceStatus.OFF.getKey());
        }

        // インクリメントするためマイナス値にする
        long usePoint = mstGirl.getPoint().longValue() * -1;
        userService.updUserPoint(userId, usePoint);

        long afterPoint = user.getPoint() + usePoint;

        // 交換ログ出力
        MocoLogger.exchangeLog(userId, PurchaseType.GIRL, girlId, mstGirl.getPoint(), user.getPoint(), afterPoint);

        return true;
    }


    /**
     * ボイスセットポイント交換処理
     * @param userId
     * @param setId
     * @return
     */
    public boolean exchangeVoiceSet(Long userId, Integer setId) {

        User user = userService.getUser(userId);
        if (user == null) {
            return false;
        }

        MstVoiceSet mstVoiceSet = mstVoiceSetCache.getMstVoiceSet(setId);
        if (mstVoiceSet == null) {
            return false;
        }

        if ( mstVoiceSet.getPoint().longValue() > user.getPoint().longValue()) {
            return false;
        }

        // ボイスセットリスト取得
        List<MstVoiceSetDetail> mstVoiceSetList = mstVoiceSetDetailCache.getVoiceSetDetail(setId);
        if (CollectionUtils.isEmpty(mstVoiceSetList)) {
            return false;
        }

        // ガールＩＤ
        Integer girlId = mstVoiceSetList.get(0).getKey().getGirlId();

        // ボイスIDのSetオブジェクト生成
        Set<Integer> voiceIdSet = new HashSet<Integer>();
        for (MstVoiceSetDetail mstVoiceSetDetail : mstVoiceSetList) {
            voiceIdSet.add(mstVoiceSetDetail.getKey().getVoiceId());
        }

        // ユーザーのボイス情報取得
        List<UserGirlVoice> userGirlVoiceList = userService.getUserGirlVoiceList(userId, girlId);
        if (CollectionUtils.isNotEmpty(userGirlVoiceList)) {

            for (UserGirlVoice userGirlVoice : userGirlVoiceList) {

                if (voiceIdSet.contains(userGirlVoice.getKey().getVoiceId())) {
                    userGirlVoiceRepository.updateStatus(TableSuffixGenerator.getUserIdSuffix(userId), userId, girlId, userGirlVoice.getKey().getVoiceId(), UserVoiceStatus.ON.getKey());
                }
            }
        } else {

            for (MstVoiceSetDetail mstVoiceSetDetail : mstVoiceSetList) {
                userGirlVoiceRepository.updateStatus(TableSuffixGenerator.getUserIdSuffix(userId), userId, girlId, mstVoiceSetDetail.getKey().getVoiceId(), UserVoiceStatus.ON.getKey());
            }
        }

        long usePoint = mstVoiceSet.getPoint().longValue() * -1;
        userService.updUserPoint(userId, usePoint);

        long afterPoint = user.getPoint() + usePoint;

        // 交換ログ出力
        MocoLogger.exchangeLog(userId, PurchaseType.VOICE, setId, mstVoiceSet.getPoint(), user.getPoint(), afterPoint);

        return true;

    }
}
