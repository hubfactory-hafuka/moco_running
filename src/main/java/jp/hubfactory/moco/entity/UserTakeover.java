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
public class UserTakeover implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    private Long userId;
    @Column
    private String takeoverCode;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date updDatetime;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date insDatetime;

}
