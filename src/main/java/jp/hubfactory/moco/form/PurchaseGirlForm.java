package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PurchaseGirlForm {
    @NotNull(message="userIdは必須です")
    private Long userId;
    @NotNull(message="girlIdは必須です")
    private Integer girlId;
    @NotNull(message="receiptは必須です")
    private String receipt;
}
