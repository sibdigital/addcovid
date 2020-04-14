package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.dto.DachaDto;
import ru.sibdigital.addcovid.model.DocDacha;
import ru.sibdigital.addcovid.model.DocDachaPerson;
import ru.sibdigital.addcovid.repository.DocDachaPersonRepo;
import ru.sibdigital.addcovid.repository.DocDachaRepo;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DachaService {

    @Autowired
    private DocDachaRepo docDachaRepo;

    @Autowired
    DocDachaPersonRepo docDachaPersonRepo;

    public DocDacha addNewRequest(DachaDto dachaDto) {

        DocDacha docDacha = null;

        List<DocDachaPerson> docDachaPersons = new ArrayList<>();
        if (dachaDto.getPersonList() != null) {
            docDachaPersons = dachaDto.getPersonList()
                    .stream()
                    .map(docDachaDto -> docDachaDto.convertToDocDachaPerson())
                    //.map(docDachaDto -> docDachaDto.setId(null))
                    .collect(Collectors.toList());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        docDacha = DocDacha.builder()
                .isAgree(dachaDto.getIsAgree())
                .isProtect(dachaDto.getIsProtect())
                .validDate(LocalDate.parse(dachaDto.getValidDate(), formatter))
                .link(dachaDto.getLink())
                .district(dachaDto.getDistrict())
                .address(dachaDto.getAddress())
                .docDachaPersons(docDachaPersons)
                .statusReview(0)
                .timeReview(Timestamp.valueOf(LocalDateTime.now()))
                .statusImport(0)
                .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                .email(dachaDto.getEmail())
                .phone(dachaDto.getPhone())
                .raion(dachaDto.getRaion())
                .naspunkt(dachaDto.getNaspunkt())
                .build();

        docDacha = docDachaRepo.save(docDacha);

        DocDacha finalDocDacha = docDacha;

        docDacha.getDocDachaPersons().forEach(item -> {
            item.setDocDachaByIdDocDacha(finalDocDacha);
        });


        docDachaPersonRepo.saveAll(docDacha.getDocDachaPersons());

        return docDacha;
    }

}
