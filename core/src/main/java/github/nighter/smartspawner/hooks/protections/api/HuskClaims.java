package github.nighter.smartspawner.hooks.protections.api;

import net.william278.huskclaims.api.HuskClaimsAPI;
import net.william278.huskclaims.claim.Claim;
import net.william278.huskclaims.claim.ClaimWorld;
import net.william278.huskclaims.position.Position;
import net.william278.huskclaims.position.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HuskClaims {

    private static Position toPosition(@NotNull Location location) {
        HuskClaimsAPI api = HuskClaimsAPI.getInstance();
        String worldName = location.getWorld() != null ? location.getWorld().getName() : "world";
        World world = api.getWorld(worldName);
        return api.getPosition(
                location.getBlockX() + 0.5,
                location.getBlockY() + 0.5,
                location.getBlockZ() + 0.5,
                world
        );
    }

    /**
     * Returns true if the player is allowed to interact with the claim at the location.
     * Allows if: no claim present, player is owner, or player has any trust level.
     */
    private static boolean isTrusted(@NotNull Player player, @NotNull Location location) {
        try {
            HuskClaimsAPI api = HuskClaimsAPI.getInstance();
            Position position = toPosition(location);

            Optional<ClaimWorld> claimWorldOpt = api.getClaimWorldAt(position);
            if (claimWorldOpt.isEmpty()) return true;

            ClaimWorld claimWorld = claimWorldOpt.get();
            Optional<Claim> claimOpt = claimWorld.getClaimAt(position);
            if (claimOpt.isEmpty()) return true;

            Claim claim = claimOpt.get();

            // Admin claim — allow everyone
            if (claim.getOwner().isEmpty()) return true;

            // Player is claim owner
            if (claim.getOwner().get().equals(player.getUniqueId())) return true;

            // Player has any trust level in this claim
            net.william278.huskclaims.user.OnlineUser onlineUser =
                    api.getOnlineUser(player.getUniqueId());
            return api.getTrustLevel(claim, claimWorld, onlineUser).isPresent();

        } catch (Exception e) {
            // Fallback: allow if HuskClaims API is unavailable or throws any error
            return true;
        }
    }

    public static boolean canPlayerOpenMenu(@NotNull Player player, @NotNull Location location) {
        return isTrusted(player, location);
    }

    public static boolean canPlayerBreakBlock(@NotNull Player player, @NotNull Location location) {
        return isTrusted(player, location);
    }

    public static boolean canPlayerStackBlock(@NotNull Player player, @NotNull Location location) {
        return isTrusted(player, location);
    }
}
