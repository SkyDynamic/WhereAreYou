package dev.skydynamic.where_are_you;

import dev.skydynamic.where_are_you.commands.WhereIsYouCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhereAreYou implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("whereisyou");

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> WhereIsYouCommand.registCommand(dispatcher));
	}
}