package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.UserExchangeHistory;
import jp.hubfactory.moco.entity.UserExchangeHistoryKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExchangeHistoryRepository extends JpaRepository<UserExchangeHistory, UserExchangeHistoryKey> {

    @Query(value = "SELECT COUNT(item_id) FROM user_exchange_history WHERE user_id = ?1 AND type = ?2 AND item_id = ?3", nativeQuery = true)
    public Integer selectCountByKey(Long userId, Integer type, Integer itemId);
}
