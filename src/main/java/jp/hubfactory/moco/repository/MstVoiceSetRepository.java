package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.MstVoiceSet;
import jp.hubfactory.moco.entity.MstVoiceSetKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MstVoiceSetRepository extends JpaRepository<MstVoiceSet, MstVoiceSetKey> {
}
