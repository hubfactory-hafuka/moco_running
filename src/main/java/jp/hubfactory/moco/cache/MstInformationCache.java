package jp.hubfactory.moco.cache;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import jp.hubfactory.moco.entity.MstInformation;
import jp.hubfactory.moco.repository.MstInformationRepository;
import jp.hubfactory.moco.util.MocoDateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstInformationCache {

    @Autowired
    MstInformationRepository repository;

    private List<MstInformation> list;

    public void load() {
        list = repository.findAll();
        this.sort();
    }

    public List<MstInformation> getAll() {
        return list;
    }

    public MstInformation getActiveInformation() {
        if (CollectionUtils.isEmpty(list)) {
            load();
        }

        Date nowDate = MocoDateUtils.getNowDate();
        for (MstInformation mstInformation : list) {
            if (MocoDateUtils.isWithin(mstInformation.getStartDatetime(), mstInformation.getEndDatetime(), nowDate)) {
                return mstInformation;
            }
        }
        return null;
    }

    private void sort() {
        Collections.sort(list, new Comparator<MstInformation>() {
            @Override
            public int compare(MstInformation o1, MstInformation o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
    }
}
