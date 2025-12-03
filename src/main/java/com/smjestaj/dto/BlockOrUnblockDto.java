package com.smjestaj.dto;

import lombok.*;

@Getter
@Setter
public class BlockOrUnblockDto {
    private String username;
    private Boolean blocked;
}
