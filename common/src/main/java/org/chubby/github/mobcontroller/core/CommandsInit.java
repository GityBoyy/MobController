package org.chubby.github.mobcontroller.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;
import org.chubby.github.mobcontroller.util.UtilityMethods;


public class CommandsInit {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("setHomePos")
                .requires(source -> source.hasPermission(1))
                .then(Commands.argument("monster", EntityArgument.entity())
                        .executes(CommandsInit::executeSetHomePos)));

        dispatcher.register(Commands.literal("setHomePosHere")
                .requires(source->source.hasPermission(1))
                .executes(CommandsInit::executeSetHomePosHere));

        dispatcher.register(Commands.literal("setHomePosAt")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("x", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg())
                        .then(Commands.argument("y", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg())
                                .then(Commands.argument("z", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg())
                                        .executes(CommandsInit::executeSetHomePosAt)))));
    }

    private static int executeSetHomePosHere(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        if (!(source.getEntity() instanceof ServerPlayer)) {
            source.sendFailure(Component.literal("This command can only be executed by a player!"));
            return 0;
        }

        ServerPlayer player = (ServerPlayer) source.getEntity();
        Vec3 homePosition = player.position();

        UtilityMethods.setHomePos(homePosition);

        source.sendSuccess(() -> Component.literal(
                        String.format("Set home position to %.2f, %.2f, %.2f at your current location",
                                homePosition.x, homePosition.y, homePosition.z)),
                true);

        return 1;
    }

    private static int executeSetHomePos(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = EntityArgument.getEntity(context, "monster");

        if (!(entity instanceof Monster)) {
            source.sendFailure(Component.literal("Selected entity is not a monster!"));
            return 0;
        }

        Monster monster = (Monster) entity;
        Vec3 homePosition = monster.position();

        UtilityMethods.setHomePos(homePosition);

        source.sendSuccess(() -> Component.literal(
                        String.format("Set home position to %.2f, %.2f, %.2f for monster: %s",
                                homePosition.x, homePosition.y, homePosition.z, monster.getDisplayName().getString())),
                true);

        return 1;
    }

    private static int executeSetHomePosAt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        double x = com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(context, "x");
        double y = com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(context, "y");
        double z = com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(context, "z");

        Vec3 homePosition = new Vec3(x, y, z);

        UtilityMethods.setHomePos(homePosition);

        source.sendSuccess(() -> Component.literal(
                        String.format("Set home position to %.2f, %.2f, %.2f", x, y, z)),
                true);

        return 1;
    }
}
