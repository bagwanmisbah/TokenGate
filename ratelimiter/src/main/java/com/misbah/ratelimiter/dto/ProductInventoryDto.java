
package com.misbah.ratelimiter.dto;

import java.math.BigDecimal;

public record ProductInventoryDto(
        Long id,
        BigDecimal price,
        String description,
        int liveViewers
) {
}