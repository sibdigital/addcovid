package ru.sibdigital.addcovid.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.ClsPrescription;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ClsOrganizationSpecification implements Specification<ClsOrganization> {

    private ClsOrganizationSearchCriteria searchCriteria;

    public void setSearchCriteria(ClsOrganizationSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<ClsOrganization> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchCriteria.getInn() != null) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.trim(root.get("inn")), '%' + searchCriteria.getInn() + '%'),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), '%' + searchCriteria.getInn() + '%')));
        }

        if (searchCriteria.getIdPrescription() != null) {
            Join<ClsOrganization, ClsPrescription> prescription = root.join("regOrganizationPrescriptions").join("prescription");
            predicates.add(criteriaBuilder.equal(prescription.get("id"), searchCriteria.getIdPrescription()));
        }

        if (searchCriteria.getTypeOrganizations() != null) {
            predicates.add(criteriaBuilder.in(root.get("idTypeOrganization")).value(searchCriteria.getTypeOrganizations()));
        }

        if (searchCriteria.getEmail() != null) {
            predicates.add(criteriaBuilder.equal(root.get("email"), searchCriteria.getEmail()));
        }

        if (searchCriteria.getIsActivated() != null) {
            predicates.add(criteriaBuilder.equal(root.get("isActivated"), searchCriteria.getIsActivated()));
        }

        if (searchCriteria.getEgrulId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("regOrganizationClassifier").get("regEgrul").get("id"), searchCriteria.getEgrulId()));
        }

        if (searchCriteria.getEgripId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("regOrganizationClassifier").get("regEgrip").get("id"), searchCriteria.getEgripId()));
        }

        if (searchCriteria.getFilialId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("regOrganizationClassifier").get("regFilial").get("id"), searchCriteria.getFilialId()));
        }

        predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
