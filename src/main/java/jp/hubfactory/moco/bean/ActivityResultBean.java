package jp.hubfactory.moco.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ActivityResultBean implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String girlName;

    private boolean newRecordFlg;

    private List<MissionClearVoiceBean> missionClearVoiceBeans;

}
