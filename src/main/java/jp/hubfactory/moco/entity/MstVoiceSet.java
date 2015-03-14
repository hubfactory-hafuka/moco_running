package jp.hubfactory.moco.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MstVoiceSet implements Serializable {

    @EmbeddedId
    private MstVoiceSetKey mstVoiceSetKey;
    @Column
    private Integer setId;
    @Column
    private Date updDatetime;
    @Column
    private Date insDatetime;

}
