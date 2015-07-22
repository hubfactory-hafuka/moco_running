package jp.hubfactory.moco.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private StringRedisTemplate template;

    @RequestMapping(value = "/add-data", method = RequestMethod.POST)
    public ResponseEntity<Boolean> getActivity() throws IllegalAccessException, IOException {

//        ValueOperations<String, String> ops = this.template.opsForValue();
//        String key = "spring.boot.redis.test";
//        if (!this.template.hasKey(key)) {
//            ops.set(key, "foo");
//            System.out.println(ops.get(key));
//        }
//
         BigDecimal L01 = new BigDecimal("3.36");
         System.out.println("元の値　：" + L01);
         System.out.println("切り上げ：" + L01.setScale(2, RoundingMode.CEILING));
         System.out.println("切り捨て：" + L01.setScale(2, RoundingMode.FLOOR));
         System.out.println("四捨五入：" + L01.setScale(2, RoundingMode.HALF_UP));

        String key = "ranking_1";
        ZSetOperations<String, String> zsetOps = this.template.opsForZSet();
        if (!this.template.hasKey(key)) {
            System.out.println("keyがありません");
            for (int i = 1; i <= 30; i++) {
                String userId = "100" + String.valueOf(i);

                // 距離を小数点第２位で切り捨て
                double score = new BigDecimal(String.valueOf(3.36)).setScale(2, RoundingMode.FLOOR).doubleValue();
                zsetOps.incrementScore(key, userId, score);
            }
        }

        long rank = 0L;
        long start = 0;
        long end = 99;

        Set<TypedTuple<String>> set = zsetOps.reverseRangeWithScores(key, start, end);

        if (set.size() == 0) {
            System.out.println("データがありません。");
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        }

        for (TypedTuple<String> typedTuple : set) {
            rank = zsetOps.count(key, typedTuple.getScore() + 1, Double.MAX_VALUE) + 1;
            String data = "順位:" + rank + " " + typedTuple.getValue() + ":" + typedTuple.getScore();
            System.out.println(data);
        }

        Long ranks = zsetOps.reverseRank(key, "10010");


//        Cursor<TypedTuple<String>> aa = zsetOps.scan(key, ScanOptions.scanOptions().match("15").build());
//        if (aa.hasNext()) {
//            System.out.println("aa");
//        } else {
//            System.out.println("bb");
//        }

        // ユーザーのランキング情報取得
//        double userScore = zsetOps.score(key, "99999");
//        long diff = 0;
//
//        while ((diff = set.size() - rank) > 0) {
//            start = end + 1;
//            end = start + diff - 1;
//
//            set = zsetOps.reverseRangeWithScores(key, start, end);
//
//            for (TypedTuple<String> typedTuple : set) {
//                rank = zsetOps.count(key, typedTuple.getScore() + 1, Double.MAX_VALUE) + 1;
//                if (rank > 100) {
//                    rank = 100;
//                    break;
//                }
//
//                String data = "順位:" + rank + " " + typedTuple.getValue() + ":" + typedTuple.getScore();
//                System.out.println(data);
//            }
//        }



        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
}
