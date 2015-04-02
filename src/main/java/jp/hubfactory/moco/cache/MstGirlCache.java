package jp.hubfactory.moco.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.repository.MstGirlRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstGirlCache {

    @Autowired
    MstGirlRepository repository;

    private List<MstGirl> list;

    private Map<Integer, MstGirl> girlMap;

    private Map<Integer, List<MstGirl>> girlTypeListMap;

    public void load() {
        list = repository.findAll();
        this.sort();
        this.createData();
    }

    public List<MstGirl> getAll() {
        return list;
    }

    public MstGirl getGirl(Integer girlId) {
        if (girlMap == null) {
            this.load();
        }
        return girlMap.get(girlId);
    }

    public List<MstGirl> getGirlTypeList(Integer type) {
        if (girlTypeListMap == null) {
            this.load();
        }
        return girlTypeListMap.get(type);
    }

    private void sort() {
        Collections.sort(list, new Comparator<MstGirl>() {
            @Override
            public int compare(MstGirl o1, MstGirl o2) {
                return o1.getGirlId().compareTo(o2.getGirlId());
            }
        });
    }

    private void createData() {
        Map<Integer, MstGirl> tmpMap = new HashMap<>();
        Map<Integer, List<MstGirl>> tmpMap2 = new HashMap<>();

        for (MstGirl mstGirl : list) {
            tmpMap.put(mstGirl.getGirlId(), mstGirl);


            List<MstGirl> list = (tmpMap2.containsKey(mstGirl.getType())) ? tmpMap2.get(mstGirl.getType()) : new ArrayList<MstGirl>();
            list.add(mstGirl);
            tmpMap2.put(mstGirl.getType(), list);

        }
        girlMap = Collections.unmodifiableMap(tmpMap);
        girlTypeListMap = Collections.unmodifiableMap(tmpMap2);
    }
}
