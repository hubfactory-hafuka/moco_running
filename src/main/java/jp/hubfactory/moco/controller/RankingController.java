package jp.hubfactory.moco.controller;

import jp.hubfactory.moco.bean.RankingInfo;
import jp.hubfactory.moco.enums.RankingType;
import jp.hubfactory.moco.form.RankingForm;
import jp.hubfactory.moco.service.RedisService;

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
@RequestMapping(value="/ranking")
public class RankingController extends BaseController {

    @Autowired
    private RedisService redisService;

    /**
     * アクティビティ一覧取得
     * @param form
     * @return
     */
    @RequestMapping(value = "/get-list", method = RequestMethod.POST)
    public ResponseEntity<RankingInfo> getActivity(@Validated @RequestBody RankingForm form) {

        RankingInfo rankingInfo = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<RankingInfo>(rankingInfo, HttpStatus.UNAUTHORIZED);
        }

        RankingType rankingType = RankingType.valueOf(form.getType());
        if (RankingType.GIRL == rankingType && form.getGirlId() == null) {
            return new ResponseEntity<RankingInfo>(rankingInfo, HttpStatus.BAD_REQUEST);
        }

        // ランキング情報取得
        rankingInfo = redisService.getRankingInfo(form.getUserId(), form.getType(), form.getGirlId());

        return new ResponseEntity<RankingInfo>(rankingInfo, HttpStatus.OK);
    }
}
