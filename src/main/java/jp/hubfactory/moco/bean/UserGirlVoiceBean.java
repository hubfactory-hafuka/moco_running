package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserGirlVoiceBean implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String voiceFileId;
    private Integer girlId;
    private Integer voiceId;
    private String word;
    private Integer situation;
    private Integer type;
    private Integer status;
    private String openCondition;
}
