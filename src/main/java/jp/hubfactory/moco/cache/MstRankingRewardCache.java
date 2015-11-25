package jp.hubfactory.moco.cache;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.hubfactory.moco.entity.MstRankingReward;
import jp.hubfactory.moco.repository.MstRankingRewardRepository;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstRankingRewardCache {

    @Autowired
    MstRankingRewardRepository repository;

    private List<MstRankingReward> list;

    public void load() {
        list = repository.findAll();
        this.sort();
        list = Collections.unmodifiableList(list);
    }

    public List<MstRankingReward> getList() {
        return list;
    }

    public Long getPoint(int userRank) {
        if (CollectionUtils.isEmpty(list)) {
            this.load();
        }

        for (MstRankingReward mstRankingReward : list) {
            if (mstRankingReward.getFromRank().intValue() <= userRank && userRank <= mstRankingReward.getToRank().intValue()) {
                return mstRankingReward.getPoint();
            }
        }
        return null;
    }

    private void sort() {
        Collections.sort(list, new Comparator<MstRankingReward>() {
            @Override
            public int compare(MstRankingReward o1, MstRankingReward o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
    }
}
