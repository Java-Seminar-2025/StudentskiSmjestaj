package com.smjestaj.dto;

public record FavoriteData (
    Long listingId,
    Long studentId,
    Boolean saved
) {}
