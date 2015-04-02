package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PurchaseVoiceForm {
    @NotNull(message="userIdは必須です")
    private Long userId;
    @NotNull(message="setIdは必須です")
    private Integer setId;
    @NotNull(message="receiptは必須です")
    private String receipt;
}
