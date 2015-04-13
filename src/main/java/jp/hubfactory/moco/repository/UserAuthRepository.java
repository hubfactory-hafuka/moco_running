package jp.hubfactory.moco.repository;

import jp.hubfactory.moco.entity.UserAuth;
import jp.hubfactory.moco.entity.UserAuthKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, UserAuthKey> {
}
