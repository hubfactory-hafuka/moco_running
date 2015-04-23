package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BaseForm {
    /** ユーザーID */
    @NotNull(message="userIdは必須です")
    private Long userId;
    /** トークン */
    @NotNull(message="tokenは必須です")
    private String token;
}
