package jp.hubfactory.moco.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivity implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UserActivityKey key;

    @Column
    private Integer girlId;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date runDate;

    @Column
    private Double distance;

    @Column
    private String time;

    @Column
    private String avgTime;

    @Column
    private String locations;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date updDatetime;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date insDatetime;
}
