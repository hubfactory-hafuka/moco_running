package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.UserGirl;
import jp.hubfactory.moco.entity.UserGirlKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGirlRepository extends JpaRepository<UserGirl, UserGirlKey>, JpaSpecificationExecutor<UserGirl> {
}
