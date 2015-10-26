package jp.hubfactory.moco.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * アクティビティ結果情報
 */
@Data
public class ActivityResultBean implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** ガール名 */
    private String girlName;
    /** 記録更新フラグ */
    private boolean newRecordFlg;
    /** ミッションクリアボイスリスト */
    private List<MissionClearVoiceBean> missionClearVoiceBeans;

}
