package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.UserActivity;
import jp.hubfactory.moco.entity.UserActivityKey;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, UserActivityKey>, JpaSpecificationExecutor<UserActivityKey> {

    public List<UserActivity> findByKeyUserIdOrderByKeyActivityIdDesc(Long userId);

    public List<UserActivity> findByKeyUserIdOrderByKeyActivityIdDesc(Long userId, Pageable pageable);

    @Query("select max(ua.key.activityId) from UserActivity ua where ua.key.userId = ?1")
    public Integer findMaxActivityId(Long userId);
}
