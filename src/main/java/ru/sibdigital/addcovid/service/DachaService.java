package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.dto.DachaDto;
import ru.sibdigital.addcovid.model.DocDacha;
import ru.sibdigital.addcovid.model.DocDachaAddr;
import ru.sibdigital.addcovid.repository.DocDachaAddrRepo;
import ru.sibdigital.addcovid.repository.DocDachaRepo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DachaService {

    @Autowired
    private DocDachaRepo docDachaRepo;

    @Autowired
    DocDachaAddrRepo docDachaAddrRepo;

    public DocDacha addNewRequest(DachaDto dachaDto) {

        DocDacha docDacha = null;

        List<DocDachaAddr> docDachaAddr = dachaDto.getAddrList()
                .stream()
                .map(docDachaDto -> docDachaDto.convertToDocDachaAddr())
                .collect(Collectors.toList());

        docDacha = DocDacha.builder()
                .lastname(dachaDto.getLastname())
                .firstname(dachaDto.getFirstname())
                .patronymic(dachaDto.getPatronymic())
                .age(dachaDto.getAge())
                .isAgree(dachaDto.getIsAgree())
                .isProtect(dachaDto.getIsProtect())
                .docDachaAddrs(docDachaAddr)
                .statusReview(0)
                .timeReview(Timestamp.valueOf(LocalDateTime.now()))
                .statusImport(0)
                .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        docDacha = docDachaRepo.save(docDacha);

        DocDacha finalDocDacha = docDacha;

        docDacha.getDocDachaAddrs().forEach(item -> {
            item.setDocDachaByIdDocDacha(finalDocDacha);
        });


        docDachaAddrRepo.saveAll(docDacha.getDocDachaAddrs());

        return docDacha;
    }

}
