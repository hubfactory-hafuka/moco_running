package jp.hubfactory.moco.form;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RegistUserActivityForm {

    @NotNull(message="ユーザーIDは必須です")
    private Long userId;

    private String runDate;

    private Double distance;

    private String time;

    private List<RegistUserActivityDetailForm> details;

    private List<RegistUserActivityLocationForm> locations;
}


