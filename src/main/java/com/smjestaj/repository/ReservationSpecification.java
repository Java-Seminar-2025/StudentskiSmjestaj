package com.smjestaj.repository;

import com.smjestaj.dto.ReservationSpecifiers;
import com.smjestaj.entity.ReservationEntity;
import com.smjestaj.enums.ReservationStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ReservationSpecification {
    public static Specification<ReservationEntity> withFilters(ReservationSpecifiers specifiers) {
        return (root, query, cb) -> {
            List<Predicate> conditionList = new ArrayList<>();

            if (specifiers.listingId() != null) {
                conditionList.add(cb.equal(root.get("listing").get("id"), specifiers.listingId()));
            }

            if (specifiers.studentUsername() != null) {
                conditionList.add(cb.equal(root.get("student").get("username"), specifiers.studentUsername()));
            }

            if (specifiers.roomId() != null) {
                conditionList.add(cb.equal(root.get("room").get("id"), specifiers.roomId()));
            }

            if (specifiers.statusList() != null && !specifiers.statusList().isEmpty()) {
                conditionList.add(root.get("status").in(specifiers.statusList()));
            }
            else if (specifiers.status() != null) {
                conditionList.add(cb.equal(root.get("status"), specifiers.status()));
            }

            if (specifiers.excludeCancelled() != null && specifiers.excludeCancelled()) {
                conditionList.add(cb.notEqual(root.get("status"), ReservationStatus.CANCELLED));
            }

            if (specifiers.type() != null) {
                conditionList.add(cb.equal(root.get("type"), specifiers.type()));
            }

            return cb.and(conditionList.toArray(new Predicate[0]));
        };
    }
}
