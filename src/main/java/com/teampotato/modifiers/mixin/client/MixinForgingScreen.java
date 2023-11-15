package com.teampotato.modifiers.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teampotato.modifiers.client.SmithingScreenReforge;
import com.teampotato.modifiers.common.config.toml.ReforgeConfig;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ForgingScreen.class)
public abstract class MixinForgingScreen<T extends ForgingScreenHandler> extends HandledScreen<T> {


    @Unique
    private static final Identifier modifiers$reforger = new Identifier("modifiers", "textures/gui/reforger.png");

    public MixinForgingScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @SuppressWarnings({"ConstantValue", "deprecation"})
    @Inject(method = "drawBackground", at = @At("HEAD"), cancellable = true)
    private void onDrawBackground(MatrixStack matrixStack, float f, int i, int j, CallbackInfo ci) {
        if (((Object) this) instanceof SmithingScreen && ((SmithingScreenReforge) this).modifiers_isOnTab2()) {
            ci.cancel();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.client.getTextureManager().bindTexture(modifiers$reforger);
            int k = (this.width - this.backgroundWidth) / 2;
            int l = (this.height - this.backgroundHeight) / 2;
            this.drawTexture(matrixStack, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
            ItemStack stack1 = this.handler.getSlot(0).getStack();
            ItemStack stack2 = this.handler.getSlot(1).getStack();
            boolean isUniversalReforgeItem = ReforgeConfig.UNIVERSAL_REFORGE_ITEM.get().equals(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack2.getItem())).toString());
            boolean cantReforge = !stack1.isEmpty() && !stack1.getItem().canRepair(stack1, stack2);
            if (ReforgeConfig.DISABLE_REPAIR_REFORGED.get() && !cantReforge) cantReforge = true;
            if (isUniversalReforgeItem && cantReforge) cantReforge = false;
            // canReforge is also true for empty slot 1. Probably how it should behave.
            ((SmithingScreenReforge) this).modifiers_setCanReforge(!cantReforge);
            if (cantReforge) this.drawTexture(matrixStack, k + 99 - 53, l + 45, this.backgroundWidth, 0, 28, 21);
        }
    }
}