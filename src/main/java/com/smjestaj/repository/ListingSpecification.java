package com.smjestaj.repository;

import com.smjestaj.dto.ListingFilters;
import com.smjestaj.entity.ListingEntity;
import com.smjestaj.enums.UserGender;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ListingSpecification {
    public static Specification<ListingEntity> withFilters(ListingFilters listingFilters, UserGender gender) {
        return (root, query, cb) -> {
            List<Predicate> conditionList = new ArrayList<>();

            if (listingFilters.lowerPrice() != null) {
                conditionList.add(cb.greaterThanOrEqualTo(root.get("price"), listingFilters.lowerPrice()));
            }

            if (listingFilters.upperPrice() != null) {
                conditionList.add(cb.lessThanOrEqualTo(root.get("price"), listingFilters.upperPrice()));
            }

            if (listingFilters.city() != null && !listingFilters.city().isEmpty()) {
                conditionList.add(cb.equal(cb.lower(root.get("city")), listingFilters.city().toLowerCase()));
            }

            if (listingFilters.numberOfStudents() != null) {
                conditionList.add(cb.equal(root.get("numberOfStudents"), listingFilters.numberOfStudents()));
            }

            if (listingFilters.numberOfRooms() != null) {
                conditionList.add(cb.equal(root.get("numberOfRooms"), listingFilters.numberOfRooms()));
            }

            Predicate sameGender = cb.equal(root.get("preferredGender"), gender);
            Predicate noPreference = cb.isNull(root.get("preferredGender"));
            conditionList.add(cb.or(sameGender, noPreference));

            conditionList.add(cb.equal(root.get("deleted"), false));

            return cb.and(conditionList.toArray(new Predicate[0]));
        };
    }
}

