package jp.hubfactory.moco.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RankingForm extends BaseForm {
    
	private Integer type;
    
	private Integer girlId;
}
