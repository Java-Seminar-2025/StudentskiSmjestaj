package com.smjestaj.dto;

import com.smjestaj.enums.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class ListingData {
    private Long listingId;
    private String landlordUsername;
    private String title;
    private String description;
    private String address;
    private String city;
    private BigDecimal price;
    private Integer numberOfStudents;
    private Integer numberOfRooms;
    private UserGender preferredGender;
    private LocalDateTime cancellationDeadline;
}
