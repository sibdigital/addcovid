package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.DocDachaAddr;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DachaAddrDto {
    private String district;
    private String address;

    public DocDachaAddr convertToDocDachaAddr(){
        return DocDachaAddr.builder()
                .district(this.district)
                .address(this.address)
                .build();
    }
}
