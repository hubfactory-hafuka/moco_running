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
public class MstVoice implements Serializable  {

    @EmbeddedId
    private MstVoiceKey mstVoiceKey;
    @Column
    private String word;
    @Column
    private Integer situation;
    @Column
    private Integer type;
    @Column
    private Date updDatetime = new Date();
    @Column
    private Date insDatetime= new Date();
}
