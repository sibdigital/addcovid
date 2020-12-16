package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.RegNewsFile;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsNewsDto {

    private Long id;
    private String heading;
    private String message;
    private String startTime;
    private String endTime;
    private String hashId;

    private List<RegNewsFile> regNewsFiles;


}