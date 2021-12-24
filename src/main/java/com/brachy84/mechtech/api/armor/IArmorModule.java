package com.brachy84.mechtech.api.armor;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemComponent;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface IArmorModule extends IItemComponent {

    @Nullable
    static IArmorModule getOf(ItemStack stack) {
        if (stack.getItem() instanceof MetaItem) {
            return getOf(((MetaItem<?>) stack.getItem()).getItem(stack));
        }
        return null;
    }

    @Nullable
    static IArmorModule getOf(MetaItem<?>.MetaValueItem mvi) {
        for (IItemComponent component : mvi.getAllStats()) {
            if (component instanceof IArmorModule)
                return (IArmorModule) component;
        }
        return null;
    }

    /**
     * Returns how many modules of the module are in the handler.
     * Can be used in {@link #canPlaceIn(EntityEquipmentSlot, ItemStack, IItemHandler)} to set a maximum.
     *
     * @param module  to check for
     * @param handler to check in
     * @return module count
     */
    static int moduleCount(IArmorModule module, IItemHandler handler) {
        int count = 0;
        ItemStack moduleItem = module.getAsItemStack(new NBTTagCompound());
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && moduleItem.getItem() == stack.getItem() && moduleItem.getMetadata() == stack.getMetadata() && ItemStack.areItemStackTagsEqual(moduleItem, stack))
                count += stack.getCount();
        }
        return count;
    }

    /**
     * Called when the module is placed in the workbench.
     *
     * @return how many of this modules can be in the same armor piece
     */
    default int maxModules() {
        return 1;
    }

    /**
     * Called when the module is placed in the workbench. If any if these modules is not already there, it can not be placed.
     *
     * @return required modules
     */
    default Collection<IArmorModule> getRequiredModules() {
        return Collections.emptyList();
    }

    /**
     * Called when the module is placed in the workbench. If any if these modules is already there, it can not be placed.
     *
     * @return incompatible modules
     */
    default Collection<IArmorModule> getIncompatibleModules() {
        return Collections.emptyList();
    }

    /**
     * Called each tick when the armor piece with this module is worn
     *
     * @param world             current world
     * @param player            wearing player
     * @param modularArmorPiece armor piece
     * @param armorData         nbt data of armor piece
     */
    default void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData) {
    }

    /**
     * Called when the worn modular armor piece is taken of
     *
     * @param world             current world
     * @param player            wearing player
     * @param modularArmorPiece armor piece
     * @param newStack          the replacing item
     */
    default void onUnequip(World world, EntityLivingBase player, ItemStack modularArmorPiece, ItemStack newStack) {
    }

    /**
     * Used to determin if the module can be placed inside that armor piece
     *
     * @param slot              slot of the armor piece
     * @param modularArmorPiece armor piece
     * @param modularSlots      item handler of the modular slots of the {@link com.brachy84.mechtech.comon.machines.MetaTileEntityArmorWorkbench}
     * @return if the armor accepts this module
     */
    boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots);

    /**
     * Modifies armor values when damaged
     *
     * @param properties        properties passed from the armor piece (can be already modified from other modules)
     * @param entity            wearing entity
     * @param modularArmorPiece armor piece
     * @param source            damage source
     * @param damage            damage amount
     * @param slot              slot of the armor piece
     * @return modified properties with a action result
     * SUCCESS and FAIL: the properties returned are final and will not be modified by other modules
     * PASS: the properties may be modified further by other modules (default)
     */
    default ActionResult<ISpecialArmor.ArmorProperties> modifyArmorProperties(ISpecialArmor.ArmorProperties properties, EntityLivingBase entity, ItemStack modularArmorPiece, DamageSource source, double damage, EntityEquipmentSlot slot) {
        return ActionResult.newResult(EnumActionResult.PASS, properties);
    }

    default int getArmorDisplay(EntityPlayer player, ItemStack armorPiece, int slot) {
        return 0;
    }

    /**
     * @return if this module can be damaged
     */
    default boolean isDamageable() {
        return false;
    }

    /**
     * Called when the armor is damaged
     *
     * @param entityLivingBase    wearing entity
     * @param modularArmorPiece   armor piece
     * @param damageSource        damage source
     * @param i                   amount
     * @param entityEquipmentSlot current slot
     * @return the damage applied to this module
     * SUCCESS: Modules max damage is reached -> destroy module
     * PASS: Nothing happens
     */
    default int damage(EntityLivingBase entityLivingBase, ItemStack modularArmorPiece, NBTTagCompound moduleData, DamageSource damageSource, int i, EntityEquipmentSlot entityEquipmentSlot) {
        return 0;
    }

    /**
     * Tooltip that will be added to the armor piece item (NOT THE MODULE)
     *
     * @param itemStack   armor piece
     * @param worldIn     current world
     * @param lines       tooltip
     * @param tooltipFlag flag
     */
    default void addTooltip(@Nonnull ItemStack itemStack, @Nullable World worldIn, @Nonnull List<String> lines, @Nonnull ITooltipFlag tooltipFlag) {
    }

    /**
     * Called when the module is saved to the armor piece
     *
     * @param nbt        data
     * @param moduleItem this module as item
     */
    default void writeExtraData(NBTTagCompound nbt, ItemStack moduleItem) {
    }

    /**
     * ItemStack representation of this module. This item will be put into the {@link com.brachy84.mechtech.comon.machines.MetaTileEntityArmorWorkbench}
     *
     * @param nbt contains extra data that was written in {@link #writeExtraData(NBTTagCompound, ItemStack)}
     * @return ItemStack representation
     */
    ItemStack getAsItemStack(NBTTagCompound nbt);

    default ItemStack getDestroyedStack() {
        return ItemStack.EMPTY;
    }

    /**
     * @return the name of this module. Will only show in the armor piece tooltip
     */
    @SideOnly(Side.CLIENT)
    default String getLocalizedName() {
        return I18n.format("mechtech.modules." + getModuleId() + ".name");
    }

    String getModuleId();
}
