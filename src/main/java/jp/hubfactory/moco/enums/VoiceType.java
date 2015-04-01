package jp.hubfactory.moco.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum VoiceType {

    /** 通常ボイス */
    NORMAL(1),
    /** ミッションクリアボイス */
    MISSION_CLEAR(2),
    /** 課金ボイス */
    PURCHASE(3);

    @Getter
    private Integer key;


    public static VoiceType valueOf(Integer voiceType) {
        for (VoiceType type : VoiceType.values()) {
            if (type.getKey().equals(voiceType)) {
                return type;
            }
        }
        throw new IllegalStateException("voiceType error. voiceType=" + voiceType);
    }

}
