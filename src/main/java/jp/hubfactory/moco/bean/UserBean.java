package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String name;
    private String totalDistance;
    private Integer totalCount;
    private String totalAvgTime;
    private Integer girlId;
    private String girlDistance;
    private String remainDistance;
    private InformationBean infoBean;
}
