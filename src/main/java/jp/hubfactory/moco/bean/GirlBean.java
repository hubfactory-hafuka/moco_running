package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

/**
 * ガール情報
 */
@Data
public class GirlBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** ガールID */
    private Integer girlId;
    /** ガールタイプ */
    private Integer type;
    /** ガール名 */
    private String name;
    /** 年齢 */
    private Integer age;
    /** 身長 */
    private Integer height;
    /** 体重 */
    private Integer weight;
    /** プロフィール */
    private String profile;

    private String cv;
    private Integer price;
    private Long point;
    private boolean holdFlg;
    private boolean favoriteFlg;
    private String comment;
}
