package jp.hubfactory.moco.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.hubfactory.moco.bean.UserActivityBean;
import jp.hubfactory.moco.entity.MstGirlMission;
import jp.hubfactory.moco.entity.UserActivity;
import jp.hubfactory.moco.entity.UserActivityDetail;
import jp.hubfactory.moco.entity.UserActivityDetailKey;
import jp.hubfactory.moco.entity.UserActivityKey;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.entity.UserGirlKey;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.entity.UserGirlVoiceKey;
import jp.hubfactory.moco.enums.UserVoiceStatus;
import jp.hubfactory.moco.form.RegistUserActivityDetailForm;
import jp.hubfactory.moco.form.RegistUserActivityForm;
import jp.hubfactory.moco.form.RegistUserActivityLocationForm;
import jp.hubfactory.moco.repository.MstGirlMissionRepository;
import jp.hubfactory.moco.repository.UserActivityDetailRepository;
import jp.hubfactory.moco.repository.UserActivityRepository;
import jp.hubfactory.moco.repository.UserGirlRepository;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class ActivityService {

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

    /**
     * ユーザーのアクティビティ一覧取得
     * @param userId ユーザーID
     * @return
     */
    public List<UserActivityBean> getUserActivities(Long userId) {

        List<UserActivity> userActivities = userActivityRepository.findByUserActivityKeyUserIdOrderByUserActivityKeyActivityIdDesc(userId);
        if (CollectionUtils.isEmpty(userActivities)) {
            return null;
        }

        List<UserActivityBean> beanList = new ArrayList<>();
        for (UserActivity userActivity : userActivities) {

            UserActivityBean bean = new UserActivityBean();
            bean.setUserId(userActivity.getUserActivityKey().getUserId());
            bean.setActivityId(userActivity.getUserActivityKey().getActivityId());
            bean.setRunDate(MocoDateUtils.convertString(userActivity.getRunDate(), MocoDateUtils.DATE_FORMAT_yyyyMMdd_SLASH));
            bean.setDistance(String.format("%.3f", userActivity.getDistance()));
            bean.setTime(userActivity.getTime());
            bean.setAvgTime(userActivity.getAvgTime());
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

        Integer activityId = userActivityRepository.selectMaxActivityId(form.getUserId());
        activityId = activityId == null ? 1 : activityId.intValue() + 1;

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

        // 現在日時取得
        Date nowDate = MocoDateUtils.getNowDate();

        // ***************************************************************************//
        // アクティビティ登録
        // ***************************************************************************//
        UserActivity record = new UserActivity();
        UserActivityKey userActivityKey = new UserActivityKey(form.getUserId(), activityId);
        record.setUserActivityKey(userActivityKey);
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
        System.out.println(locationStr);
        record.setLocations(locationStr);
        userActivityRepository.save(record);

        // ***************************************************************************//
        // アクティビティ詳細登録
        // ***************************************************************************//
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

        // ***************************************************************************//
        // 達成報酬付与
        // ***************************************************************************//

        UserGirl userGirl = userGirlRepository.findOne(new UserGirlKey(form.getUserId(), form.getGirlId()));

        List<MstGirlMission> girlMissions = mstGirlMissionRepository.findByKeyGirlIdOrderByKeyDistanceAsc(form.getGirlId());
        if (CollectionUtils.isNotEmpty(girlMissions)) {

            for (MstGirlMission mstGirlMission : girlMissions) {

                double targetDistance = (double)mstGirlMission.getDistance();

                if (userGirl.getDistance().doubleValue() < targetDistance
                        && targetDistance <= (userGirl.getDistance().doubleValue() + form.getDistance())) {
                    UserGirlVoice userGirlVoice = new UserGirlVoice();
                    userGirlVoice.setUserGirlVoiceKey(new UserGirlVoiceKey(form.getUserId(), form.getGirlId(), mstGirlMission.getRewardVoiceId()));
                    userGirlVoice.setStatus(UserVoiceStatus.ON.getKey());
                    userGirlVoice.setUpdDatetime(nowDate);
                    userGirlVoiceRepository.save(userGirlVoice);
                }
            }
        }


        // ***************************************************************************//
        // ガール距離更新
        // ***************************************************************************//

        // ***************************************************************************//
        // ユーザー総距離更新
        // ***************************************************************************//



        return true;
    }
}
