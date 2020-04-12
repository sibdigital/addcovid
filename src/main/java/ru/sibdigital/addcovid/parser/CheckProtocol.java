package ru.sibdigital.addcovid.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.dto.PostFormDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class CheckProtocol {
    private PostFormDto postFormDto;
    private List<Integer> personsEmptyRowsInExcel;
    private List<Integer> addressesEmptyRowsInExcel;
    private List<Map<String,String>> checkedDeparts;
    private String globalMessage = "OK";
    private boolean success = true;
    private Map<String, Map<String, Integer>> statistic = new HashMap<>(3);

    public CheckProtocol(PostFormDto postFormDto) {
        this.postFormDto = postFormDto;
    }
}
