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
public class UserGirlVoice implements Serializable {

    @EmbeddedId
    private UserGirlVoiceKey userGirlVoiceKey;
    @Column
    private Integer status;
    @Column
    private Date updDatetime = new Date();
    @Column
    private Date insDatetime= new Date();
}
