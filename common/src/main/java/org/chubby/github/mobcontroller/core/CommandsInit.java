package org.chubby.github.mobcontroller.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import org.chubby.github.mobcontroller.common.data.MobHomePos;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.util.UtilityMethods;

public class CommandsInit {

    // Singleton or dependency-injected MobHomePos manager
    private static MobHomePos mobHomeManager = new MobHomePos();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Main command group
        dispatcher.register(Commands.literal("mobcontroller")
                .requires(source -> source.hasPermission(2))

                // Set home position for specific monster at its current location
                .then(Commands.literal("sethome")
                        .then(Commands.argument("monster", EntityArgument.entity())
                                .executes(CommandsInit::executeSetHomeAtMonster)))

                // Set home position at player's location for nearest controlled monster
                .then(Commands.literal("sethomehere")
                        .then(Commands.argument("monster", EntityArgument.entity())
                                .executes(CommandsInit::executeSetHomeHere)))

                // Set home position at specific coordinates for a monster
                .then(Commands.literal("sethomeat")
                        .then(Commands.argument("monster", EntityArgument.entity())
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(CommandsInit::executeSetHomeAt))))

                // Get home position of a monster
                .then(Commands.literal("gethome")
                        .then(Commands.argument("monster", EntityArgument.entity())
                                .executes(CommandsInit::executeGetHome)))

                // Remove home position from a monster
                .then(Commands.literal("clearhome")
                        .then(Commands.argument("monster", EntityArgument.entity())
                                .executes(CommandsInit::executeClearHome)))

                // List all monsters with home positions
                .then(Commands.literal("listhomes")
                        .executes(CommandsInit::executeListHomes))

