package jp.hubfactory.moco.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.entity.MstVoiceSet;
import jp.hubfactory.moco.repository.MstVoiceSetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstVoiceSetCache {

    @Autowired
    MstVoiceSetRepository repository;

    private List<MstVoiceSet> list;

    private Map<Integer, List<MstVoiceSet>> girlIdKeyListMap;

    public void load() {
        list = repository.findAll();
        this.sort();
        this.createData();
    }

    public List<MstVoiceSet> getAll() {
        return list;
    }

    public List<MstVoiceSet> getVoiceSetListByGirlId(Integer girlId) {
        if (girlIdKeyListMap == null) {
            this.load();
        }
        return girlIdKeyListMap.get(girlId);
    }

    private void sort() {
        Collections.sort(list, new Comparator<MstVoiceSet>() {
            @Override
            public int compare(MstVoiceSet o1, MstVoiceSet o2) {
                return o1.getKey().getSetId().compareTo(o2.getKey().getSetId());
            }
        });
    }

    private void createData() {
        Map<Integer, List<MstVoiceSet>> tmpMap = new HashMap<>();

        for (MstVoiceSet mst : list) {
            Integer key = mst.getKey().getGirlId();
            List<MstVoiceSet> list = (tmpMap.containsKey(key)) ? tmpMap.get(key) : new ArrayList<MstVoiceSet>();
            list.add(mst);
            tmpMap.put(key, list);
        }
        girlIdKeyListMap = Collections.unmodifiableMap(tmpMap);
    }
}
