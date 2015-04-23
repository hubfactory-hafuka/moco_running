package jp.hubfactory.moco.form;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RegistUserActivityForm extends BaseForm {

    /** ガールＩＤ */
    @NotNull(message="ガールIDは必須です")
    private Integer girlId;

    /** ラン日時 */
    private String runDate;

    /** 距離 */
    private Double distance;

    /** 時間 */
    private String time;

    /** 目標距離 */
    private Integer goalDistance;

    /** 目標距離のタイム */
    private Integer goalTime;

    /** 詳細情報 */
    private List<RegistUserActivityDetailForm> details;

    /** 位置情報 */
    private List<RegistUserActivityLocationForm> locations;
}


