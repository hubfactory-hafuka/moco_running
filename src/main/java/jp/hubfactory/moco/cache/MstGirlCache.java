package jp.hubfactory.moco.cache;

import java.util.List;

import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.repository.MstGirlRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstGirlCache {

    @Autowired
    MstGirlRepository repository;

    private List<MstGirl> list;

    public void load() {
        list = repository.findAll();
    }

    public List<MstGirl> getAll() {
        return list;
    }
}
