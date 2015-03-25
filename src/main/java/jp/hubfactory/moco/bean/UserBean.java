package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String name;
    public String totalDistance;
    public Integer totalCount;
    public String totalAvgTime;
    public Integer girlId;
    public String girlDistance;
    public String remainDistance;
}
