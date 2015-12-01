package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.UserActivityDetail;
import jp.hubfactory.moco.entity.UserActivityDetailKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityDetailRepository extends JpaRepository<UserActivityDetail, UserActivityDetailKey>, JpaSpecificationExecutor<UserActivityDetail> {

//    @Query(value = "SELECT * FROM user_activity_detail_?1 WHERE user_id = ?2 AND activity_id = ?3 ORDER BY activity_id ASC", nativeQuery = true)
//    public List<UserActivityDetail> findByKeyUserIdAndKeyActivityIdOrderByKeyDetailIdAsc(Integer suffix, Long userId, Integer activityId);

    @Query(value = "SELECT * FROM user_activity_detail_?1 WHERE user_id = ?2 ORDER BY activity_id ASC", nativeQuery = true)
    public List<UserActivityDetail> findByKeyUserIdOrderByKeyActivityIdAsc(Integer suffix, Long userId);
    
    @Query(value = "SELECT * FROM user_activity_detail_?1 WHERE user_id = ?2 AND activity_id BETWEEN ?3 AND ?4 ORDER BY activity_id ASC", nativeQuery = true)
    public List<UserActivityDetail> findActivityDetailListBetweenActivityId(Integer suffix, Long userId, Integer fromActId, Integer toActId);

    @Modifying
    @Query(value = "INSERT INTO user_activity_detail_?1 VALUES (?2, ?3, ?4, ?5, ?6, ?7, ?8, sysdate(), sysdate())", nativeQuery = true)
    public void insert(Integer suffix, Long userId, Integer activityId, Integer detailId, Integer distance, String timeElapsed, String lapTime, String incDecTime);
}
