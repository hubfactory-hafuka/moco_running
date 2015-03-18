package jp.hubfactory.moco.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserVoiceStatus {

    OFF(0),
    ON(1);

    @Getter
    private Integer key;

}
