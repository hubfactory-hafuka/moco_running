package jp.hubfactory.moco.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class VoiceSetBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private Integer setId;
    private Integer girlId;
    private Integer price;
    private Long point;
    private boolean holdFlg;
    private List<VoiceSetDetailBean> setDetailList;
}
