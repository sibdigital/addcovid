package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.DocPerson;
import ru.sibdigital.addcovid.model.DocRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocPersonRepo extends JpaRepository<DocPerson, Long> {

    @Query(nativeQuery = true, value = "select count(*) from ( select distinct lastname, firstname, patronymic from doc_person) as s")
    public Long getTotalPeople();

    //public Optional<List<DocPerson>> findByDocRequest(DocRequest docRequest);

    @Query(nativeQuery = true, value = "select * from doc_person where id_request = :id_request")
    public Optional<List<DocPerson>> findByDocRequest(Long id_request);
}
