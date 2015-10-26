package jp.hubfactory.moco.controller;

import java.util.List;

import jp.hubfactory.moco.bean.RankingInfo;
import jp.hubfactory.moco.bean.UserRankingBean;
import jp.hubfactory.moco.cache.MstRankingRewardCache;
import jp.hubfactory.moco.entity.MstRankingReward;
import jp.hubfactory.moco.enums.RankingType;
import jp.hubfactory.moco.form.BaseForm;
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
    @Autowired
    private MstRankingRewardCache mstRankingRewardCache;

    /**
     * ランキング取得
     * @param form
     * @return
     */
    @RequestMapping(value = "/get-list", method = RequestMethod.POST)
    public ResponseEntity<RankingInfo> getList(@Validated @RequestBody RankingForm form) {

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

    /**
     * ランキング履歴取得
     * @param form
     * @return
     */
    @RequestMapping(value = "/history", method = RequestMethod.POST)
    public ResponseEntity<List<UserRankingBean>> getHistory(@Validated @RequestBody BaseForm form) {

        List<UserRankingBean> historyList = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<List<UserRankingBean>>(historyList, HttpStatus.UNAUTHORIZED);
        }

        // ランキング情報取得
        historyList = redisService.getRankingHistory(form.getUserId());

        return new ResponseEntity<List<UserRankingBean>>(historyList, HttpStatus.OK);
    }

    /**
     * ランキング報酬取得
     * @param form
     * @return
     */
    @RequestMapping(value = "/rewards", method = RequestMethod.POST)
    public ResponseEntity<List<MstRankingReward>> getRewardList(@Validated @RequestBody BaseForm form) {

        List<MstRankingReward> rewardList = null;

        if (!super.checkAuth(form.getUserId(), form.getToken())) {
            return new ResponseEntity<List<MstRankingReward>>(rewardList, HttpStatus.UNAUTHORIZED);
        }

        // ランキング報酬取得
        rewardList = mstRankingRewardCache.getList();

        return new ResponseEntity<List<MstRankingReward>>(rewardList, HttpStatus.OK);
    }
}
