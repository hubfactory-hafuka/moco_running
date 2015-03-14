package jp.hubfactory.moco.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGirlVoiceKey implements Serializable {
    @Column
    private Long userId;
    @Column
    private Integer girlId;
    @Column
    private Integer voiceId;
}
