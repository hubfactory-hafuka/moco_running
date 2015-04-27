package jp.hubfactory.moco.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class InformationBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    private String title;
    private String text;
}
