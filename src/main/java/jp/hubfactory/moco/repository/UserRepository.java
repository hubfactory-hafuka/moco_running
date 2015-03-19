package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select max(u.userId) from User u")
    public Long findMaxUserId();

}
