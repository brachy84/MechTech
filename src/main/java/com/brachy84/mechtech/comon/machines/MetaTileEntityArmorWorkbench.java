package com.brachy84.mechtech.comon.machines;

import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.client.BatterySlot;
import com.brachy84.mechtech.client.ModuleSlot;
import com.brachy84.mechtech.client.SlotThatActuallyNotfiesListeners;
import com.google.common.collect.Lists;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTLog;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class MetaTileEntityArmorWorkbench extends MetaTileEntity {

    public static final int[][] slotPositions = {
            {5, 26},
            {153, 26},
            {79, 26},
            {42, 47},
            {116, 47}
    };

    public static final int[][] batterySlots = {
            {117, 60},
            {135, 60},
            {153, 60}
    };

    private ItemStack lastArmor = ItemStack.EMPTY;
    private ItemStackHandler mainSlot;
    private ItemStackHandler moduleSlotHandler;
    private ItemStackHandler batterySlotHandler;

    public MetaTileEntityArmorWorkbench(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new MetaTileEntityArmorWorkbench(metaTileEntityId);
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        mainSlot = new ItemStackHandler(1);
        moduleSlotHandler = new ItemStackHandler(5);
        batterySlotHandler = new ItemStackHandler(3);
        return new ItemHandlerList(Lists.newArrayList(mainSlot, moduleSlotHandler, batterySlotHandler));
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.defaultBuilder();
        builder.bindPlayerInventory(entityPlayer.inventory);

        WidgetGroup moduleSlots = new WidgetGroup(Position.ORIGIN, new Size(176, 84));
        for (int i = 0; i < slotPositions.length; i++) {
            int[] pos = slotPositions[i];
            ModuleSlot slot = new ModuleSlot(moduleSlotHandler, i, pos[0], pos[1]);
            slot.setBackgroundTexture(GuiTextures.SLOT);
            slot.setActive(false);
            slot.setVisible(false);
            moduleSlots.addWidget(slot);
        }
        for (int i = 0; i < batterySlots.length; i++) {
            int[] pos = batterySlots[i];
            SlotWidget slot = new BatterySlot(batterySlotHandler, i, pos[0], pos[1]);
            slot.setBackgroundTexture(GuiTextures.SLOT, GuiTextures.BATTERY_OVERLAY);
            slot.setActive(false);
            slot.setVisible(false);
            moduleSlots.addWidget(slot);
        }

        SlotWidget mainSlot = new SlotThatActuallyNotfiesListeners(this.mainSlot, 0, 79, 6).setChangeListener(() -> {
            GTLog.logger.info("Slot changed");
            ItemStack stack = this.mainSlot.getStackInSlot(0);
            for (Widget widget : moduleSlots.widgets) {
                widget.setActive(false);
                widget.setVisible(false);
            }
            if (!stack.isEmpty()) {
                ModularArmor modularArmor = ModularArmor.get(stack);
                if (modularArmor != null) {
                    GTLog.logger.info("Inserted Modular Armor");
                    List<ItemStack> stacks = ModularArmor.getModuleStacksOf(stack);
                    if (stacks.size() > modularArmor.getModuleSlots())
                        throw new IllegalStateException("There were more module than allowed");
                    for (int i = 0; i < stacks.size(); i++) {
                        moduleSlotHandler.setStackInSlot(i, stacks.get(i));
                    }
                    stacks = ModularArmor.getBatteries(stack);
                    for (int i = 0; i < stacks.size(); i++) {
                        batterySlotHandler.setStackInSlot(i, stacks.get(i));
                    }
                    int count = 0;
                    for (Widget widget : moduleSlots.widgets) {
                        if (widget instanceof ModuleSlot) {
                            if (count == modularArmor.getModuleSlots())
                                continue;
                            count++;
                            ((ModuleSlot) widget).setArmorModulePredicate(module -> module.canPlaceIn(modularArmor.getSlot(), stack));
                        }
                        widget.setActive(true);
                        widget.setVisible(true);
                    }
                    lastArmor = stack;
                } else {
                    GTLog.logger.error("Can't find Modular Armor");
                }
            } else if (!lastArmor.isEmpty()) {
                GTLog.logger.info("Removed Modular Armor");
                List<ItemStack> stacks = new ArrayList<>();
                for (int i = 0; i < moduleSlotHandler.getSlots(); i++) {
                    ItemStack stack1 = moduleSlotHandler.getStackInSlot(i);
                    if (!stack1.isEmpty()) {
                        stacks.add(stack1);
                    }
                }
                ModularArmor.writeModulesTo(stacks, lastArmor);
                stacks.clear();
                for (int i = 0; i < batterySlotHandler.getSlots(); i++) {
                    ItemStack stack1 = batterySlotHandler.getStackInSlot(i);
                    if (!stack1.isEmpty()) {
                        stacks.add(stack1);
                    }
                }
                ModularArmor.setBatteries(lastArmor, stacks);
                lastArmor = ItemStack.EMPTY;
                for (int i = 0; i < getImportItems().getSlots(); i++) {
                    getImportItems().setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }).setBackgroundTexture(GuiTextures.SLOT);
        builder.widget(mainSlot);
        builder.widget(moduleSlots);
        return builder.build(getHolder(), entityPlayer);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        NBTTagCompound nbt = super.writeToNBT(data);
        nbt.setTag("LastArmor", lastArmor.writeToNBT(new NBTTagCompound()));
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        lastArmor = new ItemStack(data.getCompoundTag("LastArmor"));
    }
}
