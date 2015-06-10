package jp.hubfactory.moco.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jp.hubfactory.moco.bean.RankingInfo;
import jp.hubfactory.moco.bean.UserRankingBean;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.enums.RankingType;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RankingService {

    private static final String ALL_RANKING = "all_ranking_";
    private static final String GIRL_RANKING = "girl_ranking_";

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    /**
     * 全体ランキング更新
     * @param userId ユーザーID
     * @param distance 距離
     */
    public void updateAllRanking(Long userId, Integer girlId, Double distance) {

        // ユーザー情報取得
        User user = userService.getUser(userId);
        // Redisのランキングキー取得
        String rankingKey = this.getAllRankingKey();
        // ソート済みセット型
        ZSetOperations<String, String> zsetOpsForAll = this.redisTemplate.opsForZSet();
        // 距離データのインクリメント
        String valueForAll = userId.toString() + "_" + user.getName();
        zsetOpsForAll.incrementScore(rankingKey, valueForAll, distance);

        // Redisのランキングキー取得
        String girlRankingKey = this.getGirlRankingKey(girlId);
        // ソート済みセット型
        ZSetOperations<String, String> zsetOpsForGirl = this.redisTemplate.opsForZSet();
        // 距離データのインクリメント
        String valueForGirl = userId.toString() + "_" + user.getName();
        zsetOpsForGirl.incrementScore(girlRankingKey, valueForGirl, distance);
    }

    /**
     * ガール別ランキング更新
     * @param userId ユーザーID
     * @param distance 距離
     */
    public void updateGirlRanking(Long userId, Integer girlId, Double distance) {
        // ユーザー情報取得
        User user = userService.getUser(userId);
        // Redisのランキングキー取得
        String rankingKey = this.getAllRankingKey();
        // ソート済みセット型
        ZSetOperations<String, String> zsetOps = this.redisTemplate.opsForZSet();
        // 距離データのインクリメント
        String value = userId.toString() + "_" + user.getName();
        zsetOps.incrementScore(rankingKey, value, distance);
    }

    public RankingInfo getRankingInfo(Long userId, Integer type, Integer girlId) {

        // 全体ランキングの場合
        String key = null;
        RankingType rankingType = RankingType.valueOf(type);
        switch (rankingType) {
        case ALL:
            key = this.getAllRankingKey();
            break;
        case GIRL:
            key = this.getGirlRankingKey(girlId);
            break;
        default:
            key = this.getAllRankingKey();
            break;
        }
        return this.getRankingInfo(userId, key);
    }

    /**
     * ランキング取得
     * @param userId ユーザーID
     * @param rankingKey ランキングキー
     * @return
     */
    private RankingInfo getRankingInfo(Long userId, String rankingKey) {

        ZSetOperations<String, String> zsetOps = this.redisTemplate.opsForZSet();

        long rank = 0L;
        long start = 0;
        long end = 99;

        List<UserRankingBean> userRankingList = new ArrayList<>();

        // ランキングデータ取得
        Set<TypedTuple<String>> set = zsetOps.reverseRangeWithScores(rankingKey, start, end);

        for (TypedTuple<String> typedTuple : set) {

            // 順位取得
            rank = zsetOps.count(rankingKey, typedTuple.getScore() + 1, Double.MAX_VALUE) + 1;

            String[] userInfos = typedTuple.getValue().split("_");
            UserRankingBean rankingBean = new UserRankingBean();
            rankingBean.setUserId(userInfos[0]);
            rankingBean.setName(userInfos[1]);
            rankingBean.setRank(rank);
            rankingBean.setDistance(typedTuple.getScore());
            userRankingList.add(rankingBean);
        }

        long diff = 0;
        while ((diff = 100 - rank) > 0) {
            start = end + 1;
            end = start + diff - 1;

            // ランキングデータ取得
            set = zsetOps.reverseRangeWithScores(rankingKey, start, end);

            for (TypedTuple<String> typedTuple : set) {
                rank = zsetOps.count(rankingKey, typedTuple.getScore() + 1, Double.MAX_VALUE) + 1;
                if (rank > 100) {
                    rank = 100;
                    break;
                }
                String[] userInfos = typedTuple.getValue().split("_");
                UserRankingBean rankingBean = new UserRankingBean();
                rankingBean.setUserId(userInfos[0]);
                rankingBean.setName(userInfos[1]);
                rankingBean.setRank(rank);
                rankingBean.setDistance(typedTuple.getScore());
                userRankingList.add(rankingBean);
            }
        }

        // ユーザーのランキング情報取得
        User user = userService.getUser(userId);
        String userValue = user.getUserId() + "_" + user.getName();
        double userScore = zsetOps.score(rankingKey, userValue);
        long userRank = zsetOps.count(rankingKey, userScore + 1, Double.MAX_VALUE) + 1;

        UserRankingBean myRankingBean = new UserRankingBean();
        myRankingBean.setUserId(userId.toString());
        myRankingBean.setName(user.getName());
        myRankingBean.setRank(userRank);
        myRankingBean.setDistance(userScore);

        RankingInfo rankingInfo = new RankingInfo();
        rankingInfo.setUserRank(myRankingBean);
        rankingInfo.setRankList(userRankingList);

        return rankingInfo;
    }

    /**
     * 全体ランキングキー取得
     * @return
     */
    private String getAllRankingKey() {
        Date now = MocoDateUtils.getNowDate();
        return ALL_RANKING + MocoDateUtils.convertString(now, MocoDateUtils.DATE_FORMAT_yyyyMM);
    }

    /**
     * ガール別ランキングキー取得
     * @param girlId
     * @return
     */
    private String getGirlRankingKey(Integer girlId) {
        Date now = MocoDateUtils.getNowDate();
        return GIRL_RANKING + girlId + "_" + MocoDateUtils.convertString(now, MocoDateUtils.DATE_FORMAT_yyyyMM);
    }

}
