package com.teampotato.modifiers.mixin.client;

import com.teampotato.modifiers.client.SmithingScreenReforge;
import com.teampotato.modifiers.client.TabButtonWidget;
import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.network.NetworkHandler;
import com.teampotato.modifiers.common.network.PacketC2SReforge;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreen.class)
public abstract class MixinSmithingScreen extends ForgingScreen<SmithingScreenHandler> implements SmithingScreenReforge {
    @Unique
    private TabButtonWidget modifiers_reforgeButton;
    @Unique
    private TabButtonWidget modifiers_tabButton1;
    @Unique
    private TabButtonWidget modifiers_tabButton2;
    @Unique
    private boolean modifiers_onTab2 = false;
    @Unique
    private boolean modifiers_canReforge = false;

    @Unique
    private Text modifiers_tab1Title;
    @Unique
    private Text modifiers_tab2Title;

    @Unique
    private int modifiers_outputSlotX;
    @Unique
    private int modifiers_outputSlotY;

    public MixinSmithingScreen(SmithingScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @Unique
    private void modifiers_toTab1() {
        modifiers_onTab2 = false;
        modifiers_reforgeButton.visible = false;
        this.title = modifiers_tab1Title;
        Slot slot = this.getScreenHandler().slots.get(2);
        slot.x = modifiers_outputSlotX;
        slot.y = modifiers_outputSlotY;
        this.modifiers_tabButton1.toggled = true;
        this.modifiers_tabButton2.toggled = false;
    }

    @Unique
    private void modifiers_toTab2() {
        modifiers_onTab2 = true;
        modifiers_reforgeButton.visible = true;
        this.title = modifiers_tab2Title;
        Slot slot = this.getScreenHandler().slots.get(2);
        slot.x = 152;
        slot.y = 8;
        this.modifiers_tabButton1.toggled = false;
        this.modifiers_tabButton2.toggled = true;
    }

    @Override
    public void modifiers_init() {
        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        Slot slot = this.getScreenHandler().slots.get(2);
        modifiers_outputSlotX = slot.x;
        modifiers_outputSlotY = slot.y;
        this.modifiers_tabButton1 = new TabButtonWidget(k-70, l+2, 70, 18, new TranslatableText("container.modifiers.reforge.tab1"), (button) -> modifiers_toTab1());
        this.modifiers_tabButton2 = new TabButtonWidget(k-70, l+22, 70, 18, new TranslatableText("container.modifiers.reforge.tab2"), (button) -> modifiers_toTab2());
        this.modifiers_tabButton1.setTextureUV(0, 166, 70, 18, new Identifier("modifiers", "textures/gui/reforger.png"));
        this.modifiers_tabButton2.setTextureUV(0, 166, 70, 18, new Identifier("modifiers", "textures/gui/reforger.png"));
        this.modifiers_reforgeButton = new TabButtonWidget(k+132, l+45, 20, 20, Text.of(""),
                (button) -> NetworkHandler.sendToServer(new PacketC2SReforge()),
                (button, matrixStack, i, j) -> this.renderTooltip(matrixStack, new TranslatableText("container.modifiers.reforge.reforge"), i, j));
        this.modifiers_reforgeButton.setTextureUV(0, 202, 20, 20, new Identifier("modifiers", "textures/gui/reforger.png"));

        this.addButton(this.modifiers_tabButton1);
        this.addButton(this.modifiers_tabButton2);
        this.addButton(this.modifiers_reforgeButton);

        modifiers_tab1Title = this.title;
        modifiers_tab2Title = new TranslatableText("container.modifiers.reforge");
        this.modifiers_toTab1();
    }

    @Override
    public boolean modifiers_isOnTab2() {
        return modifiers_onTab2;
    }

    @Override
    public void modifiers_setCanReforge(boolean canReforge) {
        this.modifiers_canReforge = canReforge;
        this.modifiers_reforgeButton.toggled = canReforge;
        this.modifiers_reforgeButton.active = canReforge;
    }

    @Inject(method = "drawForeground", at = @At("RETURN"))
    private void onDrawForeground(MatrixStack matrixStack, int i, int j, CallbackInfo ci) {
        if (this.modifiers_onTab2) {
            ItemStack stack = this.handler.getSlot(0).getStack();
            Modifier modifier = ModifierHandler.getModifier(stack);
            if (modifier != null) {
                this.textRenderer.draw(matrixStack, new TranslatableText("misc.modifiers.modifier_prefix").append(modifier.getFormattedName()), (float)this.titleX-15, (float)this.titleY+15, 4210752);
            }
        }
    }
}