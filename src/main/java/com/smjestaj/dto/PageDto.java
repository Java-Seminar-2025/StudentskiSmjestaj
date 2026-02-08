package com.smjestaj.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record PageDto (
    Integer page,
    Integer size,
    Integer totalPages,
    String returnPage
) {}
