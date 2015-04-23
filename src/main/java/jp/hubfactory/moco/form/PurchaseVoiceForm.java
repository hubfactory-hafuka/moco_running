package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PurchaseVoiceForm extends BaseForm{
    @NotNull(message="setIdは必須です")
    private Integer setId;
    @NotNull(message="receiptは必須です")
    private String receipt;
}
