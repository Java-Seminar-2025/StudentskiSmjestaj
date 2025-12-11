package com.smjestaj.dto;

import lombok.*;

@Builder
public record BlockOrUnblockDto (
    String username,
    Boolean blocked
) {}
