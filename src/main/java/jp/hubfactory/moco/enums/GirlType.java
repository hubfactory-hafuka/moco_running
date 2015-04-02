package jp.hubfactory.moco.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GirlType {

    /** ノーマル */
    NORMAL(1),
    /** 課金 */
    PURCHASE(2);

    @Getter
    private Integer key;
}
