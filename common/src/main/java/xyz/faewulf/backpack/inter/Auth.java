package xyz.faewulf.backpack.inter;

import net.minecraft.client.Minecraft;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record Auth(boolean success, String token, UUID account, Instant expires) {
    // Assume that authentication tokens have already expired if they're due to expire within 30 seconds to account
    // for potential clock drift and network latency
    static final Duration AUTH_INVALIDATION_ADJUSTMENT = Duration.ofSeconds(30);

    public boolean isExpired() {
        return expires.minus(AUTH_INVALIDATION_ADJUSTMENT).isBefore(Instant.now());
    }

    public boolean isInvalidForClientPlayer() {
        var client = Minecraft.getInstance();
        return client.player == null || !account.equals(client.player.getUUID());
    }
}
