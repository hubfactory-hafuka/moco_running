package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.entity.UserGirlVoiceKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserGirlVoiceRepository extends JpaRepository<UserGirlVoice, UserGirlVoiceKey>, JpaSpecificationExecutor<UserGirlVoice> {

    /**
     * ユーザーID,ガールIDをキーにユーザーの音声情報を取得する
     * @param userId ユーザーID
     * @param girlId ガールID
     * @return ユーザーガール音声リスト
     */
    @Query(value = "SELECT * FROM user_girl_voice_?1 WHERE user_id = ?2 AND girl_id = ?3", nativeQuery = true)
    public List<UserGirlVoice> findByKeyUserIdAndKeyGirlId(Integer suffix, Long userId, Integer girlId);

    @Modifying
    @Query(value = "INSERT INTO user_girl_voice_?1 VALUES (?2, ?3, ?4, ?5, sysdate(), sysdate())", nativeQuery = true)
    public void insert(Integer suffix, Long userId, Integer girlId, Integer voiceId, Integer status);

    @Modifying
    @Query(value = "UPDATE user_girl_voice_?1 SET status = ?5, upd_datetime = sysdate() WHERE user_id = ?2 AND girl_id = ?3 AND voice_id = ?4", nativeQuery = true)
    public void updateStatus(Integer suffix, Long userId, Integer girlId, Integer voiceId, Integer status);
}
