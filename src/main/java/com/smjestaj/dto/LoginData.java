package com.smjestaj.dto;

import lombok.*;

@Builder
public record LoginData (
    String username,
    String password
) {}
