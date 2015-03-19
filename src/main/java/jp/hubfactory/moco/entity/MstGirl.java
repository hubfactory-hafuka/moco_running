package jp.hubfactory.moco.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MstGirl implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    @Id
    private Integer girlId;
    @Column
    private Integer type;
    @Column
    private String name;
    @Column
    private Date birthday;
    @Column
    private Integer age;
    @Column
    private Integer height;
    @Column
    private Integer weight;
    @Column
    private Integer bust;
    @Column
    private Integer waist;
    @Column
    private Integer hip;
    @Column
    private String hobby;
    @Column
    private String profile;
    @Column
    private String image_id;
    @Column
    private String cv;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updDatetime;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date insDatetime;
}
