package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.MstConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MstConfigRepository extends JpaRepository<MstConfig, String> {
}
