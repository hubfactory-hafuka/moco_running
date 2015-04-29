package jp.hubfactory.moco.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hubfactory.moco.MocoProperties;
import jp.hubfactory.moco.bean.VoiceSetBean;
import jp.hubfactory.moco.bean.VoiceSetDetailBean;
import jp.hubfactory.moco.cache.MstGirlCache;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.cache.MstVoiceSetCache;
import jp.hubfactory.moco.cache.MstVoiceSetDetailCache;
import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.MstVoiceSet;
import jp.hubfactory.moco.entity.MstVoiceSetDetail;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.entity.UserGirlVoiceKey;
import jp.hubfactory.moco.entity.UserPurchaseHistory;
import jp.hubfactory.moco.entity.UserPurchaseHistoryKey;
import jp.hubfactory.moco.enums.PurchaseType;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.enums.VoiceSituation;
import jp.hubfactory.moco.enums.VoiceType;
import jp.hubfactory.moco.logger.MocoLogger;
import jp.hubfactory.moco.purchase.VerifyReceipt;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.repository.UserPurchaseHistoryRepository;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    @Autowired
    private MstVoiceSetCache mstVoiceSetCache;
    @Autowired
    private MstVoiceSetDetailCache mstVoiceSetDetailCache;
    @Autowired
    private MstGirlCache mstGirlCache;
    @Autowired
    private MstVoiceCache mstVoiceCache;
    @Autowired
    private UserGirlVoiceRepository userGirlVoiceRepository;
    @Autowired
    private UserPurchaseHistoryRepository userPurchaseHistoryRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private VerifyReceipt verifyReceipt;
    @Autowired
    private MocoProperties mocoProperties;

    /**
     * ボイスセット購入処理
     * @param userId
     * @param setId
     * @param girlId
     * @return
     */
    public boolean purchaseVoiceSet(Long userId, Integer setId, String receipt) {

        Date nowDate = MocoDateUtils.getNowDate();

        // 課金ログ出力
        MocoLogger.purchaseLog(userId, PurchaseType.VOICE, setId, receipt);

        try {
            // 本番用iTunesパスで認証
            String itunesPath = mocoProperties.getSystem().getItunes();
            int status = verifyReceipt.verifyReceipt(receipt, itunesPath);

            logger.error("iTunesPath=" + itunesPath);
            logger.error("status=" + status);


            // ステータスが21007の場合、サンドボックス用パスで認証
            if (status == 21007) {
                itunesPath = mocoProperties.getSystem().getItunesSandbox();
                status = verifyReceipt.verifyReceipt(receipt, itunesPath);

                logger.error("iTunesPath=" + itunesPath);
                logger.error("status=" + status);
            }

            if (status != 0) {
                logger.error("レシート認証失敗 userId=" + userId + " setId=" + setId + " status=" + status);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("レシート認証失敗 userId=" + userId + " setId=" + setId);
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
        for (MstVoiceSetDetail mstVoiceSet : mstVoiceSetList) {
            voiceIdSet.add(mstVoiceSet.getKey().getVoiceId());
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

            for (MstVoiceSetDetail mstVoiceSet : mstVoiceSetList) {
                UserGirlVoiceKey key = new UserGirlVoiceKey(userId, girlId, mstVoiceSet.getKey().getVoiceId());
                UserGirlVoice record = new UserGirlVoice(key, UserVoiceStatus.ON.getKey(), nowDate,nowDate);
                userGirlVoiceRepository.save(record);
            }
        }


        // 購入履歴登録
        UserPurchaseHistoryKey historyKey = new UserPurchaseHistoryKey(userId, PurchaseType.VOICE.getKey(), setId);
        UserPurchaseHistory history = new UserPurchaseHistory(historyKey, nowDate, nowDate);
        userPurchaseHistoryRepository.save(history);

        return true;
    }

    /**
     * ボイスセットリスト取得
     * @param userId
     * @param girlId
     * @return
     */
    public List<VoiceSetBean> getVoiceSetList(Long userId, Integer girlId) {

        List<VoiceSetBean> voiceSetBeans = new ArrayList<VoiceSetBean>();

        List<MstVoiceSet> voiceSetList = mstVoiceSetCache.getVoiceSetListByGirlId(girlId);
        for (MstVoiceSet mstVoiceSet : voiceSetList) {

            Integer count = userPurchaseHistoryRepository.selectCountByKey(userId, PurchaseType.VOICE.getKey(), mstVoiceSet.getKey().getSetId());

            VoiceSetBean voiceSetBean = new VoiceSetBean();
            voiceSetBean.setSetId(mstVoiceSet.getKey().getSetId());
            voiceSetBean.setGirlId(girlId);
            voiceSetBean.setPrice(mstVoiceSet.getPrice());
            voiceSetBean.setHoldFlg(count <= 0 ? false : true);

            List<VoiceSetDetailBean> detailBeans = new ArrayList<>();
            List<MstVoiceSetDetail> mstVoiceSetList = mstVoiceSetDetailCache.getVoiceSetDetailList(mstVoiceSet.getKey().getSetId(), mstVoiceSet.getKey().getGirlId());
            for (MstVoiceSetDetail mstVoiceSetDetail : mstVoiceSetList) {

                MstVoice mstVoice =mstVoiceCache.getMstVoice(mstVoiceSetDetail.getKey().getGirlId(), mstVoiceSetDetail.getKey().getVoiceId());
                String situationName = VoiceSituation.valueOf(mstVoice.getSituation()).getName();

                VoiceSetDetailBean detailBean = new VoiceSetDetailBean();
                detailBean.setSituationName(situationName);
                detailBean.setWord(mstVoice.getWord());
                detailBean.setVoiceFileId(mstVoice.getKey().getGirlId() + "_" + mstVoice.getKey().getVoiceId());
                detailBeans.add(detailBean);
            }

            voiceSetBean.setSetDetailList(detailBeans);
            voiceSetBeans.add(voiceSetBean);
        }

        return voiceSetBeans;
    }

    /**
     * ガール購入処理
     * @param userId
     * @param girlId
     * @return
     */
    public boolean purchaseGirl(Long userId, Integer girlId, String receipt) {

        // 課金ログ出力
        MocoLogger.purchaseLog(userId, PurchaseType.GIRL, girlId, receipt);

        try {
            // 本番用iTunesパスで認証
            String itunesPath = mocoProperties.getSystem().getItunes();
            int status = verifyReceipt.verifyReceipt(receipt, itunesPath);

            logger.error("iTunesPath=" + itunesPath);
            logger.error("status=" + status);


            // ステータスが21007の場合、サンドボックス用パスで認証
            if (status == 21007) {
                itunesPath = mocoProperties.getSystem().getItunesSandbox();
                status = verifyReceipt.verifyReceipt(receipt, itunesPath);

                logger.error("iTunesPath=" + itunesPath);
                logger.error("status=" + status);
            }

            if (status != 0) {
                logger.error("レシート認証失敗 userId=" + userId + " girlId=" + girlId + " status=" + status);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("レシート認証失敗 userId=" + userId + " girlId=" + girlId);
            return false;
        }

        Date nowDate = MocoDateUtils.getNowDate();

        // ガール情報取得
        MstGirl mstGirl = mstGirlCache.getGirl(girlId);
        if (mstGirl == null || !MocoDateUtils.isWithin(mstGirl.getStartDatetime(), mstGirl.getEndDatetime())) {
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
