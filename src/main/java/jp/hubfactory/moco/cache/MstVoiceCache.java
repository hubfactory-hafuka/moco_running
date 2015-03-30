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

    private Map<Integer, List<MstVoice>> girlIdKeyListMap;

    private Map<String, MstVoice> girlIdAndvoiceIdKeyMap;

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
        if (girlIdKeyListMap == null) {
            this.load();
        }
        return girlIdKeyListMap.get(girlId);
    }

    public MstVoice getMstVoice(Integer girlId, Integer voiceId) {
        if (girlIdAndvoiceIdKeyMap == null) {
            this.load();
        }
        String key = girlId + "-" + voiceId;
        return girlIdAndvoiceIdKeyMap.get(key);
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
        Map<String, MstVoice> tmpMap2 = new HashMap<>();

        for (MstVoice mstVoice : list) {
            List<MstVoice> list = (tmpMap.containsKey(mstVoice.getKey().getGirlId())) ? tmpMap.get(mstVoice.getKey().getGirlId()) : new ArrayList<MstVoice>();
            list.add(mstVoice);
            tmpMap.put(mstVoice.getKey().getGirlId(), list);

            String key = mstVoice.getKey().getGirlId() + "-" + mstVoice.getKey().getVoiceId();
            tmpMap2.put(key, mstVoice);
        }
        girlIdKeyListMap = Collections.unmodifiableMap(tmpMap);
        girlIdAndvoiceIdKeyMap = Collections.unmodifiableMap(tmpMap2);
    }
}
