package jp.hubfactory.moco.repository;

import java.util.List;

import jp.hubfactory.moco.entity.MstGirl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MstGirlRepository extends JpaRepository<MstGirl, Integer> {
    public List<MstGirl> findByType(Integer type);
}
