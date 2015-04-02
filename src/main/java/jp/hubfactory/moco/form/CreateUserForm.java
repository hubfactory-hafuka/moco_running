package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class CreateUserForm {

    @NotNull(message="emailは必須です")
    private String email;
    @NotNull(message="passwordは必須です")
    private String password;
    @NotNull(message="uuIdは必須です")
    private String uuId;
}
