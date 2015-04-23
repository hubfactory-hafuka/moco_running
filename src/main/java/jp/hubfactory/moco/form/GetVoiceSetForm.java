package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class GetVoiceSetForm extends BaseForm {
    @NotNull(message="girlIdは必須です")
    private Integer girlId;
}
