package jp.hubfactory.moco.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jp.hubfactory.moco.bean.RankingInfo;
import jp.hubfactory.moco.bean.UserRankingBean;
import jp.hubfactory.moco.cache.MstRankingCache;
import jp.hubfactory.moco.entity.MstRanking;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.enums.RankingType;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RedisService {

    private static final String ALL_RANKING_KEY = "all_ranking_";
    private static final String GIRL_RANKING_KEY = "girl_ranking_";
    private static final String USER_KEY = "user";

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private MstRankingCache mstRankingCache;

    /**
     * Redisのユーザー情報更新
     * @param user
     */
    public void updateUser(User user) {

        ObjectMapper mapper = new ObjectMapper();

        // UserエンティティをJson文字列に変換
        String userJson = null;
        try {
            userJson = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON変換エラー");
        }

        HashOperations<String, Object, Object> hashOps = this.redisTemplate.opsForHash();
        hashOps.put(USER_KEY, user.getUserId().toString(), userJson);

    }

    /**
     * Redisのユーザー情報取得
     * @param userId ユーザーID
     * @return
     */
    public User getUser(Long userId) {

        String userKey = userId.toString();

        HashOperations<String, Object, Object> hashOps = this.redisTemplate.opsForHash();
        String userJson = (String)hashOps.get(USER_KEY, userKey);

        if (StringUtils.isEmpty(userJson)) {
            return null;
        }
        try {
            return new ObjectMapper().readValue(userJson, User.class);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * ランキング更新
     * @param userId ユーザーID
     * @param girlId ガールID
     * @param distance 距離
     */
    public void updateRanking(Long userId, Integer girlId, Double distance) {

        Date nowDate = MocoDateUtils.getNowDate();
        // 距離を小数点第２位で切り捨て
        Double updDistance = new BigDecimal(String.valueOf(distance)).setScale(2, RoundingMode.FLOOR).doubleValue();

        MstRanking mstRankingAll = mstRankingCache.getMstRanking(RankingType.ALL.getKey());
        if (mstRankingAll != null && MocoDateUtils.isWithin(mstRankingAll.getStartDatetime(), mstRankingAll.getEndDatetime(), nowDate)) {
            // Redisの全体ランキングキー取得
            String rankingKey = this.getAllRankingKey(nowDate);
            // ソート済みセット型
            ZSetOperations<String, String> zsetOpsForAll = this.redisTemplate.opsForZSet();
            // 距離データのインクリメント
            zsetOpsForAll.incrementScore(rankingKey, userId.toString(), updDistance);
        }

        MstRanking mstRankingGirl = mstRankingCache.getMstRanking(RankingType.GIRL.getKey());
        if (mstRankingGirl != null && MocoDateUtils.isWithin(mstRankingGirl.getStartDatetime(), mstRankingGirl.getEndDatetime(), nowDate)) {
            // Redisのガール別ランキングキー取得
            String girlRankingKey = this.getGirlRankingKey(girlId, nowDate);
            // ソート済みセット型
            ZSetOperations<String, String> zsetOpsForGirl = this.redisTemplate.opsForZSet();
            // 距離データのインクリメント
            zsetOpsForGirl.incrementScore(girlRankingKey, userId.toString(), updDistance);
        }
    }

    /**
     * ランキング情報取得
     * @param userId ユーザーID
     * @param type ランキング種別
     * @param girlId ガールID
     * @return
     */
    public RankingInfo getRankingInfo(Long userId, Integer type, Integer girlId) {

        Date nowDate = MocoDateUtils.getNowDate();

        // 全体ランキングの場合
        String key = null;
        RankingType rankingType = RankingType.valueOf(type);
        switch (rankingType) {
        case ALL:
            key = this.getAllRankingKey(nowDate);
            break;
        case GIRL:
            key = this.getGirlRankingKey(girlId, nowDate);
            break;
        default:
            key = this.getAllRankingKey(nowDate);
            break;
        }
        MstRanking mstRanking = mstRankingCache.getMstRanking(type);
        Integer limit = mstRanking == null ? 100 : mstRanking.getViewNum();

        return this.getRankingInfo(userId, key, limit);
    }

    /**
     * ランキング取得
     * @param userId ユーザーID
     * @param rankingKey ランキングキー
     * @return
     */
    private RankingInfo getRankingInfo(Long userId, String rankingKey, Integer limit) {

        ZSetOperations<String, String> zsetOps = this.redisTemplate.opsForZSet();

        // ユーザーのランキング情報取得
        User user = userService.getUser(userId);

        UserRankingBean myRankingBean = new UserRankingBean();
        myRankingBean.setUserId(userId);
        myRankingBean.setName(user.getName());
        myRankingBean.setGirlId(user.getGirlId());

        // ランキング情報に自分が存在する場合
        if (zsetOps.rank(rankingKey, userId.toString()) != null) {

            Double userScore = zsetOps.score(rankingKey, userId.toString());
            // 小数点第以下切り捨て
            userScore = new BigDecimal(String.valueOf(userScore)).setScale(2, RoundingMode.FLOOR).doubleValue();
            Long userRank = zsetOps.count(rankingKey, userScore + 1, Double.MAX_VALUE) + 1;
            myRankingBean.setRank(userRank);
            myRankingBean.setDistance(userScore);
        }

        long rank = 0L;
        long start = 0;
        long end = limit - 1;

        // ランキングデータ取得
        List<UserRankingBean> userRankingList = new ArrayList<>();
        Set<TypedTuple<String>> set = zsetOps.reverseRangeWithScores(rankingKey, start, end);
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }

        for (TypedTuple<String> typedTuple : set) {

            // 順位取得
            rank = zsetOps.count(rankingKey, typedTuple.getScore() + 1, Double.MAX_VALUE) + 1;
            // ユーザーID
            Long rankUserId = Long.valueOf(typedTuple.getValue());
            // ユーザー情報
            User rankUser = userService.getUser(rankUserId);
            if (rankUser == null) {
                continue;
            }
            // 小数点第以下切り捨て
            Double userScore = new BigDecimal(String.valueOf(typedTuple.getScore())).setScale(2, RoundingMode.FLOOR).doubleValue();

            UserRankingBean rankingBean = new UserRankingBean();
            rankingBean.setUserId(rankUserId);
            rankingBean.setName(rankUser.getName());
            rankingBean.setRank(rank);
            rankingBean.setDistance(userScore);
            rankingBean.setGirlId(rankUser.getGirlId());
            userRankingList.add(rankingBean);
        }


//        long diff = 0;
//        while ((diff = 100 - rank) > 0) {
//            start = end + 1;
//            end = start + diff - 1;
//
//            // ランキングデータ取得
//            set = zsetOps.reverseRangeWithScores(rankingKey, start, end);
//
//            for (TypedTuple<String> typedTuple : set) {
//                rank = zsetOps.count(rankingKey, typedTuple.getScore() + 1, Double.MAX_VALUE) + 1;
//                if (rank > 100) {
//                    rank = 100;
//                    break;
//                }
//
//                // ユーザーID
//                Long rankUserId = Long.valueOf(typedTuple.getValue());
//                // ユーザー情報
//                User rankUser = userService.getUser(rankUserId);
//                if (rankUser == null) {
//                    continue;
//                }
//
//                UserRankingBean rankingBean = new UserRankingBean();
//                rankingBean.setUserId(rankUserId);
//                rankingBean.setName(rankUser.getName());
//                rankingBean.setRank(rank);
//                rankingBean.setDistance(typedTuple.getScore());
//                userRankingList.add(rankingBean);
//            }
//        }

        RankingInfo rankingInfo = new RankingInfo();
        rankingInfo.setUserRank(myRankingBean);
        rankingInfo.setRankList(userRankingList);

        return rankingInfo;
    }

    /**
     * 全体ランキングキー取得
     * @return
     */
    private String getAllRankingKey(Date targetDate) {
        return ALL_RANKING_KEY + MocoDateUtils.convertString(targetDate, MocoDateUtils.DATE_FORMAT_yyyyMM);
    }

    /**
     * ガール別ランキングキー取得
     * @param girlId
     * @return
     */
    private String getGirlRankingKey(Integer girlId, Date targetDate) {
        return GIRL_RANKING_KEY + girlId + "_" + MocoDateUtils.convertString(targetDate, MocoDateUtils.DATE_FORMAT_yyyyMM);
    }
}
