package jp.hubfactory.moco.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UpdateProfileForm extends BaseForm {
    /** 体重 */
    private String weight;
    /** 身長 */
    private String height;
    /** プロフ画像(BASE64エンコード文字列) */
    private String imageData;
    /** ポイント*/
    private Long point;
}
