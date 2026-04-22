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

    // playS2C() uses RegistryByteBuf
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
        Set<String> aspects = new HashSet<>(aspectCount);
        for (int i = 0; i < aspectCount; i++) aspects.add(buf.readString());

        return new PokemonViewData(uuid, speciesId, level, shiny, nature, ability, scale,
            gender, moves, ivHp, ivAtk, ivDef, ivSpA, ivSpD, ivSpe, heldItem, aspects);
    }

    public void write(RegistryByteBuf buf) {
        buf.writeUuid(uuid);
        buf.writeIdentifier(speciesId);
        buf.writeInt(level);
        buf.writeBoolean(shiny);
        buf.writeString(nature);
        buf.writeString(ability);
        buf.writeFloat(scale);
        buf.writeString(gender);

        buf.writeInt(moves.size());
        for (String m : moves) buf.writeString(m != null ? m : "");

        buf.writeInt(ivHp);  buf.writeInt(ivAtk); buf.writeInt(ivDef);
        buf.writeInt(ivSpA); buf.writeInt(ivSpD); buf.writeInt(ivSpe);

        buf.writeString(heldItem != null ? heldItem : "minecraft:air");

        buf.writeInt(aspects.size());
        for (String a : aspects) buf.writeString(a);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }

    public String genderSymbol() {
        return switch (gender) {
            case "MALE"   -> "♂";
            case "FEMALE" -> "♀";
            default       -> "";
        };
    }

    public String genderDisplay() {
        return switch (gender) {
            case "MALE"   -> "♂ Macho";
            case "FEMALE" -> "♀ Hembra";
            default       -> "Sin género";
        };
    }

    public String speciesDisplayName() {
        String path = speciesId.getPath().replace("_", " ");
        if (path.isEmpty()) return path;
        return Character.toUpperCase(path.charAt(0)) + path.substring(1);
    }
}
