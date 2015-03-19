package jp.hubfactory.moco.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.bean.UserGirlVoiceBean;
import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.enums.VoiceType;
import jp.hubfactory.moco.repository.MstVoiceRepository;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VoiceService {

    @Autowired
    private MstVoiceRepository mstVoiceRepository;
    @Autowired
    private UserGirlVoiceRepository uesrGirlVoiceRepository;

    public List<UserGirlVoiceBean> getUserGirlVoiceBeanList(Long userId, Integer girlId) {

        List<MstVoice> mstVoiceList = mstVoiceRepository.findByMstVoiceKeyGirlId(girlId);
        if (CollectionUtils.isEmpty(mstVoiceList)) {
            return null;
        }
        List<UserGirlVoice> userGirlVoiceList = uesrGirlVoiceRepository.findByKeyUserIdAndKeyGirlId(userId, girlId);
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
            BeanUtils.copyProperties(mstVocie.getMstVoiceKey(), bean);
            BeanUtils.copyProperties(mstVocie, bean);
            bean.setStatus(UserVoiceStatus.ON.getKey());

            if (!VoiceType.NORMAL.getKey().equals(mstVocie.getType())) {
                Integer voiceId = mstVocie.getMstVoiceKey().getVoiceId();
                if (userGirlVoiceMap.containsKey(voiceId)) {
                    bean.setStatus(userGirlVoiceMap.get(voiceId).getStatus());
                }
            }

            beanList.add(bean);
        }

        return beanList;
    }
}
