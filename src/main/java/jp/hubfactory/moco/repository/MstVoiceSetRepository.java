package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.MstVoiceSet;
import jp.hubfactory.moco.entity.MstVoiceSetKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MstVoiceSetRepository extends JpaRepository<MstVoiceSet, MstVoiceSetKey> {
    public List<MstVoiceSet> findBySetId(Integer setId);
}
