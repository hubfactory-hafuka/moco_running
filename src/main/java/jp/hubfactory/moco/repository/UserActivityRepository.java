package jp.hubfactory.moco.repository;

import java.util.Date;
import java.util.List;

import jp.hubfactory.moco.entity.UserActivity;
import jp.hubfactory.moco.entity.UserActivityKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, UserActivityKey>, JpaSpecificationExecutor<UserActivityKey> {

    @Query(value = "SELECT * FROM user_activity_?1 WHERE user_id = ?2 ORDER BY activity_id DESC", nativeQuery = true)
    public List<UserActivity> findByKeyUserIdOrderByKeyActivityIdDesc(Integer suffix, Long userId);

    @Query(value = "select MAX(activity_id) from user_activity_?1 WHERE user_id = ?2", nativeQuery = true)
    public Integer findMaxActivityId(Integer suffix, Long userId);

    @Modifying
    @Query(value = "INSERT INTO user_activity_?1 VALUES (?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, sysdate(), sysdate())", nativeQuery = true)
    public void insert(Integer suffix, Long userId, Integer activityId, Integer girlId, Date runDate, Double distance, String time, String avgTime, String locations);
}
