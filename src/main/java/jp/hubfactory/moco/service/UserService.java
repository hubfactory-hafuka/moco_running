package jp.hubfactory.moco.service;

import java.util.Date;
import java.util.List;

import jp.hubfactory.moco.bean.UserBean;
import jp.hubfactory.moco.cache.MstGirlMissionCache;
import jp.hubfactory.moco.entity.MstGirlMission;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.entity.UserGirlKey;
import jp.hubfactory.moco.entity.UserGirlVoice;
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
    private MstGirlMissionCache mstGirlMissionCache;

    /**
     * ユーザー情報取得
     * @param userId
     * @return
     */
    public UserBean getUserBean(Long userId) {

        // ユーザー情報取得
        User user = this.getUser(userId);
        if (user == null) {
            return null;
        }
        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(user, userBean);
        userBean.setTotalDistance(String.format("%.3f", user.getTotalDistance()));

        // ユーザーガール情報取得
        UserGirl userGirl = this.getUserGirl(userId, user.getGirlId());
        userBean.setGirlDistance(userGirl == null ? "0.000" : String.format("%.3f", userGirl.getDistance()));

        // 次の達成報酬までの残りの距離を求める
        List<MstGirlMission> mstGirlMissionList = mstGirlMissionCache.getGirlMissions(user.getGirlId());
        for (MstGirlMission mstGirlMission : mstGirlMissionList) {
            if (userGirl.getDistance().doubleValue() < mstGirlMission.getDistance().doubleValue()) {
                double remainDistance = mstGirlMission.getDistance().doubleValue() - userGirl.getDistance().doubleValue();
                userBean.setRemainDistance(String.format("%.3f", remainDistance));
                break;
            }
        }

        return userBean;
    }

    /**
     * ユーザー情報取得
     * @param userId
     * @return
     */
    public User getUser(Long userId) {
        return userRepository.findOne(userId);
    }

    /**
     * ユーザー情報登録
     * @param user
     * @return
     */
    public User createUser(User user) {
        Date nowDate = MocoDateUtils.getNowDate();
        Long userId = userRepository.findMaxUserId();
        userId = userId == null ? 1 : userId + 1;

        User findRecord = getUser(user.getUserId());
        if (findRecord == null) {
            user.setUpdDatetime(nowDate);
            user.setInsDatetime(nowDate);
            return userRepository.save(user);
        }
        return null;
    }

    /**
     * ユーザーガールボイスリスト取得
     * @param userId
     * @param girlId
     * @return
     */
    public List<UserGirlVoice> getUserGirlVoiceList(Long userId, Integer girlId) {
        return userGirlVoiceRepository.findByKeyUserIdAndKeyGirlId(userId, girlId);
    }

    /**
     * お気に入り登録処理
     * @param userId
     * @param girlId
     * @return
     */
    public boolean updFavoriete(Long userId, Integer girlId) {

        User user = this.getUser(userId);
        if (user == null) {
            return false;
        }
        // テーブルから取得したentityに対してセットするとＤＢも更新されている
        user.setGirlId(girlId);

        return true;
    }

    /**
     * ユーザーガール情報取得
     * @param userId
     * @param girlId
     * @return
     */
    public UserGirl getUserGirl(Long userId, Integer girlId) {
        return userGirlRepository.findOne(new UserGirlKey(userId, girlId));
    }

    /**
     * ユーザーガール情報登録
     * @param userId
     * @param girlId
     */
    public void insertUserGirl(Long userId, Integer girlId) {

        Date nowDate = MocoDateUtils.getNowDate();

        UserGirlKey userGirlKey = new UserGirlKey(userId, girlId);
        UserGirl userGirl = new UserGirl();
        userGirl.setKey(userGirlKey);
        userGirl.setDistance(0.000d);
        userGirl.setUpdDatetime(nowDate);
        userGirl.setInsDatetime(nowDate);
        userGirlRepository.save(userGirl);
    }
}
