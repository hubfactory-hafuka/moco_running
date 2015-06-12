package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.MstRanking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MstRankingRepository extends JpaRepository<MstRanking, Integer> {
}
