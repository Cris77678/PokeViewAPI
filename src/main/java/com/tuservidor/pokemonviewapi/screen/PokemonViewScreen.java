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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PokemonViewScreen extends Screen {
    private static final int LEFT_W = 165, MID_W = 180, RIGHT_W = 165, PANEL_H = 205, GAP = 5;
    private static final int TOTAL_W = LEFT_W + MID_W + RIGHT_W + (GAP * 2);
    private static final float RENDER_SCALE = 38.0f;

    private final FloatingState floatingState = new FloatingState();
    private final PokemonViewData data;

    public PokemonViewScreen(PokemonViewData data) {
        super(Text.literal("PokeBuilder 3D"));
        this.data = data;
    }

    @Override
    protected void init() {
        int sx = (this.width - TOTAL_W) / 2;
        int sy = (this.height - PANEL_H) / 2;
        int midX = sx + LEFT_W + GAP;

        // --- BOTONES DEL PANEL CENTRAL (BUILDER) ---
        this.addDrawableChild(ButtonWidget.builder(Text.literal("✎ NATURALEZA"), b -> {
            // Aquí enviarás el paquete de edición al servidor
        }).dimensions(midX + 15, sy + 45, MID_W - 30, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("⚡ HABILIDAD"), b -> {
        }).dimensions(midX + 15, sy + 70, MID_W - 30, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("✦ SHINY"), b -> {
        }).dimensions(midX + 15, sy + 95, MID_W - 30, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        int sx = (this.width - TOTAL_W) / 2;
        int sy = (this.height - PANEL_H) / 2;
        int midX = sx + LEFT_W + GAP;
        int rightX = midX + MID_W + GAP;

        // 1. PANEL IZQUIERDO: POKEMON 3D
        drawPanel(ctx, sx, sy, LEFT_W, PANEL_H, 0xE0060612, 0xFFFFCC00);
        renderPokemon(ctx, sx + LEFT_W / 2, sy + PANEL_H - 35, mouseX, mouseY, delta);

        // 2. PANEL CENTRAL: BUILDER
        drawPanel(ctx, midX, sy, MID_W, PANEL_H, 0xE0060612, 0xFFFFCC00);
        ctx.drawCenteredTextWithShadow(textRenderer, "- BUILDER -", midX + MID_W/2, sy + 10, 0xFFAA00);

        // 3. PANEL DERECHO: ESTADISTICAS
        drawPanel(ctx, rightX, sy, RIGHT_W, PANEL_H, 0xE0060612, 0xFFFFCC00);
        renderStats(ctx, rightX + 15, sy + 30);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderPokemon(DrawContext ctx, int cx, int cy, int mx, int my, float delta) {
        Species species = PokemonSpecies.INSTANCE.getByIdentifier(data.speciesId());
        if (species == null) return;
        float headYaw = (float)Math.atan((cx - mx) / 40.0f) * 40.0f;
        float headPitch = (float)Math.atan(((cy - 40) - my) / 40.0f) * -20.0f;
        Quaternionf rot = QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13f, 35f, 0f));
        ctx.getMatrices().push();
        ctx.getMatrices().translate(cx, cy, 1000.0);
        ctx.getMatrices().scale(RENDER_SCALE, RENDER_SCALE, RENDER_SCALE);
        PokemonGuiUtilsKt.drawProfilePokemon(new RenderablePokemon(species, data.aspects(), ItemStack.EMPTY),
                ctx.getMatrices(), rot, PoseType.PROFILE, floatingState, delta, 1.0f, false, true, 
                1f, 1f, 1f, 1f, headYaw, headPitch);
        ctx.getMatrices().pop();
    }

    private void renderStats(DrawContext ctx, int x, int y) {
        ctx.drawTextWithShadow(textRenderer, "HP: " + data.ivHp(), x, y, 0xFF5555);
        ctx.drawTextWithShadow(textRenderer, "ATK: " + data.ivAtk(), x, y + 20, 0xFF9944);
        ctx.drawTextWithShadow(textRenderer, "DEF: " + data.ivDef(), x, y + 40, 0x5577FF);
        ctx.drawTextWithShadow(textRenderer, "SPA: " + data.ivSpA(), x, y + 60, 0xFF55FF);
        ctx.drawTextWithShadow(textRenderer, "SPD: " + data.ivSpD(), x, y + 80, 0x44EEFF);
        ctx.drawTextWithShadow(textRenderer, "VEL: " + data.ivSpe(), x, y + 100, 0xFFFF44);
    }

    private void drawPanel(DrawContext ctx, int x, int y, int w, int h, int bg, int border) {
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.fill(x, y, x + w, y + 1, border);
        ctx.fill(x, y + h - 1, x + w, y + h, border);
        ctx.fill(x, y, x + 1, y + h, border);
        ctx.fill(x + w - 1, y, x + w, y + h, border);
    }
}