                // Teleport monster to its home position
                .then(Commands.literal("sendtohome")
                        .then(Commands.argument("monster", EntityArgument.entity())
                                .executes(CommandsInit::executeSendToHome)))
        );
    }

    /**
     * Sets home position at the monster's current location
     */
    private static int executeSetHomeAtMonster(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = EntityArgument.getEntity(context, "monster");

        if (!(entity instanceof Monster monster)) {
            source.sendFailure(Component.literal("§cSelected entity is not a monster!"));
            return 0;
        }

        if (!ItemController.getPlayerMobControlMap().containsValue(monster.getId())) {
            source.sendFailure(Component.literal("§cThis monster is not controlled!"));
            return 0;
        }

        BlockPos homePosition = monster.blockPosition();
        mobHomeManager.addMobPos(monster, homePosition);

        source.sendSuccess(() -> Component.literal(
                        String.format("§aSet home position for %s to %d, %d, %d",
                                monster.getDisplayName().getString(),
                                homePosition.getX(),
                                homePosition.getY(),
                                homePosition.getZ())),
                true);

        return 1;
    }

    /**
     * Sets home position at the command executor's location
     */
    private static int executeSetHomeHere(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = EntityArgument.getEntity(context, "monster");

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§cThis command can only be executed by a player!"));
            return 0;
        }

        if (!(entity instanceof Monster monster)) {
            source.sendFailure(Component.literal("§cSelected entity is not a monster!"));
            return 0;
        }

        if (!ItemController.getPlayerMobControlMap().containsValue(monster.getId())) {
            source.sendFailure(Component.literal("§cThis monster is not controlled!"));
            return 0;
        }

        BlockPos homePosition = player.blockPosition();
        mobHomeManager.addMobPos(monster, homePosition);

        source.sendSuccess(() -> Component.literal(
                        String.format("§aSet home position for %s to your location: %d, %d, %d",
                                monster.getDisplayName().getString(),
                                homePosition.getX(),
                                homePosition.getY(),
                                homePosition.getZ())),
                true);

        return 1;
    }

    /**
     * Sets home position at specific coordinates
     */
    private static int executeSetHomeAt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = EntityArgument.getEntity(context, "monster");
        BlockPos position = BlockPosArgument.getBlockPos(context, "pos");

        if (!(entity instanceof Monster monster)) {
            source.sendFailure(Component.literal("§cSelected entity is not a monster!"));
            return 0;
        }

        if (!ItemController.getPlayerMobControlMap().containsValue(monster.getId())) {
            source.sendFailure(Component.literal("§cThis monster is not controlled!"));
            return 0;
        }

        mobHomeManager.addMobPos(monster, position);

        source.sendSuccess(() -> Component.literal(
                        String.format("§aSet home position for %s to %d, %d, %d",
                                monster.getDisplayName().getString(),
                                position.getX(),
                                position.getY(),
                                position.getZ())),
                true);

        return 1;
    }

    /**
     * Gets the home position of a monster
     */
    private static int executeGetHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = EntityArgument.getEntity(context, "monster");

        if (!(entity instanceof Monster monster)) {
            source.sendFailure(Component.literal("§cSelected entity is not a monster!"));
            return 0;
        }

        return mobHomeManager.getMobPos(monster)
                .map(pos -> {
                    source.sendSuccess(() -> Component.literal(
                                    String.format("§e%s's home position: %d, %d, %d",
                                            monster.getDisplayName().getString(),
                                            pos.getX(),
                                            pos.getY(),
                                            pos.getZ())),
                            false);
                    return 1;
                })
                .orElseGet(() -> {
                    source.sendFailure(Component.literal(
                            String.format("§c%s does not have a home position set!",
                                    monster.getDisplayName().getString())));
                    return 0;
                });
    }

    /**
     * Clears the home position of a monster
     */
    private static int executeClearHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = EntityArgument.getEntity(context, "monster");

        if (!(entity instanceof Monster monster)) {
            source.sendFailure(Component.literal("§cSelected entity is not a monster!"));
            return 0;
        }

        BlockPos removed = mobHomeManager.removeMobPos(monster);
        if (removed != null) {
            source.sendSuccess(() -> Component.literal(
                            String.format("§aCleared home position for %s (was at %d, %d, %d)",
                                    monster.getDisplayName().getString(),
                                    removed.getX(),
                                    removed.getY(),
                                    removed.getZ())),
                    true);
            return 1;
        } else {
            source.sendFailure(Component.literal(
                    String.format("§c%s did not have a home position set!",
                            monster.getDisplayName().getString())));
            return 0;
        }
    }

    /**
     * Lists all monsters with home positions
     */
    private static int executeListHomes(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        var homes = mobHomeManager.getAllPositions();

        if (homes.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§eNo monsters have home positions set."), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal(
                        String.format("§a=== Monsters with Home Positions (%d) ===", homes.size())),
                false);

        homes.forEach((uuid, pos) -> {
            source.getLevel().getEntitiesOfClass(Monster.class,new AABB(pos).inflate(10)).forEach(entity -> {
                if (entity.getUUID().equals(uuid) && entity instanceof Monster monster) {
                    source.sendSuccess(() -> Component.literal(
                                    String.format("§e- %s: §f%d, %d, %d",
                                            monster.getDisplayName().getString(),
                                            pos.getX(),
                                            pos.getY(),
                                            pos.getZ())),
                            false);
                }
            });
        });

        return 1;
    }

    /**
     * Teleports a monster to its home position
     */
    private static int executeSendToHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = EntityArgument.getEntity(context, "monster");

        if (!(entity instanceof Monster monster)) {
            source.sendFailure(Component.literal("§cSelected entity is not a monster!"));
            return 0;
        }

        return mobHomeManager.getMobPos(monster)
                .map(pos -> {
                    // Teleport to center of block + 0.5 to avoid suffocation
                    monster.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                    source.sendSuccess(() -> Component.literal(
                                    String.format("§aTeleported %s to home position: %d, %d, %d",
                                            monster.getDisplayName().getString(),
                                            pos.getX(),
                                            pos.getY(),
                                            pos.getZ())),
                            true);
                    return 1;
                })
                .orElseGet(() -> {
                    source.sendFailure(Component.literal(
                            String.format("§c%s does not have a home position set!",
                                    monster.getDisplayName().getString())));
                    return 0;
                });
    }

    /**
     * Register legacy commands for backwards compatibility (optional)
     */
    private static void registerLegacyCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setHomePos")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("monster", EntityArgument.entity())
                        .executes(CommandsInit::executeSetHomeAtMonster)));

        dispatcher.register(Commands.literal("setHomePosHere")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("monster", EntityArgument.entity())
                        .executes(CommandsInit::executeSetHomeHere)));

        dispatcher.register(Commands.literal("setHomePosAt")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("monster", EntityArgument.entity())
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(CommandsInit::executeSetHomeAt))));
    }

    /**
     * Sets the MobHomePos manager (for dependency injection or testing)
     */
    public static void setMobHomeManager(MobHomePos manager) {
        mobHomeManager = manager;
    }

    /**
     * Gets the current MobHomePos manager
     */
    public static MobHomePos getMobHomeManager() {
        return mobHomeManager;
    }
}