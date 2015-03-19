package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserActivityDetailBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private Integer distance;

    private String timeElapsed;

    private String lapTime;

    private String incDecTime;
}
