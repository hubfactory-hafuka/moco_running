package jp.hubfactory.moco.service;

import java.util.ArrayList;
import java.util.List;

import jp.hubfactory.moco.bean.GirlBean;
import jp.hubfactory.moco.cache.MstGirlCache;
import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GirlService {

    private static final Logger logger = LoggerFactory.getLogger(GirlService.class);

    @Autowired
    private MstGirlCache mstGirlCache;
    @Autowired
    private UserService userService;

    public List<GirlBean> selectActiveGirls(Long userId) {

        List<GirlBean> activeList = new ArrayList<>();

        // 現在有効なガール情報取得
        List<MstGirl> girlList = mstGirlCache.getActiveGirls();
        for (MstGirl mstGirl : girlList) {

            GirlBean bean = new GirlBean();
            BeanUtils.copyProperties(mstGirl, bean);

            // ガール所持しているか判定
            UserGirl userGirl = userService.getUserGirl(userId, mstGirl.getGirlId());
            if (userGirl != null) {
                bean.setHoldFlg(true);
            }
            activeList.add(bean);
        }
        return activeList;
    }

    public GirlBean selectMstGirl(Long userId, Integer girlId) {

        // ユーザー情報取得
        User user = userService.getUser(userId);
        if (user == null) {
            logger.error("user is null. userId=" + userId);
            return null;
        }

        // ガール情報取得
        MstGirl mstGirl = mstGirlCache.getGirl(girlId);
        if (mstGirl == null || !MocoDateUtils.isWithin(mstGirl.getStartDatetime(), mstGirl.getEndDatetime())) {
            return null;
        }

        // ガール所持しているか判定
        UserGirl userGirl = userService.getUserGirl(userId, girlId);
        if (userGirl == null) {
            logger.error("girl not purchase. userId=" + userId + " girlId=" + girlId);
            return null;
        }

        GirlBean bean = new GirlBean();
        BeanUtils.copyProperties(mstGirl, bean);

        if (user.getGirlId().equals(girlId)) {
            bean.setFavoriteFlg(true);
        }
        bean.setHoldFlg(true);

        return bean;
    }
}
