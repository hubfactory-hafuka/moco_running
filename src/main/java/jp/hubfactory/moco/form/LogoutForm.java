package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class LogoutForm {

    @NotNull(message="userIdは必須です")
    private Long userId;
    @NotNull(message="tokenは必須です")
    private String token;
}
