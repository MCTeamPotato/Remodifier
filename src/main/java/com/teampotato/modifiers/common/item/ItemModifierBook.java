package com.teampotato.modifiers.common.item;

import com.teampotato.modifiers.ModifiersMod;
import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.modifier.Modifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemModifierBook extends Item {
    public ItemModifierBook() {
        super(new Settings().rarity(Rarity.EPIC).group(ModifiersMod.GROUP_BOOKS));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if (!stack.hasTag()) return false;
        NbtCompound tag = stack.getTag();
        if (tag == null) return false;
        return tag.contains(ModifierHandler.bookTagName) && !tag.getString(ModifierHandler.bookTagName).equals("modifiers:none");
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
        if (this.isIn(group)) items.addAll(getStacks());
    }

    @Override
    public Text getName(ItemStack stack) {
        Text base = super.getName(stack);
        if (!stack.hasTag() || (stack.getTag() != null && !stack.getTag().contains(ModifierHandler.bookTagName))) return base;
        Modifier mod = Modifiers.MODIFIERS.get(new Identifier(stack.getTag().getString(ModifierHandler.bookTagName)));
        if (mod == null) return base;
        return new TranslatableText("misc.modifiers.modifier_prefix").append(mod.getFormattedName());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        String translationKey = this.getTranslationKey();
        if (stack.getTag() != null && stack.getTag().contains(ModifierHandler.bookTagName)) {
            Modifier mod = Modifiers.MODIFIERS.get(new Identifier(stack.getTag().getString(ModifierHandler.bookTagName)));
            if (mod != null) {
                tooltip.addAll(mod.getInfoLines());
                tooltip.add(new TranslatableText(translationKey + ".tooltip.0"));
                tooltip.add(new TranslatableText(translationKey + ".tooltip.1"));
                return;
            }
        }
        tooltip.add(new TranslatableText(translationKey + ".tooltip.invalid"));
    }

    protected List<ItemStack> getStacks() {
        List<Modifier> modifiers = new ObjectArrayList<>();
        modifiers.add(Modifiers.NONE);
        modifiers.addAll(Modifiers.curioPool.modifiers);
        modifiers.addAll(Modifiers.toolPool.modifiers);
        modifiers.addAll(Modifiers.shieldPool.modifiers);
        modifiers.addAll(Modifiers.bowPool.modifiers);
        modifiers.addAll(Modifiers.armorPool.modifiers);

        List<ItemStack> stacks = new ObjectArrayList<>();
        for (Modifier mod : modifiers) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putString(ModifierHandler.bookTagName, mod.name.toString());
            stacks.add(stack);
        }
        return stacks;
    }
}

