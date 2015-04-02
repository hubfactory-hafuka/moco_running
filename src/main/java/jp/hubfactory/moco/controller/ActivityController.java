package jp.hubfactory.moco.controller;

import java.util.List;

import jp.hubfactory.moco.bean.MissionClearVoiceBean;
import jp.hubfactory.moco.bean.UserActivityBean;
import jp.hubfactory.moco.form.RegistUserActivityForm;
import jp.hubfactory.moco.form.UserActivityForm;
import jp.hubfactory.moco.service.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * アクティビティコントローラー
 */
@RestController
@RequestMapping(value="/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityServie;

    /**
     * アクティビティ一覧取得
     * @param form
     * @return
     */
    @RequestMapping(value = "/get-user-activity", method = RequestMethod.POST)
    public ResponseEntity<List<UserActivityBean>> getActivity(@Validated @RequestBody UserActivityForm form) {
        List<UserActivityBean> userActivities = activityServie.getUserActivities(form.getUserId());
        return new ResponseEntity<List<UserActivityBean>>(userActivities, HttpStatus.OK);
    }

    /**
     * アクティビティ登録
     * @param form
     * @return
     */
    @RequestMapping(value = "/add-user-activity", method = RequestMethod.POST)
    public ResponseEntity<List<MissionClearVoiceBean>> addActivity(@Validated @RequestBody RegistUserActivityForm form) {
        List<MissionClearVoiceBean> missionClearVoiceBeans = activityServie.addUserActivity(form);
        return new ResponseEntity<List<MissionClearVoiceBean>>(missionClearVoiceBeans, HttpStatus.OK);
    }
}