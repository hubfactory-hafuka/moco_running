package jp.hubfactory.moco.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.repository.MstVoiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstVoiceCache {

    @Autowired
    MstVoiceRepository repository;

    private Map<Integer, List<MstVoice>> voiceMap;

    private List<MstVoice> list;

    public void load() {
        list = repository.findAll();
        this.sort();
        this.createData();
    }

    public List<MstVoice> getAll() {
        return list;
    }

    public List<MstVoice> getVoiceList(Integer girlId) {
        if (voiceMap == null) {
            this.load();
        }
        return voiceMap.get(girlId);
    }

    private void sort() {
        Collections.sort(list, new Comparator<MstVoice>() {
            @Override
            public int compare(MstVoice o1, MstVoice o2) {
                return o1.getKey().getVoiceId().compareTo(o2.getKey().getVoiceId());
            }
        });
    }

    private void createData() {
        Map<Integer, List<MstVoice>> tmpMap = new HashMap<>();

        for (MstVoice mstVoice : list) {
            List<MstVoice> list = (tmpMap.containsKey(mstVoice.getKey().getGirlId())) ? tmpMap.get(mstVoice.getKey().getGirlId()) : new ArrayList<MstVoice>();
            list.add(mstVoice);
            tmpMap.put(mstVoice.getKey().getGirlId(), list);
        }
        voiceMap = Collections.unmodifiableMap(tmpMap);
    }
}
