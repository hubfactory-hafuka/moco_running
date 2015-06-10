package jp.hubfactory.moco.controller;

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
    public ResponseEntity<Boolean> getActivity() {

//        ValueOperations<String, String> ops = this.template.opsForValue();
//        String key = "spring.boot.redis.test";
//        if (!this.template.hasKey(key)) {
//            ops.set(key, "foo");
//            System.out.println(ops.get(key));
//        }
//

        String key = "ranking_1";
        ZSetOperations<String, String> zsetOps = this.template.opsForZSet();

//        for (int i = 1; i <= 10000; i++) {
//            String userId = "100" + String.valueOf(i);
//
//            double score = Double.valueOf(i);
//            if (i == 9902 || i == 9901) {
//                score = 9900D;
//            }
//            zsetOps.incrementScore(key, userId, score);
//        }

        long rank = 0L;
        long start = 0;
        long end = 99;

        Set<TypedTuple<String>> set = zsetOps.reverseRangeWithScores(key, start, end);
        System.out.println(set.size());
        for (TypedTuple<String> typedTuple : set) {
            rank = zsetOps.count(key, typedTuple.getScore() + 1, Double.MAX_VALUE) + 1;
            String data = "順位:" + rank + " " + typedTuple.getValue() + ":" + typedTuple.getScore();
            System.out.println(data);
        }
        long diff = 0;

        while ((diff = 100 - rank) > 0) {
            start = end + 1;
            end = start + diff - 1;

            set = zsetOps.reverseRangeWithScores(key, start, end);

            for (TypedTuple<String> typedTuple : set) {
                rank = zsetOps.count(key, typedTuple.getScore() + 1, Double.MAX_VALUE) + 1;
                if (rank > 100) {
                    rank = 100;
                    break;
                }

                String data = "順位:" + rank + " " + typedTuple.getValue() + ":" + typedTuple.getScore();
                System.out.println(data);
            }
        }



        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
}
