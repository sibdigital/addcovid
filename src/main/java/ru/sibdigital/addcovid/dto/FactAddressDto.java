package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.DocAddressFact;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FactAddressDto {
    private String addressFact;
    private Long personOfficeFactCnt;
    private String status;

    public DocAddressFact convertToDocAddressFact(){
        return DocAddressFact.builder()
                .addressFact(this.addressFact)
                .personOfficeFactCnt(this.personOfficeFactCnt)
                .build();
    }
}
