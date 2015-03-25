package jp.hubfactory.moco.service;

import java.util.Date;
import java.util.List;

import jp.hubfactory.moco.cache.MstGirlCache;
import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.repository.MstGirlRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GirlService {

    @Autowired
    private MstGirlRepository repository;
    @Autowired
    private MstGirlCache mstGirlCache;

    public MstGirl insert(MstGirl mstGirl) {

        Date nowDae = new Date();

        MstGirl girl = this.selectMstGirl(mstGirl.getGirlId());
        if (girl == null) {
            mstGirl.setUpdDatetime(nowDae);
            mstGirl.setInsDatetime(nowDae);
        } else {
            girl.setUpdDatetime(nowDae);
        }
        return repository.save(mstGirl);
    }

    public List<MstGirl> selectMstGirls() {
        return mstGirlCache.getAll();
    }

    public MstGirl selectMstGirl(Integer girlId) {
        return mstGirlCache.getGirl(girlId);
    }
}
