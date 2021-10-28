package com.brachy84.mechtech.machines;

import com.brachy84.mechtech.armor.ModularArmor;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.armor.IArmorLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTUtility;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaTileEntityArmorWorkbench extends MetaTileEntity {

    private WidgetGroup activeWidgetGroup;
    private ItemStack lastArmor = ItemStack.EMPTY;

    public MetaTileEntityArmorWorkbench(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new MetaTileEntityArmorWorkbench(metaTileEntityId);
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return new ItemStackHandler(30);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.defaultBuilder();
        builder.bindPlayerInventory(entityPlayer.inventory);
        Map<ItemStack, WidgetGroup> modularArmors = new HashMap<>();
        for (int i = 0; i < entityPlayer.inventory.getSizeInventory(); i++) {
            ItemStack stack = entityPlayer.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ArmorMetaItem) {
                ArmorMetaItem.ArmorMetaValueItem metaValueItem = ((ArmorMetaItem<?>) stack.getItem()).getItem(stack);
                IArmorLogic logic = metaValueItem.getArmorLogic();
                if (logic instanceof ModularArmor) {
                    ModularArmor modularArmor = (ModularArmor) logic;
                    WidgetGroup widgetGroup = new WidgetGroup(Position.ORIGIN, new Size(176, 66));
                    modularArmor.getUiBuilder().accept(getImportItems(), widgetGroup);
                    widgetGroup.setVisible(false);
                    widgetGroup.setActive(false);
                    modularArmors.put(stack, widgetGroup);
                    builder.widget(widgetGroup);
                }
            }
        }
        SlotWidget mainSlot = new SlotWidget(getImportItems(), 0, 97, 50).setChangeListener(() -> {
            ItemStack stack = getImportItems().getStackInSlot(0);
            if (activeWidgetGroup != null) {
                activeWidgetGroup.setActive(false);
                activeWidgetGroup.setVisible(false);
                activeWidgetGroup = null;
            }
            if (!stack.isEmpty()) {
                activeWidgetGroup = modularArmors.get(stack);
                if (activeWidgetGroup != null) {
                    List<ItemStack> stacks = ModularArmor.getModuleStacksOf(stack);
                    for(int i = 0; i < stacks.size(); i++) {
                        getImportItems().setStackInSlot(i+1, stacks.get(i));
                    }
                    activeWidgetGroup.setVisible(true);
                    activeWidgetGroup.setActive(true);
                    lastArmor = stack;
                }
            } else {
                List<ItemStack> stacks = new ArrayList<>();
                for(int i = 1; i < getImportItems().getSlots(); i++) {
                    ItemStack stack1 = getImportItems().getStackInSlot(i);
                    if(!stack1.isEmpty()) {
                        stacks.add(stack1);
                    }
                }
                ModularArmor.writeModulesTo(stacks, lastArmor);
                lastArmor = ItemStack.EMPTY;
                for(int i = 0; i < getImportItems().getSlots(); i++) {
                    getImportItems().setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        });
        builder.widget(mainSlot);
        return null;
    }
}
