package com.tuservidor.pokemonviewapi;

import com.tuservidor.pokemonviewapi.api.PokemonViewAPI;
import com.tuservidor.pokemonviewapi.network.PokemonViewData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Client entrypoint — registers the packet handler.
 */
@Environment(EnvType.CLIENT)
public class PokemonViewAPIClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Fabric 1.21.1: registerGlobalReceiver(CustomPayload.Id<T>, PlayPayloadHandler<T>)
        // The CODEC was already registered server-side via PayloadTypeRegistry
        ClientPlayNetworking.registerGlobalReceiver(
            PokemonViewData.ID,
            (payload, context) -> PokemonViewAPI.openScreen(payload)
        );

        PokemonViewAPIServer.LOGGER.info("PokemonViewAPI client receiver registered.");
    }
}
