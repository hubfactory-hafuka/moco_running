package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class LoginForm {
    @NotNull(message="loginIdは必須です")
    private String loginId;
    @NotNull(message="passwordは必須です")
    private String password;
    @NotNull(message="uuIdは必須です")
    private String uuId;
    @NotNull(message="serviceIdは必須です")
    private String serviceId;
    @NotNull(message="userNameは必須です")
    private String userName;
}
