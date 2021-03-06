package jp.hubfactory.moco.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.entity.MstVoiceSetDetail;
import jp.hubfactory.moco.repository.MstVoiceSetDetailRepository;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstVoiceSetDetailCache {

    /** ソートカラム：ログインボーナスタイプ */
    public static final String SET_ID = "key.setId";
    /** ソートカラム：チャンス日 */
    public static final String GIRL_ID = "key.girlId";
    /** 閾値 */
    public static final String VOICE_ID = "key.voiceId";


    @Autowired
    MstVoiceSetDetailRepository repository;

    private List<MstVoiceSetDetail> list;

    private Map<Integer, List<MstVoiceSetDetail>> setIdKeyListMap;

    private Map<String, List<MstVoiceSetDetail>> setIdGirlIdKeyListMap;

    private Map<Integer, List<MstVoiceSetDetail>> girlIdKeyListMap;

    public void load() {
        list = repository.findAll();
        this.sort();
        this.createData();
    }

    public List<MstVoiceSetDetail> getAll() {
        return list;
    }

    public List<MstVoiceSetDetail> getVoiceSetDetail(Integer setId) {
        if (setIdKeyListMap == null) {
            this.load();
        }
        return setIdKeyListMap.get(setId);
    }

    public List<MstVoiceSetDetail> getVoiceSetDetailList(Integer setId, Integer girlId) {
        if (setIdGirlIdKeyListMap == null) {
            this.load();
        }
        String key = setId + "-" + girlId;
        return setIdGirlIdKeyListMap.get(key);
    }

    public List<MstVoiceSetDetail> getVoiceSetDetailByGirlId(Integer girlId) {
        if (girlIdKeyListMap == null) {
            this.load();
        }
        return girlIdKeyListMap.get(girlId);
    }

    @SuppressWarnings("unchecked")
    private void sort() {
        // ソート
        ComparatorChain comparator = new ComparatorChain();
        // ログインボーナスタイプ＞チャンス日＞閾値の昇順
        comparator.addComparator(new BeanComparator(SET_ID));
        comparator.addComparator(new BeanComparator(GIRL_ID));
        comparator.addComparator(new BeanComparator(VOICE_ID));
        // ソート処理
        Collections.sort(list, comparator);
    }

    private void createData() {
        Map<Integer, List<MstVoiceSetDetail>> tmpMap = new HashMap<>();
        Map<String, List<MstVoiceSetDetail>> tmpMap2 = new HashMap<>();
        Map<Integer, List<MstVoiceSetDetail>> tmpMap3 = new HashMap<>();

        for (MstVoiceSetDetail mst : list) {
            Integer key = mst.getKey().getSetId();
            List<MstVoiceSetDetail> list = (tmpMap.containsKey(key)) ? tmpMap.get(key) : new ArrayList<MstVoiceSetDetail>();
            list.add(mst);
            tmpMap.put(key, list);

            String keySetIdGirlId = mst.getKey().getSetId() + "-" + mst.getKey().getGirlId();
            List<MstVoiceSetDetail> list2 = (tmpMap2.containsKey(keySetIdGirlId)) ? tmpMap2.get(keySetIdGirlId) : new ArrayList<MstVoiceSetDetail>();
            list2.add(mst);
            tmpMap2.put(keySetIdGirlId, list2);

            Integer keyGirlId = mst.getKey().getGirlId();
            List<MstVoiceSetDetail> list3 = (tmpMap3.containsKey(keyGirlId)) ? tmpMap3.get(keyGirlId) : new ArrayList<MstVoiceSetDetail>();
            list3.add(mst);
            tmpMap3.put(keyGirlId, list3);
        }
        setIdKeyListMap = Collections.unmodifiableMap(tmpMap);
        setIdGirlIdKeyListMap = Collections.unmodifiableMap(tmpMap2);
        girlIdKeyListMap = Collections.unmodifiableMap(tmpMap3);
    }
}
