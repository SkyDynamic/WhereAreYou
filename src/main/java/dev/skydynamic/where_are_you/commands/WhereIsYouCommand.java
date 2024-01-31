package dev.skydynamic.where_are_you.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

import static dev.skydynamic.where_are_you.Utils.buildText;
import static dev.skydynamic.where_are_you.Utils.giveEffect;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class WhereIsYouCommand {

    private static final LiteralArgumentBuilder<ServerCommandSource> whereIsYouCommand = literal("here")
        .executes(it -> hereExcute(it.getSource(), it.getSource().getPlayer()))
        .then(argument("player", EntityArgumentType.player())
            .executes(it -> hereExcute(it.getSource(), EntityArgumentType.getPlayer(it, "player")) // how? what r u doing? fabric and mojang?
            )
        );

    public static void registCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(whereIsYouCommand);
    }

    public static int hereExcute(ServerCommandSource commandSource, ServerPlayerEntity player) {
        giveEffect(player);
        final List<ServerPlayerEntity> serverPlayerList = commandSource.getServer().getPlayerManager().getPlayerList();
        for (ServerPlayerEntity serverPlayer : serverPlayerList) {
            serverPlayer.sendMessage(buildText(player));
        }
        return 0;
    }

}
