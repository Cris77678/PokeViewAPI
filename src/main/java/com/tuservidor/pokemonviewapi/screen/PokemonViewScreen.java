package com.tuservidor.pokemonviewapi.screen;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.math.QuaternionUtilsKt;
import com.tuservidor.pokemonviewapi.network.PokemonViewData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PokemonViewScreen extends Screen {
    private static final int LEFT_W = 165, RIGHT_W = 165, PANEL_H = 205, GAP = 10;
    private static final int TOTAL_W = LEFT_W + RIGHT_W + GAP;
    private static final float RENDER_SCALE = 38.0f;

    private final FloatingState floatingState = new FloatingState();
    private final PokemonViewData data;

    public PokemonViewScreen(PokemonViewData data) {
        super(Text.literal("Visor 3D"));
        this.data = data;
    }

    @Override
    protected void init() {
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        int sx = (this.width - TOTAL_W) / 2;
        int sy = (this.height - PANEL_H) / 2;
        int rightX = sx + LEFT_W + GAP;

        drawPanel(ctx, sx, sy, LEFT_W, PANEL_H, 0xE0060612, 0xFFFFCC00);
        renderPokemon(ctx, sx + LEFT_W / 2, sy + PANEL_H - 35, mouseX, mouseY, delta);

        drawPanel(ctx, rightX, sy, RIGHT_W, PANEL_H, 0xE0060612, 0xFFFFCC00);
        ctx.drawCenteredTextWithShadow(textRenderer, "✦ " + data.speciesDisplayName() + " ✦", rightX + RIGHT_W / 2, sy + 10, 0xFFAA00);
        
        renderInfo(ctx, rightX + 15, sy + 35);
        renderStats(ctx, rightX + 15, sy + 110);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderPokemon(DrawContext ctx, int cx, int cy, int mx, int my, float delta) {
        Species species = PokemonSpecies.INSTANCE.getByIdentifier(data.speciesId());
        if (species == null) return;
        
        float headYaw = (float)Math.atan((cx - mx) / 40.0f) * 40.0f;
        float headPitch = (float)Math.atan(((cy - 40) - my) / 40.0f) * -20.0f;
        Quaternionf rot = QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13f, 35f, 0f));
        
        // [FIX] Recuperar el ItemStack desde el String enviado por el servidor
        Identifier itemId = Identifier.tryParse(data.heldItem() != null ? data.heldItem() : "minecraft:air");
        Item heldItem = itemId != null ? Registries.ITEM.getOrEmpty(itemId).orElse(Items.AIR) : Items.AIR;
        ItemStack heldStack = heldItem != Items.AIR ? new ItemStack(heldItem) : ItemStack.EMPTY;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(cx, cy, 50.0);
        ctx.getMatrices().scale(RENDER_SCALE, RENDER_SCALE, RENDER_SCALE);
        
        // Pasamos heldStack en lugar de ItemStack.EMPTY
        PokemonGuiUtilsKt.drawProfilePokemon(new RenderablePokemon(species, data.aspects(), heldStack),
                ctx.getMatrices(), rot, PoseType.PROFILE, floatingState, delta, 1.0f, false, true, 
                1f, 1f, 1f, 1f, headYaw, headPitch);
        ctx.getMatrices().pop();
    }

    private void renderInfo(DrawContext ctx, int x, int y) {
        ctx.drawTextWithShadow(textRenderer, "Nivel: " + data.level(), x, y, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "Sexo: " + data.genderDisplay(), x, y + 15, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "Nat: " + data.nature(), x, y + 30, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "Hab: " + data.ability(), x, y + 45, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "Shiny: " + (data.shiny() ? "Sí" : "No"), x, y + 60, data.shiny() ? 0xFFFF55 : 0xAAAAAA);
    }

    private void renderStats(DrawContext ctx, int x, int y) {
        ctx.drawTextWithShadow(textRenderer, "IVs del Pokémon:", x, y, 0xAAAAAA);
        ctx.drawTextWithShadow(textRenderer, "HP: " + data.ivHp(), x, y + 15, 0xFF5555);
        ctx.drawTextWithShadow(textRenderer, "ATK: " + data.ivAtk(), x, y + 30, 0xFF9944);
        ctx.drawTextWithShadow(textRenderer, "DEF: " + data.ivDef(), x, y + 45, 0x5577FF);
        ctx.drawTextWithShadow(textRenderer, "SPA: " + data.ivSpA(), x + 60, y + 15, 0xFF55FF);
        ctx.drawTextWithShadow(textRenderer, "SPD: " + data.ivSpD(), x + 60, y + 30, 0x44EEFF);
        ctx.drawTextWithShadow(textRenderer, "VEL: " + data.ivSpe(), x + 60, y + 45, 0xFFFF44);
    }

    private void drawPanel(DrawContext ctx, int x, int y, int w, int h, int bg, int border) {
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.fill(x, y, x + w, y + 1, border);
        ctx.fill(x, y + h - 1, x + w, y + h, border);
        ctx.fill(x, y, x + 1, y + h, border);
        ctx.fill(x + w - 1, y, x + w, y + h, border);
    }
}
