package com.tuservidor.pokemonviewapi.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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
        // [FIX CRÍTICO]: Verificar si el jugador TIENE el mod instalado en su cliente.
        // Si no lo hacemos, el servidor expulsará al jugador por paquete desconocido.
        if (!ServerPlayNetworking.canSend(player, PokemonViewData.ID)) {
            player.sendMessage(Text.literal("§c¡Necesitas instalar el mod PokeViewAPI en tu cliente para ver esto en 3D!"), false);
            return;
        }

        PokemonViewData payload = new PokemonViewData(
            uuid, speciesId, level, shiny, nature, ability, scale, gender,
            moves, ivHp, ivAtk, ivDef, ivSpA, ivSpD, ivSpe, heldItem, aspects
        );
        
        ServerPlayNetworking.send(player, payload);
    }
}
