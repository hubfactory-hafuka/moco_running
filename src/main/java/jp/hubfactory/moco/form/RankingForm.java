package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RankingForm extends BaseForm {
    /** ランキング種別 */
    @NotNull(message="typeは必須です")
    private Integer type;
    /** ガールID */
    private Integer girlId;
}
