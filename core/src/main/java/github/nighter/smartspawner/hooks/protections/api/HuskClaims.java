package github.nighter.smartspawner.hooks.protections.api;

import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationType;
import net.william278.huskclaims.api.HuskClaimsAPI;
import net.william278.huskclaims.position.Position;
import net.william278.huskclaims.position.World;
import net.william278.huskclaims.user.OnlineUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HuskClaims {

    private static Position toPosition(@NotNull Location location) {
        HuskClaimsAPI api = HuskClaimsAPI.getInstance();
        String worldName = location.getWorld() != null ? location.getWorld().getName() : "world";
        World world = api.getWorld(worldName);
        return api.getPosition(location.getX(), location.getY(), location.getZ(), world);
    }

    private static boolean isOperationAllowed(@NotNull Player player, @NotNull Location location,
                                              @NotNull OperationType operationType) {
        try {
            HuskClaimsAPI api = HuskClaimsAPI.getInstance();
            Position position = toPosition(location);
            OnlineUser onlineUser = api.getOnlineUser(player.getUniqueId());
            return api.isOperationAllowed(onlineUser, operationType, position);
        } catch (HuskClaimsAPI.NotRegisteredException | IllegalArgumentException e) {
            return true;
        }
    }

    /**
     * Check if a player can open/interact with the spawner (requires CONTAINER_OPEN permission).
     */
    public static boolean canPlayerOpenMenu(@NotNull Player player, @NotNull Location location) {
        return isOperationAllowed(player, location, OperationType.CONTAINER_OPEN);
    }

    /**
     * Check if a player can break the spawner block (requires BLOCK_BREAK permission).
     */
    public static boolean canPlayerBreakBlock(@NotNull Player player, @NotNull Location location) {
        return isOperationAllowed(player, location, OperationType.BLOCK_BREAK);
    }

    /**
     * Check if a player can stack spawners (requires BLOCK_PLACE permission).
     */
    public static boolean canPlayerStackBlock(@NotNull Player player, @NotNull Location location) {
        return isOperationAllowed(player, location, OperationType.BLOCK_PLACE);
    }
}
