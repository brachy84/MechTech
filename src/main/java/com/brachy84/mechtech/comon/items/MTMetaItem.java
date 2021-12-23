package com.brachy84.mechtech.comon.items;

import com.brachy84.mechtech.api.armor.Modules;
import com.brachy84.mechtech.api.armor.modules.ProtectionModule;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.api.items.metaitem.stats.*;
import gregtech.api.unification.material.Material;
import gregtech.common.items.MetaItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Map;

import static com.brachy84.mechtech.comon.items.MTMetaItems.MATERIAL_ARMOR_PLATINGS;
import static com.brachy84.mechtech.comon.items.MTMetaItems.WIRELESS_RECEIVER;

public class MTMetaItem extends StandardMetaItem {

    public MTMetaItem() {
        super((short) 0);
    }

    @Override
    public void registerSubItems() {
        WIRELESS_RECEIVER = addItem(0, "wireless_receiver");
        //WIRELESS_BINDER = addItem(2001, "wireless_binder");

        MetaItems.NIGHTVISION_GOGGLES.addComponents(Modules.nightVision);
        MetaItems.COVER_SOLAR_PANEL_LV.addComponents(Modules.solarGen1);
        MetaItems.COVER_SOLAR_PANEL_MV.addComponents(Modules.solarGen2);
        MetaItems.COVER_SOLAR_PANEL_HV.addComponents(Modules.solarGen3);
        for (Map.Entry<Integer, Material> entry : Modules.getArmorModules().entrySet()) {
            ProtectionModule module = (ProtectionModule) Modules.getModule(entry.getKey());
            MetaItem<?>.MetaValueItem metaValueItem = addItem(entry.getKey(), "armor_plating_" + entry.getValue().toString())
                    .addComponents(Modules.getModule(entry.getKey()), ((IItemColorProvider) (stack, layer) -> entry.getValue().getMaterialRGB()))
                    .addComponents(new IItemBehaviour() {
                        @Override
                        public void addInformation(ItemStack itemStack, List<String> lines) {
                            NBTTagCompound nbt = itemStack.getTagCompound();
                            int damaged = 0;
                            if (nbt != null && nbt.hasKey("Dmg")) {
                                damaged = nbt.getInteger("Dmg");
                            }
                            lines.add(I18n.format("mechtech.modules.armor_plating.tooltip.1", damaged, module.durability));
                            lines.add(I18n.format("mechtech.modules.armor_plating.tooltip.2", module.armor));
                            lines.add(I18n.format("mechtech.modules.armor_plating.tooltip.3", module.toughness));
                        }
                    })
                    .addComponents(((IItemNameProvider) (stack, name) -> I18n.format("mechtech.modules.armor_plating.name", entry.getValue().getLocalizedName())))
                    .addComponents((IItemMaxStackSizeProvider) (itemStack, i) -> 64)
                    .addComponents(new IItemDurabilityManager() {
                        @Override
                        public boolean showsDurabilityBar(ItemStack itemStack) {
                            NBTTagCompound nbt = itemStack.getTagCompound();
                            return nbt != null && nbt.getInteger("Dmg") > 0;
                        }

                        @Override
                        public double getDurabilityForDisplay(ItemStack itemStack) {
                            NBTTagCompound nbt = itemStack.getTagCompound();
                            return nbt == null ? 0 : nbt.getInteger("Dmg") / ((double) module.durability);
                        }

                        @Override
                        public int getRGBDurabilityForDisplay(ItemStack itemStack) {
                            return MathHelper.hsvToRGB((1.0f - (float) getDurabilityForDisplay(itemStack)) / 3.0f, 1.0f, 1.0f);
                        }
                    });
            MATERIAL_ARMOR_PLATINGS.put(entry.getValue(), metaValueItem);

            module.setStack(metaValueItem.getStackForm());
        }
        //MetaItems.TOOL_DATA_STICK.addComponents(new DataStickBehavior());
    }

    @Override
    protected String formatModelPath(MetaItem<?>.MetaValueItem metaValueItem) {
        if (metaValueItem.unlocalizedName.startsWith("armor_plating_")) {
            return "metaitems/armor_plating";
        }
        return super.formatModelPath(metaValueItem);
    }
}
