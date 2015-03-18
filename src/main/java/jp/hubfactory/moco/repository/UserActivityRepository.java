package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.UserActivity;
import jp.hubfactory.moco.entity.UserActivityKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, UserActivityKey>, JpaSpecificationExecutor<UserActivityKey> {

    public List<UserActivity> findByUserActivityKeyUserIdOrderByUserActivityKeyActivityIdDesc(Long userId);

    @Query("select max(ua.userActivityKey.activityId) from UserActivity ua where ua.userActivityKey.userId = ?1")
    public Integer selectMaxActivityId(Long userId);
}
