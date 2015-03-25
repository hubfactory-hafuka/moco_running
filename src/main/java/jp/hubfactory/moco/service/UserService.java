package jp.hubfactory.moco.service;

import java.util.Date;
import java.util.List;

import jp.hubfactory.moco.bean.UserBean;
import jp.hubfactory.moco.entity.MstGirlMission;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.entity.UserGirlKey;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.repository.MstGirlMissionRepository;
import jp.hubfactory.moco.repository.UserGirlRepository;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.repository.UserRepository;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserGirlVoiceRepository userGirlVoiceRepository;
    @Autowired
    private UserGirlRepository userGirlRepository;
    @Autowired
    private MstGirlMissionRepository mstGirlMissionRepository;

    /**
     * ユーザー情報取得
     * @param userId
     * @return
     */
    public UserBean getUser(Long userId) {

        // ユーザー情報取得
        User user = this.findUserByUserId(userId);
        if (user == null) {
            return null;
        }
        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(user, userBean);
        userBean.setTotalDistance(String.format("%.3f", user.getTotalDistance()));

        // ユーザーガール情報取得
        UserGirlKey userGirlKey = new UserGirlKey(userId, user.getGirlId());
        UserGirl userGirl = userGirlRepository.findOne(userGirlKey);
        userBean.setGirlDistance(userGirl == null ? "0.000" : String.format("%.3f", userGirl.getDistance()));

        // 次の達成報酬までの残りの距離を求める
        List<MstGirlMission> mstGirlMissionList = mstGirlMissionRepository.findByKeyGirlIdOrderByDistanceAsc(user.getGirlId());
        for (MstGirlMission mstGirlMission : mstGirlMissionList) {
            if (userGirl.getDistance().doubleValue() < mstGirlMission.getDistance().doubleValue()) {
                double remainDistance = mstGirlMission.getDistance().doubleValue() - userGirl.getDistance().doubleValue();
                userBean.setRemainDistance(String.format("%.3f", remainDistance));
                break;
            }
        }

        return userBean;
    }

    public User findUserByUserId(Long userId) {
        return userRepository.findOne(userId);
    }

    public User createUser(User user) {
        Date nowDate = MocoDateUtils.getNowDate();
        Long userId = userRepository.findMaxUserId();
        userId = userId == null ? 1 : userId + 1;

        User findRecord = findUserByUserId(user.getUserId());
        if (findRecord == null) {
            user.setUpdDatetime(nowDate);
            user.setInsDatetime(nowDate);
            return userRepository.save(user);
        }
        return null;
    }

    public List<UserGirlVoice> findUserGirlVoice(Long userId, Integer girlId) {
        return userGirlVoiceRepository.findByKeyUserIdAndKeyGirlId(userId, girlId);
    }
}
