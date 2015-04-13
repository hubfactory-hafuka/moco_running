package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.MstVoiceSetDetail;
import jp.hubfactory.moco.entity.MstVoiceSetDetailKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MstVoiceSetDetailRepository extends JpaRepository<MstVoiceSetDetail, MstVoiceSetDetailKey> {
    public List<MstVoiceSetDetail> findByKeySetId(Integer setId);
}
