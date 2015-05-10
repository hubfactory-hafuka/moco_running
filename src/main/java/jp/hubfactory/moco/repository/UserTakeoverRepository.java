package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.UserTakeover;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTakeoverRepository extends JpaRepository<UserTakeover, Long> {
}
