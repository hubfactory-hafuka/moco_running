package jp.hubfactory.moco.cache;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.entity.MstLoginBonus;
import jp.hubfactory.moco.repository.MstLoginBonusRepository;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstLoginBonusCache {

    @Autowired
    MstLoginBonusRepository repository;

    private List<MstLoginBonus> list;

    private Map<Integer, MstLoginBonus> map;

    public void load() {
        list = repository.findAll();
        this.createData();
    }

    public MstLoginBonus getLoginBonus(Date date) {
        if (map == null) {
            this.load();
        }

        int week = MocoDateUtils.getWeek(date);
        MstLoginBonus mstLoginBonus = map.get(week);
        if (mstLoginBonus != null && MocoDateUtils.isWithin(mstLoginBonus.getStartDatetime(), mstLoginBonus.getEndDatetime(), date)) {
            return mstLoginBonus;
        }
        return null;

    }

    private void createData() {
        Map<Integer, MstLoginBonus> tmpMap = new HashMap<>();

        for (MstLoginBonus data : list) {
            tmpMap.put(data.getWeek(), data);
        }
        map = Collections.unmodifiableMap(tmpMap);
    }
}
