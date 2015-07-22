package jp.hubfactory.moco.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.entity.MstRanking;
import jp.hubfactory.moco.repository.MstRankingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstRankingCache {

    @Autowired
    MstRankingRepository repository;

    private List<MstRanking> list;

    private Map<Integer, MstRanking> rankingMap;


    public void load() {
        list = repository.findAll();
        this.createData();
    }

    public MstRanking getMstRanking(Integer type) {
        if (rankingMap == null) {
            load();
        }
        return rankingMap.get(type);
    }

    private void createData() {
        Map<Integer, MstRanking> tmpMap = new HashMap<>();

        for (MstRanking data : list) {
            tmpMap.put(data.getType(), data);
        }
        rankingMap = Collections.unmodifiableMap(tmpMap);
    }
}
