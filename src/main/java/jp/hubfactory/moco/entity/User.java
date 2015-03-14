package jp.hubfactory.moco.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @Id
    @Column
    private Long userId;
    @Column
    @NotNull
    private String email;
    @Column
    private String password;
    @Column
    private String name;
    @Column
    public Double totalDistance;
    @Column
    public Integer girlId;
    @Column
    public Date updDatetime;
    @Column
    public Date insDatetime;

}
