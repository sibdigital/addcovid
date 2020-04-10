package ru.sibdigital.addcovid.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.dto.PostFormDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class CheckProtocol {
    private PostFormDto postFormDto;
    private List<Integer> personsEmptyRowsInExcel;
    private List<Integer> addressesEmptyRowsInExcel;
    private String globalMessage;
    private boolean success = true;

    public CheckProtocol(PostFormDto postFormDto) {
        this.postFormDto = postFormDto;
    }
}
