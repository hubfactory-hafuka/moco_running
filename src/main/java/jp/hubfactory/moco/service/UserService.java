package jp.hubfactory.moco.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import jp.hubfactory.moco.MocoProperties;
import jp.hubfactory.moco.bean.LoginBean;
import jp.hubfactory.moco.bean.RankingBonusBean;
import jp.hubfactory.moco.bean.UserBean;
import jp.hubfactory.moco.cache.MstConfigCache;
import jp.hubfactory.moco.cache.MstGirlCache;
import jp.hubfactory.moco.cache.MstGirlMissionCache;
import jp.hubfactory.moco.cache.MstLoginBonusCache;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.entity.MstGirlMission;
import jp.hubfactory.moco.entity.MstLoginBonus;
import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.entity.UserGirlKey;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.entity.UserRankingPoint;
import jp.hubfactory.moco.entity.UserTakeover;
import jp.hubfactory.moco.enums.GirlType;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.enums.VoiceType;
import jp.hubfactory.moco.repository.UserGirlRepository;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.repository.UserRankingPointRepository;
import jp.hubfactory.moco.repository.UserRepository;
import jp.hubfactory.moco.repository.UserTakeoverRepository;
import jp.hubfactory.moco.util.MocoDateUtils;
import jp.hubfactory.moco.util.TableSuffixGenerator;

