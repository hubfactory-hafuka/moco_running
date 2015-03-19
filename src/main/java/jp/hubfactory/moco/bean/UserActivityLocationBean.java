package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserActivityLocationBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 経度 */
    private String latitude;
    /** 緯度 */
    private String longitude;
}
