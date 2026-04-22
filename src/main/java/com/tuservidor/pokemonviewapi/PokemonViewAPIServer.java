package com.tuservidor.pokemonviewapi;

import com.tuservidor.pokemonviewapi.network.PokemonViewData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common (server+client) entrypoint.
 * Registers the CustomPayload type so both sides know how to encode/decode it.
 * Must run on BOTH sides before any packet is sent.
 */
public class PokemonViewAPIServer implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("PokemonViewAPI");

    @Override
    public void onInitialize() {
        // Register payload type for Server→Client play channel
        PayloadTypeRegistry.playS2C().register(PokemonViewData.ID, PokemonViewData.CODEC);
        LOGGER.info("PokemonViewAPI payload registered.");
    }
}
