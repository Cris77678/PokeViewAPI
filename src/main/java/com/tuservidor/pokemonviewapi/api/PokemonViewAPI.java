package com.tuservidor.pokemonviewapi.api;

import com.tuservidor.pokemonviewapi.network.PokemonViewData;
import com.tuservidor.pokemonviewapi.screen.PokemonViewScreen;
import net.minecraft.client.MinecraftClient;

/**
 * Public API — client-side entry point for other mods.
 *
 * Any mod that has PokemonViewAPI as a dependency can call:
 * <pre>{@code
 *   // Client-side only — call from a client packet handler
 *   PokemonViewAPI.openScreen(viewData);
 * }</pre>
 *
 * The data object is constructed from the packet buf you receive on
 * the CLIENT when the server sends PacketHelper.sendOpenViewer(...).
 */
public final class PokemonViewAPI {

    private PokemonViewAPI() {}

    /**
     * Open the 3D Pokemon viewer screen on the client.
     * Must be called from the client thread (e.g. inside a packet handler).
     *
     * @param data the decoded packet data
     */
    public static void openScreen(PokemonViewData data) {
        MinecraftClient.getInstance().execute(() ->
            MinecraftClient.getInstance().setScreen(new PokemonViewScreen(data)));
    }
}
