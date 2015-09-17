package jp.hubfactory.moco.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.bean.UserGirlVoiceBean;
import jp.hubfactory.moco.cache.MstGirlMissionCache;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.cache.MstVoiceSetDetailCache;
import jp.hubfactory.moco.entity.MstGirlMission;
import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.MstVoiceSetDetail;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.enums.VoiceType;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VoiceService {

    @Autowired
    private MstVoiceCache mstVoiceCache;
    @Autowired
    private MstGirlMissionCache mstGirlMissionCache;
    @Autowired
    private MstVoiceSetDetailCache mstVoiceSetDetailCache;
    @Autowired
    private UserService userService;

    /**
     * ガール音声情報を取得する
     * @param userId ユーザID
     * @param girlId ガールID
     * @return
     */
    public List<UserGirlVoiceBean> getUserGirlVoiceBeanList(Long userId, Integer girlId) {

        // ガールのボイスリスト取得
        List<MstVoice> mstVoiceList = mstVoiceCache.getVoiceList(girlId);
        if (CollectionUtils.isEmpty(mstVoiceList)) {
            return null;
        }
        // ユーザーのガールボイス情報取得
        List<UserGirlVoice> userGirlVoiceList = userService.getUserGirlVoiceList(userId, girlId);
        if (CollectionUtils.isEmpty(userGirlVoiceList)) {
            return null;
        }
        // ガールのミッションリスト取得
        List<MstGirlMission> girlMissionList = mstGirlMissionCache.getGirlMissions(girlId);
        Map<Integer, String> girlMissionMap = new HashMap<>(girlMissionList.size());
        for (MstGirlMission mstGirlMission : girlMissionList) {
            girlMissionMap.put(mstGirlMission.getKey().getVoiceId(), mstGirlMission.getDescription());
        }

        // ガールのセット詳細リスト取得
        List<MstVoiceSetDetail> voiceSetDetailList = mstVoiceSetDetailCache.getVoiceSetDetailByGirlId(girlId);
        Map<Integer, Integer> voiceSetIdMap = new HashMap<>(voiceSetDetailList.size());
        for (MstVoiceSetDetail mstVoiceSetDetail : voiceSetDetailList) {
            voiceSetIdMap.put(mstVoiceSetDetail.getKey().getVoiceId(), mstVoiceSetDetail.getKey().getSetId());
        }

        Map<Integer, UserGirlVoice> userGirlVoiceMap = new HashMap<>(userGirlVoiceList.size());
        for (UserGirlVoice userGirlVoice : userGirlVoiceList) {
            userGirlVoiceMap.put(userGirlVoice.getKey().getVoiceId(), userGirlVoice);
        }

        List<UserGirlVoiceBean> beanList = new ArrayList<>(mstVoiceList.size());
        for (MstVoice mstVocie : mstVoiceList) {

            UserGirlVoiceBean bean = new UserGirlVoiceBean();
            BeanUtils.copyProperties(mstVocie.getKey(), bean);
            BeanUtils.copyProperties(mstVocie, bean);
            bean.setStatus(UserVoiceStatus.ON.getKey());
            bean.setVoiceFileId(bean.getGirlId() + "_" + bean.getVoiceId());

            // ボイスタイプが通常以外の場合
            if (!VoiceType.NORMAL.getKey().equals(mstVocie.getType())) {
                Integer voiceId = mstVocie.getKey().getVoiceId();
                if (userGirlVoiceMap.containsKey(voiceId)) {
                    bean.setStatus(userGirlVoiceMap.get(voiceId).getStatus());
                }

                // ボイスが聴けない場合
                if (UserVoiceStatus.OFF.getKey().equals(bean.getStatus())) {
                    bean.setOpenCondition(getOpenCondition(mstVocie.getType(), voiceId, girlMissionMap, voiceSetIdMap));
                }
            }

            beanList.add(bean);
        }

        return beanList;
    }

    /**
     * 有効な台詞情報のシチュエーション別リストを取得する
     * @param userId ユーザID
     * @param girlId ガールID
     * @return
     */
    public Map<Integer, List<UserGirlVoiceBean>> getRunVoiceList(Long userId, Integer girlId) {

        List<UserGirlVoiceBean> voiceBeanList = this.getUserGirlVoiceBeanList(userId, girlId);
        if (CollectionUtils.isEmpty(voiceBeanList)) {
            return null;
        }
        Map<Integer, List<UserGirlVoiceBean>> situationListMap = new HashMap<>();
        for (UserGirlVoiceBean userGirlVoiceBean : voiceBeanList) {
            if (UserVoiceStatus.ON.getKey().equals(userGirlVoiceBean.getStatus())) {
                List<UserGirlVoiceBean> list = (situationListMap.containsKey(userGirlVoiceBean.getSituation())) ? situationListMap.get(userGirlVoiceBean.getSituation()) : new ArrayList<UserGirlVoiceBean>();
                list.add(userGirlVoiceBean);
                situationListMap.put(userGirlVoiceBean.getSituation(), list);
            }
        }
        return situationListMap;
    }

    /**
     * ボイス開放条件文取得
     * @param voiceType
     * @param voiceId
     * @param girlMissionMap
     * @param voiceSetIdMap
     * @return
     */
    private String getOpenCondition(Integer voiceType, Integer voiceId, Map<Integer, String> girlMissionMap, Map<Integer, Integer> voiceSetIdMap) {

        if (VoiceType.MISSION_CLEAR.getKey().equals(voiceType)) {
            // ミッションクリアボイスの場合
            return girlMissionMap.get(voiceId);

        } else if (VoiceType.PURCHASE.getKey().equals(voiceType)) {
            // 課金ボイスの場合
            int viewSetId = voiceSetIdMap.get(voiceId).intValue() % 3;
            viewSetId = viewSetId == 0 ? 3 : viewSetId;
            String conditionStr = "声援セット" + viewSetId + "を購入すると聴けるよ";
            return conditionStr;

        } else {
            return null;
        }
    }
}
