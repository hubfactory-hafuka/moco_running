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
public class MstVoiceSetKey implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    @Column
    private Integer setId;
    @Column
    private Integer girlId;
}
