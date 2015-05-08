package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.UserGoal;
import jp.hubfactory.moco.entity.UserGoalKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, UserGoalKey>, JpaSpecificationExecutor<UserGoal> {

    @Query(value = "SELECT * FROM user_goal_?1 WHERE user_id = ?2 AND distance = ?3", nativeQuery = true)
    public UserGoal selectUserGoal(Integer suffix, Long userId, Integer distance);

    @Modifying
    @Query(value = "INSERT INTO user_goal_?1 VALUES (?2, ?3, ?4, sysdate(), sysdate())", nativeQuery = true)
    public void insert(Integer suffix, Long userId, Integer distance, Integer time);

    @Modifying
    @Query(value = "UPDATE user_goal_?1 SET time = ?4, upd_datetime = sysdate() WHERE user_id = ?2 AND distance = ?3", nativeQuery = true)
    public void updateTime(Integer suffix, Long userId, Integer distance, Integer time);
}
