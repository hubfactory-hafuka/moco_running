package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.MstVoice;
import jp.hubfactory.moco.entity.MstVoiceKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MstVoiceRepository extends JpaRepository<MstVoice, MstVoiceKey>, JpaSpecificationExecutor<MstVoice> {
    public List<MstVoice> findByKeyGirlId(Integer girlId);
}
