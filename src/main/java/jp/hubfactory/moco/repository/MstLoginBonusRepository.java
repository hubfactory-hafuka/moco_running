package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.MstLoginBonus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MstLoginBonusRepository extends JpaRepository<MstLoginBonus, Integer> {
}