import org.apache.commons.codec.binary.Base64;
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
    private UserRankingPointRepository userRankingPointRepository;
    @Autowired
    private MstGirlMissionCache mstGirlMissionCache;
    @Autowired
    private MstLoginBonusCache mstLoginBonusCache;
    @Autowired
    private MstGirlCache mstGirlCache;
    @Autowired
    private MstVoiceCache mstVoiceCache;
    @Autowired
    private MstConfigCache mstConfigCache;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MocoProperties mocoProperties;

    /**
     * ログイン処理
     * @param loginId
     * @param password
     * @param serviceId
     * @param uuId
     * @return
     */
    public LoginBean login(Long userId, String uuId) {

//        User user = this.getUser(userId);
        User user = userRepository.findOne(userId);
        if (user == null) {
            return null;
        }

        user.setToken(tokenService.getToken(uuId));
        user.setUpdDatetime(MocoDateUtils.getNowDate());

        redisService.updateUser(user);

        return new LoginBean(user.getUserId(), user.getToken(), user.getGirlId());
    }

    /**
     * ユーザー情報取得
     * @param userId
     * @return
     */
    public UserBean getUserBean(Long userId, boolean topFlg) {

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

        userBean.setHeight(user.getHeight() == null ? null : String.valueOf(user.getHeight()));
        userBean.setWeight(user.getWeight() == null ? null : String.valueOf(user.getWeight()));
        userBean.setPoint(user.getPoint());

        // TOP情報取得の場合、ログインボーナス付与処理
        if (topFlg && this.sendLoginBonus(userId, userBean)) {
            // ランキングボーナス情報取得
            userBean.setRankingBonusBean(this.getRankingBonusBean(userId));
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

    /**
     * 体重更新処理
     * @param userId
     * @param weight
     * @return
     */
    public boolean updWeight(Long userId, String weight) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            return false;
        }
        // テーブルから取得したentityに対してセットするとＤＢも更新されている
        user.setWeight(Double.valueOf(weight));
        // redisのユーザー情報も更新
        redisService.updateUser(user);

        return true;
    }

    /**
     * 身長更新処理
     * @param userId
     * @param height
     * @return
     */
    public boolean updHeight(Long userId, String height) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            return false;
        }
        // テーブルから取得したentityに対してセットするとＤＢも更新されている
        user.setHeight(Double.valueOf(height));
        // redisのユーザー情報も更新
        redisService.updateUser(user);

        return true;
    }

    /**
     * ユーザーポイント更新
     * @param userId
     * @param point
     */
    public boolean updUserPoint(Long userId, Long point) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            return false;
        }
        user.setPoint(user.getPoint().longValue() + point.longValue());
        redisService.updateUser(user);

        return true;
    }

    /**
     * プロフィール画像アップロード
     * @param userId ユーザーID
     * @param base64ImageStr 画像Base64エンコード文字列データ
     * @return
     * @throws IOException
     */
    public boolean updProfileImage(Long userId, String base64ImageStr) throws IOException {

        // DBからユーザー情報取得
        User user = userRepository.findOne(userId);
        if (user == null) {
            return false;
        }

        // プロフィール画像が既に存在する場合
        if (StringUtils.isNotEmpty(user.getProfImgPath())) {
            // 前回の画像を削除
            File delFile = new File(mocoProperties.getSystem().getWriteImageUrl() + user.getProfImgPath());
            if(delFile.delete()){
                //ファイル削除成功
                System.out.println("ファイル削除成功");
             }else{
                //ファイル削除失敗
                System.out.println("ファイル削除失敗");
             }
        }

        // 画像ファイル名を生成する
        String hash = RandomStringUtils.randomAlphanumeric(16);
        String fileName = userId + "_" + hash +".jpg";

        // テーブルとredisのユーザー情報を更新
        user.setProfImgPath(fileName);
        redisService.updateUser(user);

        // 画像ファイルをサーバーに作成
        byte[] imageData = base64ImageStr.getBytes();
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(imageData)));
        File file = new File(mocoProperties.getSystem().getWriteImageUrl() + fileName);
        if (!file.exists()) {
            file.mkdirs();
        }
        ImageIO.write(image, "jpg", file);

        return true;
    }

    /**
     * ログインボーナス付与処理
     * @param userBean ユーザー情報
     */
    private boolean sendLoginBonus(Long userId, UserBean userBean) {

        // ポイント有効期間外の場合
        if (!mstConfigCache.isPointEnable()) {
            return false;
        }

        // ユーザー情報が存在しない場合
        User user = userRepository.findOne(userId);
        if (user == null) {
            return false;
        }

        Date nowDate = MocoDateUtils.getNowDate();
        // ログインボーナス日時(4:00)
        Date loginBonusDatetime = MocoDateUtils.getLoginBonusDate(nowDate);

        // ログインボーナス日がnullかつ、当日4:00前の場合。または、ログインボーナス日が当日4:00以降の場合
        if ((user.getLoginBonusDatetime() == null && nowDate.compareTo(loginBonusDatetime) < 0) || (user.getLoginBonusDatetime() != null && loginBonusDatetime.compareTo(user.getLoginBonusDatetime()) <= 0)) {
            return false;
        }
        // ログインボーナス情報取得
        MstLoginBonus mstLoginBonus = mstLoginBonusCache.getLoginBonus(loginBonusDatetime);
        if (mstLoginBonus == null) {
            return false;
        }

        Long userPoint = user.getPoint() == null ? 0L : user.getPoint();
        Long nowPoint = mstLoginBonus.getPoint();

        user.setLoginBonusDatetime(nowDate);
        user.setPoint(userPoint + nowPoint);

        userBean.setLoginBonusPoint(nowPoint);
        userBean.setPoint(userPoint + nowPoint);

        redisService.updateUser(user);

        return true;
    }

    /**
     * ランキング情報取得
     * @param userId ユーザーID
     * @return
     */
    private RankingBonusBean getRankingBonusBean(Long userId) {
        UserRankingPoint userRankingPoint = userRankingPointRepository.findOne(userId);
        if (userRankingPoint == null) {
            return null;
        }

        RankingBonusBean bean = new RankingBonusBean();
        bean.setRankingDate(userRankingPoint.getRankingDate());
        bean.setRank(userRankingPoint.getRank());
        bean.setPoint(userRankingPoint.getPoint());

        userRankingPointRepository.delete(userId);

        return bean;

    }
}
