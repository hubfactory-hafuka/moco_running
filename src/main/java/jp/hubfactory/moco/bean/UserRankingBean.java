package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserRankingBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String name;
    private Double distance;
    private Long rank;
}
