package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.entity.UserGirlVoiceKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGirlVoiceRepository extends JpaRepository<UserGirlVoice, UserGirlVoiceKey>, JpaSpecificationExecutor<UserGirlVoice> {

    /**
     * ユーザーID,ガールIDをキーにユーザーの音声情報を取得する
     * @param userId ユーザーID
     * @param girlId ガールID
     * @return ユーザーガール音声リスト
     */
    public List<UserGirlVoice> findByUserGirlVoiceKeyUserIdAndUserGirlVoiceKeyGirlId(Long userId, Integer girlId);
}
