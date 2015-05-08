package jp.hubfactory.moco.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.bean.ActivityResultBean;
import jp.hubfactory.moco.bean.MissionClearVoiceBean;
import jp.hubfactory.moco.bean.UserActivityBean;
import jp.hubfactory.moco.bean.UserActivityDetailBean;
import jp.hubfactory.moco.bean.UserActivityLocationBean;
import jp.hubfactory.moco.cache.MstGirlMissionCache;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.entity.MstGirlMission;
import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserActivity;
import jp.hubfactory.moco.entity.UserActivityDetail;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.entity.UserGoal;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.form.RegistUserActivityDetailForm;
import jp.hubfactory.moco.form.RegistUserActivityForm;
import jp.hubfactory.moco.form.RegistUserActivityLocationForm;
import jp.hubfactory.moco.repository.UserActivityDetailRepository;
import jp.hubfactory.moco.repository.UserActivityRepository;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.repository.UserGoalRepository;
import jp.hubfactory.moco.util.MocoDateUtils;
import jp.hubfactory.moco.util.TableSuffixGenerator;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private UserActivityRepository userActivityRepository;
    @Autowired
    private UserActivityDetailRepository userActivityDetailRepository;
    @Autowired
    private UserGoalRepository userGoalRepository;
    @Autowired
    private UserGirlVoiceRepository userGirlVoiceRepository;
    @Autowired
    private MstGirlMissionCache mstGirlMissionCache;
    @Autowired
    private MstVoiceCache mstVoiceCache;
    @Autowired
    private UserService userService;

    /**
     * ユーザーのアクティビティ一覧取得
     * @param userId ユーザーID
     * @return
     */
    public List<UserActivityBean> getUserActivities(Long userId) {

        List<UserActivity> userActivities = userActivityRepository.findByKeyUserIdOrderByKeyActivityIdDesc(TableSuffixGenerator.getUserIdSuffix(userId), userId);
        if (CollectionUtils.isEmpty(userActivities)) {
            return null;
        }

        Map<Integer, List<UserActivityDetail>> activityIdKeyMap = new HashMap<>();

        List<UserActivityDetail> userActivityDetailAll = userActivityDetailRepository.findByKeyUserIdOrderByKeyActivityIdAsc(TableSuffixGenerator.getUserIdSuffix(userId), userId);
        if (CollectionUtils.isEmpty(userActivityDetailAll)) {
            logger.error("アクティビティ詳細情報がありません。 userId=" + userId);
        } else {

            for (UserActivityDetail userActivityDetail : userActivityDetailAll) {
                List<UserActivityDetail> cardList = (activityIdKeyMap.containsKey(userActivityDetail.getKey().getActivityId())) ? activityIdKeyMap.get(userActivityDetail.getKey().getActivityId()) : new ArrayList<UserActivityDetail>();
                cardList.add(userActivityDetail);
                activityIdKeyMap.put(userActivityDetail.getKey().getActivityId(), cardList);
            }
        }

        List<UserActivityBean> beanList = new ArrayList<>();
        for (UserActivity userActivity : userActivities) {

            UserActivityBean bean = new UserActivityBean();
            bean.setGirlId(userActivity.getGirlId());
            bean.setUserId(userActivity.getKey().getUserId());
            bean.setActivityId(userActivity.getKey().getActivityId());
            bean.setRunDate(MocoDateUtils.convertString(userActivity.getRunDate(), MocoDateUtils.DATE_FORMAT_yyyyMMdd_SLASH));
            bean.setDistance(String.format("%.2f", userActivity.getDistance()));
            bean.setTime(userActivity.getTime());
            bean.setAvgTime(userActivity.getAvgTime());

            try {
                List<UserActivityLocationBean> locations = new ObjectMapper().readValue(userActivity.getLocations(), new TypeReference<List<UserActivityLocationBean>>() {});
                bean.setLocations(locations);
            } catch (IOException e) {
                logger.error("JSON変換エラー。 userId=" + userId + " activityId=" + userActivity.getKey().getActivityId());
                e.printStackTrace();
            }

            List<UserActivityDetail> userActivityDetails = activityIdKeyMap.get(userActivity.getKey().getActivityId());
            if (CollectionUtils.isEmpty(userActivityDetails)) {
                logger.error("アクティビティ詳細情報がありません。 userId=" + userId + " activityId=" + userActivity.getKey().getActivityId());
                beanList.add(bean);
                continue;
            }

            List<UserActivityDetailBean> detailBeans = new ArrayList<>(userActivityDetails.size());
            for (UserActivityDetail userActivityDetail : userActivityDetails) {
                UserActivityDetailBean detailBean = new UserActivityDetailBean();
                BeanUtils.copyProperties(userActivityDetail, detailBean);
                detailBeans.add(detailBean);
            }
            bean.setDetails(detailBeans);
            beanList.add(bean);
        }
        return beanList;
    }

    /**
     * ユーザーのアクティビティ登録
     * @param userId ユーザーID
     * @param runDateStr 日時
     * @param distance 距離
     * @param time 時間
     * @return
     */
    public ActivityResultBean addUserActivity(RegistUserActivityForm form) {

        // 現在日時取得
        Date nowDate = MocoDateUtils.getNowDate();

        Integer activityId = userActivityRepository.findMaxActivityId(TableSuffixGenerator.getUserIdSuffix(form.getUserId()), form.getUserId());
        activityId = activityId == null ? 1 : activityId.intValue() + 1;

        // ***************************************************************************//
        // ユーザーの現在のガール情報取得
        // ***************************************************************************//
        UserGirl userGirl = userService.getUserGirl(form.getUserId(), form.getGirlId());
        if (userGirl == null) {
            logger.error("userGirl is null. userId=" + form.getUserId() + " girlId=" + form.getGirlId());
            return null;
        }

        // ***************************************************************************//
        // 総平均ペース算出
        // ***************************************************************************//
        String totalAvgTime = this.calcTotalAvgTime(form);

        // ***************************************************************************//
        // アクティビティ登録
        // ***************************************************************************//
        this.registActivity(form, activityId, nowDate);

        // ***************************************************************************//
        // アクティビティ詳細登録
        // ***************************************************************************//
        this.registActivityDetail(form, activityId, nowDate);

        // ***************************************************************************//
        // 達成報酬付与
        // ***************************************************************************//
        List<MstVoice> clearVoiceList = this.updateUserGirl(form, userGirl, nowDate);

        // ***************************************************************************//
        // ガール距離更新
        // ***************************************************************************//
        this.updateUserGirlDistance(form, userGirl, nowDate);

        // ***************************************************************************//
        // ユーザー総距離更新
        // ***************************************************************************//
        this.updateUser(form, totalAvgTime, nowDate);

        // ***************************************************************************//
        // 新記録更新処理
        // ***************************************************************************//
        boolean isNewRecord = this.updateUserGoal(form.getUserId(), form.getGoalDistance(), form.getGoalTime(), form.getDistance());

        List<MissionClearVoiceBean> beanList = new ArrayList<MissionClearVoiceBean>();
        for (MstVoice mstVoice : clearVoiceList) {
            MissionClearVoiceBean bean = new MissionClearVoiceBean();
            BeanUtils.copyProperties(mstVoice.getKey(), bean);
            BeanUtils.copyProperties(mstVoice, bean);
            beanList.add(bean);
        }

        ActivityResultBean resultBean = new ActivityResultBean();
        resultBean.setNewRecordFlg(isNewRecord);
        resultBean.setMissionClearVoiceBeans(beanList);
        return resultBean;
    }

    private String calcTotalAvgTime(RegistUserActivityForm form) {
        // ユーザーのアクティビティ一覧取得
        List<UserActivity> userActivities = userActivityRepository.findByKeyUserIdOrderByKeyActivityIdDesc(TableSuffixGenerator.getUserIdSuffix(form.getUserId()), form.getUserId());

        int totalSecond = MocoDateUtils.convertTimeStrToSecond(form.getTime());
        double totalDistance = form.getDistance();

        for (UserActivity userActivity : userActivities) {
            totalSecond += MocoDateUtils.convertTimeStrToSecond(userActivity.getTime());
            totalDistance += userActivity.getDistance();
        }
        return MocoDateUtils.calcAvgTime(totalSecond, totalDistance);
    }

    /**
     * アクティビティ情報登録
     * @param form
     * @param activityId
     * @param nowDate
     * @return 総平均時間(9'99")
     */
    private void registActivity(RegistUserActivityForm form, Integer activityId, Date nowDate) {

        Date runDate = MocoDateUtils.convertDate(form.getRunDate(), MocoDateUtils.DATE_FORMAT_yyyyMMdd_SLASH);

        // 平均時間算出
        String avgTime = MocoDateUtils.calcAvgTime(form.getTime(), form.getDistance());

//        UserActivity record = new UserActivity();
//        UserActivityKey userActivityKey = new UserActivityKey(form.getUserId(), activityId);
//        record.setKey(userActivityKey);
//        record.setGirlId(form.getGirlId());
//        record.setDistance(form.getDistance());
//        record.setRunDate(runDate);
//        record.setTime(form.getTime());
//        record.setAvgTime(avgTime);
//        record.setUpdDatetime(nowDate);
//        record.setInsDatetime(nowDate);

        String locationStr = null;
        ObjectMapper mapper = new ObjectMapper();
        List<RegistUserActivityLocationForm> locations = form.getLocations();
        try {
            locationStr = mapper.writeValueAsString(locations);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalStateException("JSON変換エラー:" + e.toString());
        }
//        record.setLocations(locationStr);

//        userActivityRepository.save(record);

        userActivityRepository.insert(TableSuffixGenerator.getUserIdSuffix(form.getUserId()), form.getUserId(), activityId, form.getGirlId(), runDate, form.getDistance(), form.getTime(), avgTime, locationStr);

    }

    /**
     * アクティビティ詳細情報登録
     * @param form
     * @param activityId
     * @param nowDate
     */
    private void registActivityDetail(RegistUserActivityForm form, Integer activityId, Date nowDate) {

        List<RegistUserActivityDetailForm> detailList = form.getDetails();
//        List<UserActivityDetail> detailRecords = new ArrayList<>(detailList.size());
        int index = 1;
        int beforeTimeElapsedSec = 0;
        int beforeLapTime = 0;
        for (RegistUserActivityDetailForm registUserActivityDetailForm : detailList) {

            // 距離
            Integer distance = registUserActivityDetailForm.getDistance();

            // 経過時間(秒)
            int timeElapsed = registUserActivityDetailForm.getTimeElapsed();
            // 経過時間(秒) → 経過時間(hh:mm:ss)
            String timeElapsedStr = MocoDateUtils.convertSecToHMS(timeElapsed);

            String incDecTime = null;

            String lapTime = null;

            if (distance.intValue() == 1) {
                lapTime = timeElapsedStr;
                beforeLapTime = timeElapsed;
            } else {
                int lapTimeSec = timeElapsed - beforeTimeElapsedSec;
                lapTime = MocoDateUtils.convertSecToHMS(lapTimeSec);

                String sign = "";
                if (beforeLapTime == lapTimeSec) {
                    incDecTime = "0'00\"";
                } else {
                    int nowIncDecTimeSec = beforeLapTime - lapTimeSec;
                    incDecTime = MocoDateUtils.convertSecToLapTimeString(nowIncDecTimeSec);
                    sign = nowIncDecTimeSec < 0 ? "+" : "-";

                }
                incDecTime = sign + incDecTime;
                beforeLapTime = lapTimeSec;

            }
            beforeTimeElapsedSec = timeElapsed;

//            UserActivityDetailKey detailKey = new UserActivityDetailKey(form.getUserId(), activityId, index);
//            UserActivityDetail detailRecord = new UserActivityDetail();
//            detailRecord.setKey(detailKey);
//            detailRecord.setDistance(registUserActivityDetailForm.getDistance());
//            detailRecord.setIncDecTime(incDecTime);
//            detailRecord.setLapTime(lapTime);
//            detailRecord.setTimeElapsed(timeElapsedStr);
//            detailRecord.setUpdDatetime(nowDate);
//            detailRecord.setInsDatetime(nowDate);
//            detailRecords.add(detailRecord);
            index++;

            userActivityDetailRepository.insert(TableSuffixGenerator.getUserIdSuffix(form.getUserId()), form.getUserId(), activityId, index, registUserActivityDetailForm.getDistance(), timeElapsedStr, lapTime, incDecTime);

        }
//        userActivityDetailRepository.save(detailRecords);
    }

    /**
     * ミッション達成報酬付与処理
     * @param form
     * @param nowDate
     */
    private List<MstVoice> updateUserGirl(RegistUserActivityForm form, UserGirl userGirl, Date nowDate) {

        // ミッション達成ボイスリスト
        List<MstVoice> clearVoiceList = new ArrayList<>();

        // ガールミッション情報取得
        List<MstGirlMission> girlMissions = mstGirlMissionCache.getGirlMissions(form.getGirlId());

        // ミッション情報が存在する場合
        if (CollectionUtils.isNotEmpty(girlMissions)) {

            Map<Integer, UserGirlVoice> userGirlVoiceMap = new HashMap<>();
            List<UserGirlVoice> userGirlVoiceList = userService.getUserGirlVoiceList(form.getUserId(), form.getGirlId());
            for (UserGirlVoice userGirlVoice : userGirlVoiceList) {
                userGirlVoiceMap.put(userGirlVoice.getKey().getVoiceId(), userGirlVoice);
            }

            if (userGirl == null) {

                for (MstGirlMission mstGirlMission : girlMissions) {

                    double targetDistance = mstGirlMission.getDistance();

                    if (targetDistance <= form.getDistance().doubleValue()) {
//                        UserGirlVoice userGirlVoice = userGirlVoiceMap.get(mstGirlMission.getKey().getVoiceId());
//                        userGirlVoice.setStatus(UserVoiceStatus.ON.getKey());
//                        userGirlVoice.setUpdDatetime(nowDate);
//                        userGirlVoiceRepository.save(userGirlVoice);

                        userGirlVoiceRepository.updateStatus(TableSuffixGenerator.getUserIdSuffix(form.getUserId()), form.getUserId(), form.getGirlId(), mstGirlMission.getKey().getVoiceId(), UserVoiceStatus.ON.getKey());

                        // 達成ボイスを設定
                        clearVoiceList.add(mstVoiceCache.getMstVoice(mstGirlMission.getKey().getGirlId(), mstGirlMission.getKey().getVoiceId()));
                    }
                }
            } else {

                double userDistance = userGirl.getDistance().doubleValue() + form.getDistance().doubleValue();

                for (MstGirlMission mstGirlMission : girlMissions) {

                    double targetDistance = mstGirlMission.getDistance();

                    if (userGirl.getDistance() < targetDistance && targetDistance <= userDistance) {
//                        UserGirlVoice userGirlVoice = userGirlVoiceMap.get(mstGirlMission.getKey().getVoiceId());
//                        userGirlVoice.setStatus(UserVoiceStatus.ON.getKey());
//                        userGirlVoice.setUpdDatetime(nowDate);
//                        userGirlVoiceRepository.save(userGirlVoice);

                        userGirlVoiceRepository.updateStatus(TableSuffixGenerator.getUserIdSuffix(form.getUserId()), form.getUserId(), form.getGirlId(), mstGirlMission.getKey().getVoiceId(), UserVoiceStatus.ON.getKey());

                        // 達成ボイスを設定
                        clearVoiceList.add(mstVoiceCache.getMstVoice(mstGirlMission.getKey().getGirlId(), mstGirlMission.getKey().getVoiceId()));
                    }
                }
            }
        }

        return clearVoiceList;
    }

    /**
     * ユーザーガールの距離を更新
     * @param form
     * @param userGirl
     * @param nowDate
     */
    private void updateUserGirlDistance(RegistUserActivityForm form, UserGirl userGirl, Date nowDate) {
//        UserGirl record = new UserGirl();
//        BeanUtils.copyProperties(userGirl, record);
//        record.setDistance(record.getDistance() + form.getDistance());
//        record.setUpdDatetime(nowDate);
//        userGirlRepository.save(record);
//
        userGirl.setDistance(userGirl.getDistance() + form.getDistance());
        userGirl.setUpdDatetime(nowDate);

    }

    /**
     * ユーザーの総距離・回数更新
     * @param form
     * @param nowDate
     */
    private void updateUser(RegistUserActivityForm form, String totalAvgTime, Date nowDate) {
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            throw new IllegalStateException("user is null. userId=" + form.getUserId());
        }
        user.setTotalDistance(user.getTotalDistance() + form.getDistance());
        user.setTotalCount(user.getTotalCount().intValue() + 1);
        user.setTotalAvgTime(totalAvgTime);
        user.setUpdDatetime(nowDate);
    }


    /**
     * 新記録更新処理
     * @param userId
     * @param gaolDistance
     * @param goalTime
     * @return true:新記録達成/false:達成してない
     */
    private boolean updateUserGoal(Long userId, Integer goalDistance, Integer goalTime, Double distance) {

        // どちらかしか設定されていない場合
        if (goalDistance == null || goalTime == null) {
            return false;
        }

        // 走行距離が目標距離に達していない場合
        if (Double.valueOf(String.valueOf(goalDistance)) > distance) {
            return false;
        }

//        Date nowDate = MocoDateUtils.getNowDate();
        UserGoal userGoal = userGoalRepository.selectUserGoal(TableSuffixGenerator.getUserIdSuffix(userId), userId, goalDistance);
//        UserGoal userGoal = userGoalRepository.findOne(new UserGoalKey(userId, goalDistance));
        if (userGoal == null) {
//            userGoal = new UserGoal();
//            userGoal.setKey(new UserGoalKey(userId, goalDistance));
//            userGoal.setTime(goalTime);
//            userGoal.setUpdDatetime(nowDate);
//            userGoal.setInsDatetime(nowDate);
//            userGoalRepository.save(userGoal);
            userGoalRepository.insert(TableSuffixGenerator.getUserIdSuffix(userId), userId, goalDistance, goalTime);
            return false;
        } else {
            // 記録更新
            if (goalTime < userGoal.getTime().intValue()) {
//                userGoal.setTime(goalTime);
//                userGoal.setUpdDatetime(nowDate);
                userGoalRepository.updateTime(TableSuffixGenerator.getUserIdSuffix(userId), userId, goalDistance, goalTime);
                return true;
            }
        }
        return false;
    }
}
