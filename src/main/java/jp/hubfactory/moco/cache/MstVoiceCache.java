package jp.hubfactory.moco.cache;

import java.util.List;

import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.repository.MstVoiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Scope("singleton")
@Component
public class MstVoiceCache {

    @Autowired
    MstVoiceRepository repository;

    private List<MstVoice> list;

    public void load() {
        list = repository.findAll();
    }

    public List<MstVoice> getAll() {
        return list;
    }
}
