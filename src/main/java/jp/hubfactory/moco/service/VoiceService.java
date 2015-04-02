package jp.hubfactory.moco.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.bean.UserGirlVoiceBean;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.enums.VoiceType;
import jp.hubfactory.moco.repository.MstVoiceRepository;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VoiceService {

    @Autowired
    private MstVoiceCache mstVoiceCache;
    @Autowired
    private UserService userService;
    @Autowired
    private MstVoiceRepository mstVoiceRepository;

    /**
     * ガール音声情報を取得する
     * @param userId ユーザID
     * @param girlId ガールID
     * @return
     */
    public List<UserGirlVoiceBean> getUserGirlVoiceBeanList(Long userId, Integer girlId) {

        List<MstVoice> mstVoiceList = mstVoiceCache.getVoiceList(girlId);
        if (CollectionUtils.isEmpty(mstVoiceList)) {
            return null;
        }
        List<UserGirlVoice> userGirlVoiceList = userService.getUserGirlVoiceList(userId, girlId);
        if (CollectionUtils.isEmpty(userGirlVoiceList)) {
            return null;
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

            if (!VoiceType.NORMAL.getKey().equals(mstVocie.getType())) {
                Integer voiceId = mstVocie.getKey().getVoiceId();
                if (userGirlVoiceMap.containsKey(voiceId)) {
                    bean.setStatus(userGirlVoiceMap.get(voiceId).getStatus());
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
}
