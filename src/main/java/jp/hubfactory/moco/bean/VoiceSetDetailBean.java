package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class VoiceSetDetailBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String situationName;
    private String word;
    private String voiceFileId;
}
