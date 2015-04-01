package jp.hubfactory.moco.cache;

import java.util.ArrayList;
import java.util.Collections;
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

    private Map<Integer, List<MstVoiceSet>> setIdKeyListMap;

    public void load() {
        list = repository.findAll();
        this.createData();
    }

    public List<MstVoiceSet> getAll() {
        return list;
    }

    public List<MstVoiceSet> getVoiceSet(Integer setId) {
        if (setIdKeyListMap == null) {
            this.load();
        }
        return setIdKeyListMap.get(setId);
    }

    private void createData() {
        Map<Integer, List<MstVoiceSet>> tmpMap = new HashMap<>();
        for (MstVoiceSet mst : list) {
            Integer key = mst.getSetId();
            List<MstVoiceSet> list = (tmpMap.containsKey(key)) ? tmpMap.get(key) : new ArrayList<MstVoiceSet>();
            list.add(mst);
            tmpMap.put(key, list);
        }
        setIdKeyListMap = Collections.unmodifiableMap(tmpMap);
    }
}
