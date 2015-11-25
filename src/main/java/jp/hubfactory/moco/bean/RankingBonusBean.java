package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingBonusBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String rankingDate;

    private Long rank;

    private Long point;
}
