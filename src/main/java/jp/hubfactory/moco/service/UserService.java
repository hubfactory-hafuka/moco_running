package jp.hubfactory.moco.service;

import java.util.Date;
import java.util.List;

import jp.hubfactory.moco.bean.LoginBean;
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
import jp.hubfactory.moco.entity.UserTakeover;
import jp.hubfactory.moco.enums.GirlType;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.enums.VoiceType;
import jp.hubfactory.moco.repository.UserGirlRepository;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.repository.UserRepository;
import jp.hubfactory.moco.repository.UserTakeoverRepository;
import jp.hubfactory.moco.util.MocoDateUtils;
import jp.hubfactory.moco.util.TableSuffixGenerator;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
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
    private UserTakeoverRepository userTakeoverRepository;
    @Autowired
    private MstGirlMissionCache mstGirlMissionCache;
    @Autowired
    private MstGirlCache mstGirlCache;
    @Autowired
    private MstVoiceCache mstVoiceCache;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RedisService redisService;

    /**
     * ログイン処理
     * @param loginId
     * @param password
     * @param serviceId
     * @param uuId
     * @return
     */
    public LoginBean login(Long userId, String uuId) {

        User user = this.getUser(userId);
        if (user == null) {
            return null;
        }

        user.setToken(tokenService.getToken(uuId));
        user.setUpdDatetime(MocoDateUtils.getNowDate());

        // redis側も更新
        redisService.updateUser(user);

        return new LoginBean(user.getUserId(), user.getToken(), user.getGirlId());
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
        userBean.setTotalDistance(String.format("%.2f", user.getTotalDistance()));

        // ユーザーガール情報取得
        UserGirl userGirl = this.getUserGirl(userId, user.getGirlId());
        userBean.setGirlDistance(userGirl == null ? "0.00" : String.format("%.2f", userGirl.getDistance()));

        // 次の達成報酬までの残りの距離を求める
        List<MstGirlMission> mstGirlMissionList = mstGirlMissionCache.getGirlMissions(user.getGirlId());
        for (MstGirlMission mstGirlMission : mstGirlMissionList) {
            if (userGirl.getDistance().doubleValue() < mstGirlMission.getDistance().doubleValue()) {
                double remainDistance = mstGirlMission.getDistance().doubleValue() - userGirl.getDistance().doubleValue();
                userBean.setRemainDistance(String.format("%.2f", remainDistance));
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
        User user = redisService.getUser(userId);
        if (user == null) {
            user = userRepository.findOne(userId);
            redisService.updateUser(user);
        }
        return user;
    }

    /**
     * ユーザー情報登録
     * @param user
     * @return
     */
    public LoginBean createUser(String uuId, String userName) {

        // トークン取得
        String token = tokenService.getToken(uuId);
        if (token == null) {
            return null;
        }

        Date nowDate = MocoDateUtils.getNowDate();
        Long userId = userRepository.findMaxUserId();
        userId = userId == null ? 1001 : userId + 1;

        // ***************************************************************************//
        // ユーザー情報登録
        // ***************************************************************************//
        User record = new User();
        record.setUserId(userId);
        record.setGirlId(1);
        record.setToken(token);
        record.setName(userName);
        record.setTotalCount(0);
        record.setTotalDistance(0.00d);
        record.setTotalAvgTime("0'00\"");
        record.setUpdDatetime(nowDate);
        record.setInsDatetime(nowDate);
        userRepository.save(record);
        // Redisのユーザー情報更新
        redisService.updateUser(record);

        List<MstGirl> normalGirls = mstGirlCache.getGirlTypeList(GirlType.NORMAL.getKey());
        for (MstGirl mstGirl : normalGirls) {

            // ***************************************************************************//
            // ユーザーガールの登録
            // ***************************************************************************//
            this.insertUserGirl(userId, mstGirl.getGirlId());

            // ユーザーガールボイス情報登録
            List<MstVoice> voiceList = mstVoiceCache.getVoiceList(mstGirl.getGirlId());
            for (MstVoice mstVoice : voiceList) {

                if (VoiceType.NORMAL.getKey().equals(mstVoice.getType())) {
                    continue;
                }

                userGirlVoiceRepository.insert(TableSuffixGenerator.getUserIdSuffix(userId), userId, mstGirl.getGirlId(), mstVoice.getKey().getVoiceId(), UserVoiceStatus.OFF.getKey());
            }
        }

        return new LoginBean(record.getUserId(), record.getToken(), record.getGirlId());
    }

    /**
     * ユーザーガールボイスリスト取得
     * @param userId
     * @param girlId
     * @return
     */
    public List<UserGirlVoice> getUserGirlVoiceList(Long userId, Integer girlId) {
        return userGirlVoiceRepository.findByKeyUserIdAndKeyGirlId(TableSuffixGenerator.getUserIdSuffix(userId), userId, girlId);
    }

    /**
     * お気に入り更新処理
     * @param userId
     * @param girlId
     * @return
     */
    public boolean updFavoriete(Long userId, Integer girlId) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            return false;
        }
        // テーブルから取得したentityに対してセットするとＤＢも更新されている
        user.setGirlId(girlId);
        // redisのユーザー情報も更新
        redisService.updateUser(user);

        return true;
    }

    /**
     * 名前更新処理
     * @param userId
     * @param girlId
     * @return
     */
    public boolean updName(Long userId, String name) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            return false;
        }
        // テーブルから取得したentityに対してセットするとＤＢも更新されている
        user.setName(name);
        // redisのユーザー情報も更新
        redisService.updateUser(user);

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
        userGirl.setDistance(0.00d);
        userGirl.setUpdDatetime(nowDate);
        userGirl.setInsDatetime(nowDate);
        userGirlRepository.save(userGirl);
    }

    /**
     * 引き継ぎID発行
     * @param userId
     * @return
     */
    public UserTakeover issueTakeOverCode(Long userId) {

        String takeoverCode = RandomStringUtils.randomAlphanumeric(8);

        Date nowDate = MocoDateUtils.getNowDate();

        UserTakeover userTakeover = userTakeoverRepository.findOne(userId);
        if (userTakeover == null) {
            userTakeover = new UserTakeover(userId, takeoverCode, nowDate, nowDate);
            userTakeoverRepository.save(userTakeover);
        } else {
            userTakeover.setTakeoverCode(takeoverCode);
            userTakeover.setUpdDatetime(nowDate);
        }
        return userTakeover;
    }

    /**
     * データ引継処理
     * @param userId
     * @param uuId
     * @param takeoverCode
     * @return
     */
    public LoginBean takeover(Long userId, String uuId, String takeoverCode) {

        UserTakeover userTakeover = userTakeoverRepository.findOne(userId);
        if (userTakeover == null) {
            return null;
        }

        if (!StringUtils.equals(takeoverCode, userTakeover.getTakeoverCode())) {
            return null;
        }

        Date nowDate = MocoDateUtils.getNowDate();
        userTakeover.setTakeoverCode(null);
        userTakeover.setUpdDatetime(nowDate);

        return this.login(userId, uuId);
    }
}
