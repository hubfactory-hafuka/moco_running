package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.UserPurchaseHistory;
import jp.hubfactory.moco.entity.UserPurchaseHistoryKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPurchaseHistoryRepository extends JpaRepository<UserPurchaseHistory, UserPurchaseHistoryKey> {

    @Query("select count(uph.key.itemId) from UserPurchaseHistory uph where uph.key.userId = ?1 and uph.key.type = ?2 and uph.key.itemId = ?3")
    public Integer selectCountByKey(Long userId, Integer type, Integer itemId);

    @Query(value = "SELECT COUNT(*) FROM user_purchase_history WHERE type = ?1", nativeQuery = true)
    public Integer selectCountByType(Integer type);
}
