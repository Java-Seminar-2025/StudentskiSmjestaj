package com.smjestaj.enums;

import lombok.*;

@RequiredArgsConstructor
@Getter
public enum UserRole {
    STUDENT("student") {
        @Override
        public ReservationStatus getCorrectReservationStatus() {
            return ReservationStatus.ACTIVE;
        }
    },
    LANDLORD("landlord") {
        @Override
        public ReservationStatus getCorrectReservationStatus() {
            return ReservationStatus.FIRST_ACTIVE;
        }
    },
    ADMIN("admin") {
        @Override
        public ReservationStatus getCorrectReservationStatus() {
            return ReservationStatus.ACTIVE;
        }
    };

    private final String displayName;
    public abstract ReservationStatus getCorrectReservationStatus();
}
