package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.MstGirlMission;
import jp.hubfactory.moco.entity.MstGirlMissionKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MstGirlMissionRepository extends JpaRepository<MstGirlMission, MstGirlMissionKey>, JpaSpecificationExecutor<MstGirlMission> {
    public List<MstGirlMission> findByKeyGirlIdOrderByDistanceAsc(Integer girlId);
}
