package com.tuservidor.pokemonviewapi.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * CustomPayload for Fabric 1.21.1.
 * Must be registered via PayloadTypeRegistry.playS2C() before use.
 */
public record PokemonViewData(
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
) implements CustomPayload {

    public static final CustomPayload.Id<PokemonViewData> ID =
        new CustomPayload.Id<>(new Identifier("pokemonviewapi", "open_pokemon_view"));

    public static final PacketCodec<RegistryByteBuf, PokemonViewData> CODEC =
        PacketCodec.of(
            (value, buf) -> value.write(buf),
            PokemonViewData::read
        );

    public static PokemonViewData read(RegistryByteBuf buf) {
        UUID       uuid      = buf.readUuid();
        Identifier speciesId = buf.readIdentifier();
        int        level     = buf.readInt();
        boolean    shiny     = buf.readBoolean();
        String     nature    = buf.readString();
        String     ability   = buf.readString();
        float      scale     = buf.readFloat();
        String     gender    = buf.readString();

        int moveCount = buf.readInt();
        // [FIX]: Límite de seguridad para evitar crasheos por falta de memoria RAM (OOM)
        if (moveCount < 0 || moveCount > 4) moveCount = 0; 
        List<String> moves = new ArrayList<>(moveCount);
        for (int i = 0; i < moveCount; i++) moves.add(buf.readString());

        int ivHp  = buf.readInt();
        int ivAtk = buf.readInt();
        int ivDef = buf.readInt();
        int ivSpA = buf.readInt();
        int ivSpD = buf.readInt();
        int ivSpe = buf.readInt();

        String heldItem = buf.readString();

        int aspectCount = buf.readInt();
        // [FIX]: Límite de seguridad para aspectos
        if (aspectCount < 0 || aspectCount > 20) aspectCount = 0;
        Set<String> aspects = new HashSet<>(aspectCount);
        for (int i = 0; i < aspectCount; i++) aspects.add(buf.readString());

        return new PokemonViewData(uuid, speciesId, level, shiny, nature, ability, scale,
            gender, moves, ivHp, ivAtk, ivDef, ivSpA, ivSpD, ivSpe, heldItem, aspects);
    }

    public void write(RegistryByteBuf buf) {
        // [FIX]: Prevención de NullPointerException antes de enviar al cliente
        buf.writeUuid(uuid != null ? uuid : UUID.randomUUID());
        buf.writeIdentifier(speciesId != null ? speciesId : Identifier.of("cobblemon", "substitute"));
        buf.writeInt(level);
        buf.writeBoolean(shiny);
        buf.writeString(nature != null ? nature : "N/A");
        buf.writeString(ability != null ? ability : "N/A");
        buf.writeFloat(scale);
        buf.writeString(gender != null ? gender : "NONE");

        if (moves == null) {
            buf.writeInt(0);
        } else {
            buf.writeInt(Math.min(moves.size(), 4));
            for (int i = 0; i < Math.min(moves.size(), 4); i++) {
                String m = moves.get(i);
                buf.writeString(m != null ? m : "");
            }
        }

        buf.writeInt(ivHp);  buf.writeInt(ivAtk); buf.writeInt(ivDef);
        buf.writeInt(ivSpA); buf.writeInt(ivSpD); buf.writeInt(ivSpe);

        buf.writeString(heldItem != null ? heldItem : "minecraft:air");

        if (aspects == null) {
            buf.writeInt(0);
        } else {
            buf.writeInt(Math.min(aspects.size(), 20));
            int count = 0;
            for (String a : aspects) {
                if (count++ >= 20) break;
                buf.writeString(a != null ? a : "");
            }
        }
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }

    public String genderSymbol() {
        if (gender == null) return "";
        return switch (gender.toUpperCase()) {
            case "MALE"   -> "♂";
            case "FEMALE" -> "♀";
            default       -> "";
        };
    }

    public String genderDisplay() {
        if (gender == null) return "Sin género";
        return switch (gender.toUpperCase()) {
            case "MALE"   -> "♂ Macho";
            case "FEMALE" -> "♀ Hembra";
            default       -> "Sin género";
        };
    }

    public String speciesDisplayName() {
        if (speciesId == null) return "Desconocido";
        String path = speciesId.getPath().replace("_", " ");
        if (path.isEmpty()) return path;
        
        // [FIX] Capitalización correcta para nombres compuestos (Ej. "Iron Valiant" en lugar de "Iron valiant")
        String[] words = path.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
