package github.nighter.smartspawner.hooks.protections.api;

import net.william278.huskclaims.api.HuskClaimsAPI;
import net.william278.huskclaims.position.Position;
import net.william278.huskclaims.position.World;
import net.william278.huskclaims.trust.TrustLevel;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HuskClaims {

    /**
     * Converts a Bukkit Location to a HuskClaims Position.
     */
    private static Position toPosition(@NotNull Location location) {
        HuskClaimsAPI api = HuskClaimsAPI.getInstance();
        String worldName = location.getWorld() != null ? location.getWorld().getName() : "world";
        World world = api.getWorld(worldName);
        return api.getPosition(location.getX(), location.getY(), location.getZ(), world);
    }

    /**
     * Checks if a player has at least BUILD trust (or higher) in the claim at the given location.
     * Returns true (allow) if:
     * - There is no claim at the location (wilderness)
     * - The player is the claim owner
     * - The player has a trust level with the BUILD privilege
     */
    private static boolean hasPrivilege(@NotNull Player player, @NotNull Location location,
                                        @NotNull TrustLevel.Privilege privilege) {
        try {
            HuskClaimsAPI api = HuskClaimsAPI.getInstance();
            Position position = toPosition(location);

            // No claim at position — allow
            if (!api.isClaimAt(position)) {
                return true;
            }

            // Check if player is owner
            boolean isOwner = api.getClaimOwnerAt(position)
                    .map(owner -> owner.getUuid().equals(player.getUniqueId()))
                    .orElse(false);
            if (isOwner) return true;

            // Check privilege
            return api.getTrustLevelAt(position, api.getOnlineUser(player.getUniqueId()))
                    .map(level -> level.getPrivileges().contains(privilege))
                    .orElse(false);

        } catch (HuskClaimsAPI.NotRegisteredException | IllegalArgumentException e) {
            // HuskClaims not ready or world not claimable — allow
            return true;
        }
    }

    /**
     * Check if a player can open/interact with the spawner menu (requires CONTAINER trust or higher).
     */
    public static boolean canPlayerOpenMenu(@NotNull Player player, @NotNull Location location) {
        return hasPrivilege(player, location, TrustLevel.Privilege.CONTAINER_OPEN);
    }

    /**
     * Check if a player can break the spawner block (requires BUILD trust or higher).
     */
    public static boolean canPlayerBreakBlock(@NotNull Player player, @NotNull Location location) {
        return hasPrivilege(player, location, TrustLevel.Privilege.BLOCK_BREAK);
    }

    /**
     * Check if a player can stack spawners (requires BUILD trust or higher).
     */
    public static boolean canPlayerStackBlock(@NotNull Player player, @NotNull Location location) {
        return hasPrivilege(player, location, TrustLevel.Privilege.BLOCK_PLACE);
    }
}
