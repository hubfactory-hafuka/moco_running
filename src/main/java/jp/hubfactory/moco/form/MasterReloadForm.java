package jp.hubfactory.moco.form;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class MasterReloadForm {

    @NotNull(message="paramは必須です")
    private String param;
}
