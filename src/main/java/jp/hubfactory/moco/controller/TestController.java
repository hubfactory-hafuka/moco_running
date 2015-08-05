package jp.hubfactory.moco.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jp.hubfactory.moco.bean.RankingInfo;
import jp.hubfactory.moco.bean.UserRankingBean;
import jp.hubfactory.moco.form.RankingForm;
import jp.hubfactory.moco.service.RedisService;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
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
@RequestMapping(value="/test")
public class TestController extends BaseController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/get-list", method = RequestMethod.POST)
    public ResponseEntity<RankingInfo> getActivity(@Validated @RequestBody RankingForm form) throws IllegalAccessException, IOException {

        // ランキング情報取得
        RankingInfo rankingInfo = new RankingInfo();

        ZSetOperations<String, String> zsetOps = this.redisTemplate.opsForZSet();

        long rank = 0L;
        long start = 0;
        long end = 100 - 1;

        // ランキングデータ取得
        List<UserRankingBean> userRankingList = new ArrayList<>();
        Set<TypedTuple<String>> set = zsetOps.reverseRangeWithScores("all_ranking_201508", start, end);
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }

        for (TypedTuple<String> typedTuple : set) {



            long tmpRank = zsetOps.reverseRank("all_ranking_201508", typedTuple.getValue()) + 1;


            // 順位取得
            rank = zsetOps.count("all_ranking_201508", typedTuple.getScore() + 1.0, Double.MAX_VALUE) + 1;
            // ユーザーID
            Long rankUserId = Long.valueOf(typedTuple.getValue());

            // 小数点第以下切り捨て
            Double userScore = new BigDecimal(String.valueOf(typedTuple.getScore())).setScale(2, RoundingMode.FLOOR).doubleValue();

            UserRankingBean rankingBean = new UserRankingBean();
            rankingBean.setUserId(rankUserId);
            rankingBean.setRank(tmpRank);
            rankingBean.setDistance(userScore);
            userRankingList.add(rankingBean);
        }

        rankingInfo.setRankList(userRankingList);

        return new ResponseEntity<RankingInfo>(rankingInfo, HttpStatus.OK);
    }
}
