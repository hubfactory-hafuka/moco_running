package jp.hubfactory.moco.form;

import lombok.Data;

@Data
public class RegistUserActivityDetailForm {

    /** 距離 */
    private Integer distance;
    /** 経過時間 */
    private String timeElapsed;
    /** ラップタイム */
    private String lapTime;
    /** 増減時間 */
    private String incDecTime;
}
