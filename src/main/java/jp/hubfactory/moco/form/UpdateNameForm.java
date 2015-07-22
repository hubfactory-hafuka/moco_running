package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UpdateNameForm extends BaseForm {
    @NotNull(message="nameは必須です")
    private String name;
}
