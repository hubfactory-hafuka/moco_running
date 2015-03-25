package jp.hubfactory.moco.service;

import java.util.Date;
import java.util.List;

import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.repository.MstGirlRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GirlService {

    @Autowired
    private MstGirlRepository repository;

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
        return repository.findAll(new Sort(Direction.ASC, "girlId"));
    }

    public MstGirl selectMstGirl(Integer girlId) {
        return repository.findOne(girlId);
    }
}
