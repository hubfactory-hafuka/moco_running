package jp.hubfactory.moco.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.entity.MstGirlMission;
import jp.hubfactory.moco.repository.MstGirlMissionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


//@Scope("singleton")
@Component
public class MstGirlMissionCache {

    @Autowired
    MstGirlMissionRepository repository;

    private List<MstGirlMission> list;

    private Map<Integer, List<MstGirlMission>> girlIdKeyListMap;

    public void load() {
        list = repository.findAll();
        this.sort();
        this.createData();
    }

    public List<MstGirlMission> getAll() {
        return list;
    }

    public List<MstGirlMission> getGirlMissions(Integer girlId) {
        if (girlIdKeyListMap == null) {
            this.load();
        }
        return girlIdKeyListMap.get(girlId);
    }

    private void sort() {
        Collections.sort(list, new Comparator<MstGirlMission>() {
            @Override
            public int compare(MstGirlMission o1, MstGirlMission o2) {
                return o1.getDistance().compareTo(o2.getDistance());
            }
        });
    }

    private void createData() {
        Map<Integer, List<MstGirlMission>> tmpMap = new HashMap<>();

        for (MstGirlMission mstGirlMission : list) {
            List<MstGirlMission> list = (tmpMap.containsKey(mstGirlMission.getKey().getGirlId())) ? tmpMap.get(mstGirlMission.getKey().getGirlId()) : new ArrayList<MstGirlMission>();
            list.add(mstGirlMission);
            tmpMap.put(mstGirlMission.getKey().getGirlId(), list);

        }
        girlIdKeyListMap = Collections.unmodifiableMap(tmpMap);
    }
}
