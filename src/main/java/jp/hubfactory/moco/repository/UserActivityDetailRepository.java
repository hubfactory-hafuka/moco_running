package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.UserActivityDetail;
import jp.hubfactory.moco.entity.UserActivityDetailKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityDetailRepository extends JpaRepository<UserActivityDetail, UserActivityDetailKey>, JpaSpecificationExecutor<UserActivityDetail> {

    public List<UserActivityDetail> findByKeyUserIdAndKeyActivityIdOrderByKeyDetailIdAsc(Long userId, Integer activityId);

    public List<UserActivityDetail> findByKeyUserIdOrderByKeyActivityIdAsc(Long userId);
}
