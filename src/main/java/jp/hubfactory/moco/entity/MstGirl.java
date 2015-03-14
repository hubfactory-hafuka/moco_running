package jp.hubfactory.moco.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MstGirl implements Serializable {
    @Id
    private Integer girlId;
    @Column
    private String name;
    @Column
    private Date updDatetime;
    @Column
    private Date insDatetime;
}
