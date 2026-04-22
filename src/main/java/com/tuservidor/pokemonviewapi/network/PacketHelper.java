package com.tuservidor.pokemonviewapi.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Server-side helper — zero client imports.
 * Call this from any server-only mod to open the 3D viewer on the player's client.
 */
public final class PacketHelper {

    private PacketHelper() {}

    public static void sendOpenViewer(
            ServerPlayerEntity player,
            UUID         uuid,
            Identifier   speciesId,
            int          level,
            boolean      shiny,
            String       nature,
            String       ability,
            float        scale,
            String       gender,
            List<String> moves,
            int ivHp, int ivAtk, int ivDef, int ivSpA, int ivSpD, int ivSpe,
            String       heldItem,
            Set<String>  aspects
    ) {
        PokemonViewData payload = new PokemonViewData(
            uuid, speciesId, level, shiny, nature, ability, scale, gender,
            moves, ivHp, ivAtk, ivDef, ivSpA, ivSpD, ivSpe, heldItem, aspects
        );
        // Fabric 1.21.1: send(ServerPlayerEntity, CustomPayload)
        ServerPlayNetworking.send(player, payload);
    }
}
