package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.UserGoal;
import jp.hubfactory.moco.entity.UserGoalKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, UserGoalKey>, JpaSpecificationExecutor<UserGoal> {
}
