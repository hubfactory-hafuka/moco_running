package jp.hubfactory.moco.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.hubfactory.moco.bean.UserBean;
import jp.hubfactory.moco.cache.MstGirlCache;
import jp.hubfactory.moco.cache.MstGirlMissionCache;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.entity.MstGirlMission;
import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.entity.UserGirlKey;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.entity.UserGirlVoiceKey;
import jp.hubfactory.moco.enums.GirlType;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.enums.VoiceType;
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
    @Autowired
    private MstGirlCache mstGirlCache;
    @Autowired
    private MstVoiceCache mstVoiceCache;

    /**
     * ログイン処理
     * @param email
     * @param password
     * @param uuId
     * @return
     */
    public UserBean login(String email, String password, String uuId) {
        // ユーザー情報取得
        User user = userRepository.findByEmailAndPassword(email, password);
        if (user == null) {
            return null;
        }
        return this.getUserBean(user.getUserId());
    }

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
    public User createUser(String email, String password, String uuId) {
        Date nowDate = MocoDateUtils.getNowDate();
        Long userId = userRepository.findMaxUserId();
        userId = userId == null ? 1 : userId + 1;
        User user = getUser(userId);
        if (user != null) {
            return null;
        }
        // ***************************************************************************//
        // ユーザー情報登録
        // ***************************************************************************//
        User record = new User();
        record.setUserId(userId);
        record.setEmail(email);
        record.setPassword(password);
        record.setGirlId(1);
        record.setTotalCount(0);
        record.setTotalDistance(0.000d);
        record.setUpdDatetime(nowDate);
        record.setInsDatetime(nowDate);
        userRepository.save(record);

        List<MstGirl> normalGirls = mstGirlCache.getGirlTypeList(GirlType.NORMAL.getKey());
        for (MstGirl mstGirl : normalGirls) {

            // ***************************************************************************//
            // ユーザーガールの登録
            // ***************************************************************************//
            this.insertUserGirl(userId, mstGirl.getGirlId());

            // ユーザーガールボイス情報登録
            List<UserGirlVoice> insertRecords = new ArrayList<>();
            List<MstVoice> voiceList = mstVoiceCache.getVoiceList(mstGirl.getGirlId());
            for (MstVoice mstVoice : voiceList) {

                if (VoiceType.NORMAL.getKey().equals(mstVoice.getType())) {
                    continue;
                }
                UserGirlVoiceKey key = new UserGirlVoiceKey(userId, mstGirl.getGirlId(), mstVoice.getKey().getVoiceId());
                UserGirlVoice userGirlVoiceRecord = new UserGirlVoice();
                userGirlVoiceRecord.setKey(key);
                userGirlVoiceRecord.setStatus(UserVoiceStatus.OFF.getKey());
                userGirlVoiceRecord.setUpdDatetime(nowDate);
                userGirlVoiceRecord.setInsDatetime(nowDate);
                insertRecords.add(userGirlVoiceRecord);
            }

            // ***************************************************************************//
            // ユーザーガールボイス情報登録
            // ***************************************************************************//
            userGirlVoiceRepository.save(insertRecords);
        }

        return record;
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
