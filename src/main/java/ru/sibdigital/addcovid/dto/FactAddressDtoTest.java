package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.DocAddressFact;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FactAddressDtoTest {

    private Long id;
    private boolean isDeleted = false;
    private Date timeCreate;
    private String fiasRegionObjectGuid;
    private String fiasRaionObjectGuid;
    private String fullAddress;
    private boolean isHand = false;
}
