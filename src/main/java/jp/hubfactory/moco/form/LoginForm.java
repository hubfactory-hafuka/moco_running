package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class LoginForm {
    @NotNull(message="userIdは必須です")
    private Long userId;
    @NotNull(message="uuIdは必須です")
    private String uuId;
}
