package com.smjestaj.repository;

import com.smjestaj.dto.OptionsData;
import com.smjestaj.entity.ListingEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ListingSpecification {
    public static Specification<ListingEntity> withFilters(OptionsData optionsData) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (optionsData.getLowerPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), optionsData.getLowerPrice()));
            }

            if (optionsData.getUpperPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), optionsData.getUpperPrice()));
            }

            if (optionsData.getCity() != null && !optionsData.getCity().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), optionsData.getCity().toLowerCase()));
            }

            if (optionsData.getNumberOfStudents() != null) {
                predicates.add(cb.equal(root.get("numberOfStudents"), optionsData.getNumberOfStudents()));
            }

            if (optionsData.getNumberOfRooms() != null) {
                predicates.add(cb.equal(root.get("numberOfRooms"), optionsData.getNumberOfRooms()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

