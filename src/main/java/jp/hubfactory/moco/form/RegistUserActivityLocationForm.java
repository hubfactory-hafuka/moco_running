package jp.hubfactory.moco.form;

import lombok.Data;

@Data
public class RegistUserActivityLocationForm {
    /** 経度 */
    private String latitude;
    /** 緯度 */
    private String longitude;
}
