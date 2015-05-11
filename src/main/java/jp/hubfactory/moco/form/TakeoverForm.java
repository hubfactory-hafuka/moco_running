package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class TakeoverForm {
    @NotNull(message="userIdは必須です")
    private Long userId;
    @NotNull(message="uuIdは必須です")
    private String uuId;
    @NotNull(message="takeoverCodeは必須です")
    private String takeoverCode;
}
