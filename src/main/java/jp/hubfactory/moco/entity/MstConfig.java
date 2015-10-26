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
public class MstConfig implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    @Id
    private String name;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDatetime;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updDatetime;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date insDatetime;
}
