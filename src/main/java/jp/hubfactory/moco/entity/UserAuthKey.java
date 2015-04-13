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
public class UserAuthKey implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    @Column
    private String loginId;
    @Column
    private String password;
    @Column
    private String serviceId;
}
