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
            List<Predicate> conditionList = new ArrayList<>();

            if (optionsData.lowerPrice() != null) {
                conditionList.add(cb.greaterThanOrEqualTo(root.get("price"), optionsData.lowerPrice()));
            }

            if (optionsData.upperPrice() != null) {
                conditionList.add(cb.lessThanOrEqualTo(root.get("price"), optionsData.upperPrice()));
            }

            if (optionsData.city() != null && !optionsData.city().isEmpty()) {
                conditionList.add(cb.equal(cb.lower(root.get("city")), optionsData.city().toLowerCase()));
            }

            if (optionsData.numberOfStudents() != null) {
                conditionList.add(cb.equal(root.get("numberOfStudents"), optionsData.numberOfStudents()));
            }

            if (optionsData.numberOfRooms() != null) {
                conditionList.add(cb.equal(root.get("numberOfRooms"), optionsData.numberOfRooms()));
            }

            conditionList.add(cb.equal(root.get("deleted"), false));

            return cb.and(conditionList.toArray(new Predicate[0]));
        };
    }
}

