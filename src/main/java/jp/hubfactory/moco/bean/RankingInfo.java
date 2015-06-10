package jp.hubfactory.moco.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class RankingInfo implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private UserRankingBean userRank;
    private List<UserRankingBean> rankList;
}
