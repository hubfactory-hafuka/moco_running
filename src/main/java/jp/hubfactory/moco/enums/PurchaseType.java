package jp.hubfactory.moco.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PurchaseType {

    /** ボイス */
    VOICE(1),
    /** ガール */
    GIRL(2);

    @Getter
    private Integer key;
}
