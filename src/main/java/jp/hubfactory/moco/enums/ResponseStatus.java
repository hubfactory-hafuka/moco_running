package jp.hubfactory.moco.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResponseStatus {

    SUCCESS("success"),
    ERROR("error");

    @Getter
    private String key;
}
