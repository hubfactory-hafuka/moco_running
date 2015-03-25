package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class GetUserForm {

    @NotNull(message="userIdは必須です")
    private Long userId;
}
