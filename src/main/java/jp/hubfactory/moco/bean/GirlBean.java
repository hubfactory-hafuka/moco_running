package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class GirlBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private Integer girlId;
    private Integer type;
    private String name;
    private Integer age;
    private Integer height;
    private Integer weight;
    private String profile;
    private String cv;
    private Integer price;
    private boolean holdFlg;
    private boolean favoriteFlg;
    private String comment;
}
