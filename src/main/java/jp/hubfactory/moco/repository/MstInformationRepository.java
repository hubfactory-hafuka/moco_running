package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.MstInformation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MstInformationRepository extends JpaRepository<MstInformation, Integer> {
}
