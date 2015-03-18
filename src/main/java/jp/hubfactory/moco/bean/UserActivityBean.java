package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserActivityBean implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Integer activityId;

    private String runDate;

    private String distance;

    private String time;

    private String avgTime;
}
