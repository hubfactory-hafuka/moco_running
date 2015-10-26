package jp.hubfactory.moco.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ExchangeForm extends BaseForm{
    private Integer girlId;
    private Integer setId;
}
