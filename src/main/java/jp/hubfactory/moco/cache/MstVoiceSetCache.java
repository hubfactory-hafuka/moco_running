package jp.hubfactory.moco.cache;

import java.util.List;

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

    public void load() {
        list = repository.findAll();
    }

    public List<MstVoiceSet> getAll() {
        return list;
    }
}
