package jp.hubfactory.moco.controller;

import java.util.List;

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
@RequestMapping(value="/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityServie;

    @RequestMapping(value = "/get-user-activity", method = RequestMethod.POST)
    public ResponseEntity<List<UserActivityBean>> getActivity(@Validated @RequestBody UserActivityForm form) {
        List<UserActivityBean> userActivities = activityServie.getUserActivities(form.getUserId());
        return new ResponseEntity<List<UserActivityBean>>(userActivities, HttpStatus.OK);
    }

    @RequestMapping(value = "/add-user-activity", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addActivity(@Validated @RequestBody RegistUserActivityForm form) {
        activityServie.addUserActivity(form);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
}
