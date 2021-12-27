package com.brachy84.mechtech.comon.machines;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.client.*;
import com.google.common.collect.Lists;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTUtility;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MetaTileEntityArmorWorkbench extends MetaTileEntity {

    protected static final int[][] slotPositions = {
            {8, 8},
            {26, 8},
            {8, 26},
            {26, 26},
            {8, 44},
            {26, 44},
            {8, 62},
            {26, 62},
            {44, 8},
            {44, 26},
            {44, 44},
            {44, 62},
    };

    protected static final int[][] batterySlots = {
            {115, 60},
            {133, 60},
            {151, 60}
    };

    private static final String REQUIRED = "mechtech.modular_workbench.error1";
    private static final String INCOMPATIBLE = "mechtech.modular_workbench.error2";
    private static final String NOT_MODULE = "mechtech.modular_workbench.error3";
    private static final String MAX_MODULES = "mechtech.modular_workbench.error4";
    private static final String INVALID_SLOT = "mechtech.modular_workbench.error5";

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
        mainSlot = new ItemStackHandler(1) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        moduleSlotHandler = new ItemStackHandler(slotPositions.length) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        batterySlotHandler = new ItemStackHandler(batterySlots.length) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        return new ItemHandlerList(Lists.newArrayList(mainSlot, moduleSlotHandler, batterySlotHandler));
    }

    @Override
    protected ModularUI createUI(EntityPlayer player) {
        ModularUI.Builder builder = ModularUI.builder(ClientHandler.ARMOR_WORKBENCH_BACKGROUND, 176, 166);
        builder.bindPlayerInventory(player.inventory);
        ErrorTextWidget errorTextWidget = new ErrorTextWidget(136, 8).setCentered(true).setWidth(71);
        builder.widget(errorTextWidget);

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
            SlotWidget slot = new BatterySlot(batterySlotHandler, i, pos[0], pos[1])
                    .setFilter(stack -> {
                        IElectricItem electricItem = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                        return electricItem != null && electricItem.getMaxCharge() <= Long.MAX_VALUE / batterySlots.length;
                    });
            slot.setBackgroundTexture(GuiTextures.SLOT, GuiTextures.BATTERY_OVERLAY);
            slot.setActive(false);
            slot.setVisible(false);
            moduleSlots.addWidget(slot);
        }
        builder.image(-26, 0, 26, 88, ClientHandler.ARMOR_SLOTS_BACKGROUND);
        ArmorInventoryWrapper armorInventory = new ArmorInventoryWrapper(player);
        builder.slot(armorInventory, 0, -18, 62, GuiTextures.SLOT);
        builder.slot(armorInventory, 1, -18, 44, GuiTextures.SLOT);
        builder.slot(armorInventory, 2, -18, 26, GuiTextures.SLOT);
        builder.slot(armorInventory, 3, -18, 8, GuiTextures.SLOT);

        SlotWidget mainSlot = new SlotThatActuallyNotfiesListeners(this.mainSlot, 0, 79, 26).setBackgroundTexture(GuiTextures.SLOT).setChangeListener(() -> {
            ItemStack stack = this.mainSlot.getStackInSlot(0);
            for (Widget widget : moduleSlots.widgets) {
                widget.setActive(false);
                widget.setVisible(false);
            }
            if (!stack.isEmpty()) {
                ModularArmor modularArmor = ModularArmor.get(stack);
                if (modularArmor != null) {
                    List<ItemStack> stacks = ModularArmor.getModuleStacksOf(stack);
                    if (stacks.size() > modularArmor.getModuleSlots())
                        throw new IllegalStateException("There were more modules than allowed");
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
                            ((ModuleSlot) widget).setPredicate(stack1 -> {
                                IModule module = IModule.getOf(stack1);
                                if (module == null) {
                                    errorTextWidget.updateText(NOT_MODULE);
                                    return false;
                                }
                                if (!module.canPlaceIn(modularArmor.getSlot(), stack, moduleSlotHandler)) {
                                    errorTextWidget.updateText(INVALID_SLOT);
                                    return false;
                                }
                                if (!(module.maxModules() <= 0 || module.maxModules() > IModule.moduleCount(module, moduleSlotHandler))) {
                                    errorTextWidget.updateText(MAX_MODULES);
                                    return false;
                                }
                                List<IModule> modules = getModules();
                                for (IModule module1 : modules) {
                                    if (module1.getIncompatibleModules().contains(module) || module.getIncompatibleModules().contains(module1)) {
                                        errorTextWidget.updateText(INCOMPATIBLE, module1.getLocalizedName());
                                        return false;
                                    }
                                }
                                for (IModule module1 : module.getRequiredModules()) {
                                    if (!modules.contains(module1)) {
                                        errorTextWidget.updateText(REQUIRED, module1.getLocalizedName());
                                        return false;
                                    }
                                }
                                return true;
                            });
                        }
                        widget.setActive(true);
                        widget.setVisible(true);
                    }
                    lastArmor = stack;
                }
            } else if (!lastArmor.isEmpty()) {
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
        });
        builder.widget(mainSlot);
        builder.widget(moduleSlots);
        return builder.build(getHolder(), player);
    }

    private List<IModule> getModules() {
        List<IModule> modules = new ArrayList<>();
        for (int i = 0; i < moduleSlotHandler.getSlots(); i++) {
            IModule module = IModule.getOf(moduleSlotHandler.getStackInSlot(i));
            if (module != null)
                modules.add(module);
        }
        return modules;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        int paintingColor = getPaintingColorForRendering();
        pipeline = ArrayUtils.add(pipeline, new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(paintingColor)));
        Textures.CRAFTING_TABLE.renderOriented(renderState, translation, pipeline, getFrontFacing());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        NBTTagCompound nbt = super.writeToNBT(data);
        nbt.setTag("LastArmor", lastArmor.serializeNBT());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        lastArmor = new ItemStack(data.getCompoundTag("LastArmor"));
    }

    private static class ArmorInventoryWrapper implements IItemHandlerModifiable {

        private final EntityPlayer player;

        public ArmorInventoryWrapper(EntityPlayer player) {
            this.player = player;
        }

        @Override
        public int getSlots() {
            return 4;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return player.inventory.armorInventory.get(slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            ItemStack currentStack = getStackInSlot(slot);
            EntityEquipmentSlot equipmentSlot = stack.getItem().getEquipmentSlot(stack);
            if (stack.isEmpty() || !currentStack.isEmpty() || equipmentSlot == null || equipmentSlot.getIndex() != slot)
                return stack;
            ItemStack copy = stack.copy();
            copy.setCount(1);
            if (!simulate)
                setStackInSlot(slot, copy);
            if (stack.getCount() == 1)
                return ItemStack.EMPTY;
            stack.shrink(1);
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack currentStack = getStackInSlot(slot);
            if (amount <= 0 || currentStack.isEmpty())
                return ItemStack.EMPTY;
            if (!simulate)
                setStackInSlot(slot, ItemStack.EMPTY);
            return currentStack;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            player.inventory.armorInventory.set(slot, stack);
        }
    }
}
