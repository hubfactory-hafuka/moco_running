package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class CreateUserForm {
    @NotNull(message="uuIdは必須です")
    private String uuId;
    @NotNull(message="userNameは必須です")
    private String userName;
}
