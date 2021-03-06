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
public class UserActivityDetail implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UserActivityDetailKey key;
    @Column
    private Integer distance;
    @Column
    private String timeElapsed;
    @Column
    private String lapTime;
    @Column
    private String incDecTime;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updDatetime;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date insDatetime;
}
