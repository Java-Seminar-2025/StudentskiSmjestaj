package com.smjestaj.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
public class OptionsData {
    private BigDecimal lowerPrice;
    private BigDecimal upperPrice;
    private String city;
    private Integer numberOfStudents;
    private Integer numberOfRooms;
}
