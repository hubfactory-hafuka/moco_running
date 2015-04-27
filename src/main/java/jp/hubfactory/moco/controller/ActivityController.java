package jp.hubfactory.moco.controller;

import java.util.List;

import jp.hubfactory.moco.bean.ActivityResultBean;
import jp.hubfactory.moco.bean.UserActivityBean;
import jp.hubfactory.moco.form.BaseForm;
import jp.hubfactory.moco.form.RegistUserActivityForm;
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
public class ActivityController extends BaseController {

    @Autowired
    private ActivityService activityServie;

    /**
     * アクティビティ一覧取得
     * @param form
     * @return
     */
    @RequestMapping(value = "/get-user-activity", method = RequestMethod.POST)
    public ResponseEntity<List<UserActivityBean>> getActivity(@Validated @RequestBody BaseForm form) {

        List<UserActivityBean> userActivities = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<List<UserActivityBean>>(userActivities, HttpStatus.UNAUTHORIZED);

        }
        userActivities = activityServie.getUserActivities(form.getUserId());
        return new ResponseEntity<List<UserActivityBean>>(userActivities, HttpStatus.OK);
    }

    /**
     * アクティビティ登録
     * @param form
     * @return
     */
    @RequestMapping(value = "/add-user-activity", method = RequestMethod.POST)
    public ResponseEntity<ActivityResultBean> addActivity(@Validated @RequestBody RegistUserActivityForm form) {

        ActivityResultBean resultBean = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<ActivityResultBean>(resultBean, HttpStatus.UNAUTHORIZED);
        }

        resultBean = activityServie.addUserActivity(form);
        if (resultBean == null) {
            return new ResponseEntity<ActivityResultBean>(resultBean, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<ActivityResultBean>(resultBean, HttpStatus.OK);
    }
}
