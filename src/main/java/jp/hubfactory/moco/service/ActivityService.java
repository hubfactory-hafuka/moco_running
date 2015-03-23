package jp.hubfactory.moco.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.bean.UserActivityBean;
import jp.hubfactory.moco.bean.UserActivityDetailBean;
import jp.hubfactory.moco.bean.UserActivityLocationBean;
import jp.hubfactory.moco.entity.MstGirlMission;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserActivity;
import jp.hubfactory.moco.entity.UserActivityDetail;
import jp.hubfactory.moco.entity.UserActivityDetailKey;
import jp.hubfactory.moco.entity.UserActivityKey;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.entity.UserGirlKey;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.form.RegistUserActivityDetailForm;
import jp.hubfactory.moco.form.RegistUserActivityForm;
import jp.hubfactory.moco.form.RegistUserActivityLocationForm;
import jp.hubfactory.moco.repository.MstGirlMissionRepository;
import jp.hubfactory.moco.repository.UserActivityDetailRepository;
import jp.hubfactory.moco.repository.UserActivityRepository;
import jp.hubfactory.moco.repository.UserGirlRepository;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.repository.UserRepository;
import jp.hubfactory.moco.util.MocoDateUtils;

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
    private MstGirlMissionRepository mstGirlMissionRepository;
    @Autowired
    private UserGirlRepository userGirlRepository;
    @Autowired
    private UserGirlVoiceRepository userGirlVoiceRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * ユーザーのアクティビティ一覧取得
     * @param userId ユーザーID
     * @return
     */
    public List<UserActivityBean> getUserActivities(Long userId) {

        List<UserActivity> userActivities = userActivityRepository.findByKeyUserIdOrderByKeyActivityIdDesc(userId);
        if (CollectionUtils.isEmpty(userActivities)) {
            return null;
        }

        Map<Integer, List<UserActivityDetail>> activityIdKeyMap = new HashMap<>();

        List<UserActivityDetail> userActivityDetailAll = userActivityDetailRepository.findByKeyUserIdOrderByKeyActivityIdAsc(userId);
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
            bean.setDistance(String.format("%.3f", userActivity.getDistance()));
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
    public boolean addUserActivity(RegistUserActivityForm form) {

        // 現在日時取得
        Date nowDate = MocoDateUtils.getNowDate();

        Integer activityId = userActivityRepository.findMaxActivityId(form.getUserId());
        activityId = activityId == null ? 1 : activityId.intValue() + 1;

        // ユーザーの現在のガール情報取得
        UserGirl userGirl = userGirlRepository.findOne(new UserGirlKey(form.getUserId(), form.getGirlId()));

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
        this.updateUserGirl(form, userGirl, nowDate);

        // ***************************************************************************//
        // ガール距離更新
        // ***************************************************************************//
        this.updateUserGirlDistance(form, userGirl, nowDate);

        // ***************************************************************************//
        // ユーザー総距離更新
        // ***************************************************************************//
        this.updateUser(form, nowDate);

        return true;
    }

    private void registActivity(RegistUserActivityForm form, Integer activityId, Date nowDate) {

        Date runDate = MocoDateUtils.convertDate(form.getRunDate(), MocoDateUtils.DATE_FORMAT_yyyyMMdd_SLASH);

        // 走った時間を秒に変換する
        String times[] = form.getTime().split(":");
        int hour = Integer.parseInt(times[0]) * 3600;
        int minute = Integer.parseInt(times[1]) * 60;
        int second = Integer.parseInt(times[2]);
        int timeSecond = hour + minute + second;

        // 平均時間(秒)を求める
        int avgTimeSecond = Double.valueOf(Math.ceil(timeSecond / form.getDistance())).intValue();
        int avgMinute = avgTimeSecond % 3600 / 60;
        int avgSecond = avgTimeSecond % 60;
        // 平均時間文字列
        String avgTime = MocoDateUtils.convertTimeString(avgMinute, avgSecond);

        UserActivity record = new UserActivity();
        UserActivityKey userActivityKey = new UserActivityKey(form.getUserId(), activityId);
        record.setKey(userActivityKey);
        record.setGirlId(form.getGirlId());
        record.setDistance(form.getDistance());
        record.setRunDate(runDate);
        record.setTime(form.getTime());
        record.setAvgTime(avgTime);
        record.setUpdDatetime(nowDate);
        record.setInsDatetime(nowDate);

        String locationStr = null;
        ObjectMapper mapper = new ObjectMapper();
        List<RegistUserActivityLocationForm> locations = form.getLocations();
        try {
            locationStr = mapper.writeValueAsString(locations);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalStateException("JSON変換エラー:" + e.toString());
        }
        record.setLocations(locationStr);
        userActivityRepository.save(record);
    }

    private void registActivityDetail(RegistUserActivityForm form, Integer activityId, Date nowDate) {

        List<RegistUserActivityDetailForm> detailList = form.getDetails();
        List<UserActivityDetail> detailRecords = new ArrayList<>(detailList.size());
        int index = 1;
        for (RegistUserActivityDetailForm registUserActivityDetailForm : detailList) {

            UserActivityDetailKey detailKey = new UserActivityDetailKey(form.getUserId(), activityId, index);
            UserActivityDetail detailRecord = new UserActivityDetail();
            detailRecord.setKey(detailKey);
            detailRecord.setDistance(registUserActivityDetailForm.getDistance());
            detailRecord.setIncDecTime(registUserActivityDetailForm.getIncDecTime());
            detailRecord.setLapTime(registUserActivityDetailForm.getLapTime());
            detailRecord.setTimeElapsed(registUserActivityDetailForm.getTimeElapsed());
            detailRecord.setUpdDatetime(nowDate);
            detailRecord.setInsDatetime(nowDate);
            detailRecords.add(detailRecord);
            index++;
        }
        userActivityDetailRepository.save(detailRecords);
    }

    /**
     * ミッション達成報酬付与処理
     * @param form
     * @param nowDate
     */
    private void updateUserGirl(RegistUserActivityForm form, UserGirl userGirl, Date nowDate) {

        // ガールミッション情報取得
        List<MstGirlMission> girlMissions = mstGirlMissionRepository.findByKeyGirlIdOrderByDistanceAsc(form.getGirlId());
        // ミッション情報が存在する場合
        if (CollectionUtils.isNotEmpty(girlMissions)) {

            Map<Integer, UserGirlVoice> userGirlVoiceMap = new HashMap<>();
            List<UserGirlVoice> userGirlVoiceList = userGirlVoiceRepository.findByKeyUserIdAndKeyGirlId(form.getUserId(), form.getGirlId());
            for (UserGirlVoice userGirlVoice : userGirlVoiceList) {
                userGirlVoiceMap.put(userGirlVoice.getKey().getVoiceId(), userGirlVoice);
            }


            if (userGirl == null) {

                for (MstGirlMission mstGirlMission : girlMissions) {

                    double targetDistance = mstGirlMission.getDistance();

                    if (targetDistance <= form.getDistance()) {
                        UserGirlVoice userGirlVoice = userGirlVoiceMap.get(mstGirlMission.getKey().getVoiceId());
                        userGirlVoice.setStatus(UserVoiceStatus.ON.getKey());
                        userGirlVoice.setUpdDatetime(nowDate);
//                        userGirlVoiceRepository.save(userGirlVoice);
                    }
                }
            } else {

                for (MstGirlMission mstGirlMission : girlMissions) {

                    double targetDistance = mstGirlMission.getDistance();
                    double userDistance = userGirl.getDistance().doubleValue() + form.getDistance().doubleValue();

                    if (userDistance <= targetDistance && targetDistance <= userDistance) {
                        UserGirlVoice userGirlVoice = userGirlVoiceMap.get(mstGirlMission.getKey().getVoiceId());
                        userGirlVoice.setStatus(UserVoiceStatus.ON.getKey());
                        userGirlVoice.setUpdDatetime(nowDate);
//                        userGirlVoiceRepository.save(userGirlVoice);
                    }
                }
            }
        }
    }

    /**
     * ユーザーガールの距離を更新
     * @param form
     * @param userGirl
     * @param nowDate
     */
    private void updateUserGirlDistance(RegistUserActivityForm form, UserGirl userGirl, Date nowDate) {
        if (userGirl == null) {
            UserGirl record = new UserGirl();
            record.setKey(new UserGirlKey(form.getUserId(), form.getGirlId()));
            record.setDistance(form.getDistance());
            record.setUpdDatetime(nowDate);
            record.setInsDatetime(nowDate);
            userGirlRepository.save(record);
        } else {
            UserGirl record = new UserGirl();
            BeanUtils.copyProperties(userGirl, record);
            record.setDistance(record.getDistance() + form.getDistance());
            record.setUpdDatetime(nowDate);
            userGirlRepository.save(record);
        }
    }

    /**
     * ユーザーの総距離更新
     * @param form
     * @param nowDate
     */
    private void updateUser(RegistUserActivityForm form, Date nowDate) {
        User user = userRepository.findOne(form.getUserId());
        if (user == null) {
            throw new IllegalStateException("user is null. userId=" + form.getUserId());
        }
        user.setTotalDistance(user.getTotalDistance() + form.getDistance());
        user.setUpdDatetime(nowDate);
    }
}
