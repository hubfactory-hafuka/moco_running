package jp.hubfactory.moco.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hubfactory.moco.entity.MstConfig;
import jp.hubfactory.moco.repository.MstConfigRepository;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstConfigCache {

    /** point有効設定 */
    private static final String POINT_ENABLE = "POINT_ENABLE";

    @Autowired
    MstConfigRepository repository;

    private List<MstConfig> list;

    private Map<String, MstConfig> map;

    public void load() {
        list = repository.findAll();
        this.createData();
    }

    /**
     * 有効な設定情報取得
     *
     * @param name
     * @return
     */
    public MstConfig getMstConfig(String name) {
        if (map == null) {
            this.load();
        }
        MstConfig config = map.get(name);
        if (config != null && MocoDateUtils.isWithin(config.getStartDatetime(), config.getEndDatetime())) {
            return config;
        }
        return null;
    }

    private void createData() {
        Map<String, MstConfig> tmpMap = new HashMap<>();

        for (MstConfig data : list) {
            tmpMap.put(data.getName(), data);
        }
        map = Collections.unmodifiableMap(tmpMap);
    }


    public boolean isPointEnable() {
        return this.getMstConfig(POINT_ENABLE) != null ? true : false;
    }
}
