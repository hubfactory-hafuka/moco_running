package jp.hubfactory.moco.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class UserActivityBean implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private Integer girlId;

    private Long userId;

    private Integer activityId;

    private String runDate;

    private String distance;

    private String time;

    private String avgTime;

    private List<UserActivityLocationBean> locations;

    private List<UserActivityDetailBean> details;
}
