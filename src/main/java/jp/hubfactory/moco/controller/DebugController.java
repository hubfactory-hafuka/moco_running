package jp.hubfactory.moco.controller;

import java.util.Date;
import java.util.Map;

import jp.hubfactory.moco.MocoProperties;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserRankingPoint;
import jp.hubfactory.moco.repository.UserRankingPointRepository;
import jp.hubfactory.moco.repository.UserRepository;
import jp.hubfactory.moco.service.RedisService;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
@RequestMapping(value="/debug")
public class DebugController extends BaseController {

    @Autowired
    private MocoProperties mocoProperties;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserRankingPointRepository userRankingPointRepository;

    /**
     * ログインボーナスリセット
     * @param form
     * @return
     */
    @RequestMapping(value = "/reset-login-bonus", method = RequestMethod.POST)
    public ResponseEntity<Boolean> resetLoginBonus(@RequestBody Map<String, Long> map) {

        if (!mocoProperties.getSystem().isDebug()) {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        // ユーザー情報存在チェック判定
        User user = userRepository.findOne(map.get("userId"));
        if (user == null) {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
        user.setLoginBonusDatetime(null);
        redisService.updateUser(user);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    /**
     * ログインボーナスリセット
     * @param form
     * @return
     */
    @RequestMapping(value = "/add-ranking-point", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addRankingPoint(@RequestBody Map<String, Long> map) {

        if (!mocoProperties.getSystem().isDebug()) {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        Date nowDate = MocoDateUtils.getNowDate();
        UserRankingPoint userRankingPoint = new UserRankingPoint(map.get("userId"), "2015/10", 1L, 100000L, nowDate, nowDate);
        userRankingPointRepository.save(userRankingPoint);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
}
