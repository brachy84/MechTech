package com.brachy84.mechtech.api.armor;

import com.brachy84.mechtech.api.armor.modules.NightVision;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.armor.IArmorLogic;
import gregtech.api.items.armor.ISpecialArmorLogic;
import gregtech.api.util.GTControlledRegistry;
import gregtech.common.items.MetaItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;

public class ModularArmor implements ISpecialArmorLogic {

    public static class Modules {
        public static final IArmorModule nightVision = new NightVision();

        static {
            registerModule(0, nightVision);
        }
    }

    private static final GTControlledRegistry<Class<? extends IArmorModule>, IArmorModule> MODULE_REGISTRY = new GTControlledRegistry<>(256);

    public static void registerModule(int id, IArmorModule module) {
        MODULE_REGISTRY.register(id, module.getClass(), module);
    }

    public static IArmorModule getModule(int id) {
        return MODULE_REGISTRY.getObjectById(id);
    }

    public static IArmorModule getModule(Class<? extends IArmorModule> clazz) {
        return MODULE_REGISTRY.getObject(clazz);
    }

    public static int getModuleId(IArmorModule module) {
        return MODULE_REGISTRY.getIDForObject(module);
    }

    private final int moduleSlots;
    private final EntityEquipmentSlot slot;
    private final int tier;
    private final long baseEnergyCapacity;
    private BiConsumer<IItemHandler, WidgetGroup> uiBuilder = (handler, builder) -> {
    };

    public ModularArmor(EntityEquipmentSlot slot, int moduleSlots, int tier, long baseEnergyCapacity) {
        this.slot = slot;
        this.moduleSlots = moduleSlots;
        this.baseEnergyCapacity = baseEnergyCapacity;
        this.tier = tier;
    }

    public ModularArmor setUiBuilder(BiConsumer<IItemHandler, WidgetGroup> builder) {
        this.uiBuilder = Objects.requireNonNull(builder);
        return this;
    }

    public BiConsumer<IItemHandler, WidgetGroup> getUiBuilder() {
        return uiBuilder;
    }

    public int getTier() {
        return tier;
    }

    public long getBaseEnergyCapacity() {
        return baseEnergyCapacity;
    }

    public int getModuleSlots() {
        return moduleSlots;
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase entityLivingBase, @Nonnull ItemStack itemStack, DamageSource damageSource, double v, EntityEquipmentSlot entityEquipmentSlot) {
        Collection<IArmorModule> modules = getModulesOf(itemStack);
        ISpecialArmor.ArmorProperties properties = new ISpecialArmor.ArmorProperties(0, 0, Integer.MAX_VALUE);
        for (IArmorModule module : modules) {
            module.modifyArmorProperties(properties, entityLivingBase, itemStack, damageSource, v, entityEquipmentSlot);
        }
        return properties;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        Collection<IArmorModule> modules = getModulesOf(itemStack);
        for (IArmorModule module : modules) {
            module.onTick(world, player, itemStack);
        }
    }

    public void onUnequip(World world, EntityLivingBase player, ItemStack modularArmorPiece, ItemStack newStack) {
        Collection<IArmorModule> modules = getModulesOf(modularArmorPiece);
        for (IArmorModule module : modules) {
            module.onUnequip(world, player, modularArmorPiece, newStack);
        }
    }

    @Override
    public int getArmorDisplay(EntityPlayer entityPlayer, @Nonnull ItemStack itemStack, int i) {
        return 0;
    }

    @Override
    public void addToolComponents(ArmorMetaItem.ArmorMetaValueItem mvi) {
        mvi.addComponents(new ModularArmorStats());
    }

    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack itemStack) {
        return slot;
    }

    public EntityEquipmentSlot getSlot() {
        return slot;
    }

    @Override
    public void damageArmor(EntityLivingBase entityLivingBase, ItemStack itemStack, DamageSource damageSource, int i, EntityEquipmentSlot entityEquipmentSlot) {

    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {
        return ImmutableMultimap.of();
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        String armorTexture = "nano_muscule_suite";
        return slot != EntityEquipmentSlot.LEGS ?
                String.format("gregtech:textures/armor/%s_1.png", armorTexture) :
                String.format("gregtech:textures/armor/%s_2.png", armorTexture);
    }

    public static ModularArmor get(ItemStack stack) {
        if (stack.getItem() instanceof ArmorMetaItem) {
            ArmorMetaItem.ArmorMetaValueItem metaValueItem = ((ArmorMetaItem<?>) stack.getItem()).getItem(stack);
            IArmorLogic logic = metaValueItem.getArmorLogic();
            if (logic instanceof ModularArmor) {
                return (ModularArmor) logic;
            }
        }
        return null;
    }

    public static long fill(ItemStack stack, long amount, int tier, boolean simulate) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return 0;
        }
        long original = amount;
        if (nbt.hasKey("Batteries")) {
            NBTTagList list = nbt.getTagList("Batteries", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound batteryNbt = list.getCompoundTagAt(i);
                ItemStack batteryStack = new ItemStack(batteryNbt);
                IElectricItem electricItem = batteryStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (electricItem != null) {
                    amount -= electricItem.charge(amount, tier, false, simulate);
                    if (amount == 0)
                        return amount;
                }
            }
        }
        return amount - original;
    }

    public static long drain(ItemStack stack, long amount, int tier, boolean simulate) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return 0;
        }
        long original = amount;
        if (nbt.hasKey("Batteries")) {
            NBTTagList list = nbt.getTagList("Batteries", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound batteryNbt = list.getCompoundTagAt(i);
                ItemStack batteryStack = new ItemStack(batteryNbt);
                IElectricItem electricItem = batteryStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (electricItem != null) {
                    amount -= electricItem.discharge(amount, tier, false, false, simulate);
                    if (amount == 0)
                        return amount;
                }
            }
        }
        return amount - original;
    }

    public static long getCapacity(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return 0;
        }
        long cap = 0;
        if (nbt.hasKey("Batteries")) {
            NBTTagList list = nbt.getTagList("Batteries", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound batteryNbt = list.getCompoundTagAt(i);
                ItemStack batteryStack = new ItemStack(batteryNbt);
                IElectricItem electricItem = batteryStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (electricItem != null) {
                    cap += electricItem.getMaxCharge();
                }
            }
        }
        return cap;
    }

    public static long getEnergy(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return 0;
        }
        long cap = 0;
        if (nbt.hasKey("Batteries")) {
            NBTTagList list = nbt.getTagList("Batteries", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound batteryNbt = list.getCompoundTagAt(i);
                ItemStack batteryStack = new ItemStack(batteryNbt);
                IElectricItem electricItem = batteryStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (electricItem != null) {
                    cap += electricItem.getCharge();
                }
            }
        }
        return cap;
    }

    public static void setBatteries(ItemStack stack, List<ItemStack> batteries) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
        }
        NBTTagList list = new NBTTagList();
        for (ItemStack battery : batteries) {
            list.appendTag(battery.serializeNBT());
        }
        nbt.setTag("Batteries", list);
        stack.setTagCompound(nbt);
    }

    public static List<ItemStack> getBatteries(ItemStack stack) {
        if(!stack.isEmpty() && stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if(!nbt.hasKey("Batteries"))
                return Collections.emptyList();
            List<ItemStack> batteries = new ArrayList<>();
            NBTTagList list = nbt.getTagList("Batteries", Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < list.tagCount(); i++) {
                batteries.add(new ItemStack(list.getCompoundTagAt(i)));
            }
            return batteries;
        }
        return Collections.emptyList();
    }

    public static List<ItemStack> getModuleStacksOf(ItemStack stack) {
        ModularArmor armor = get(stack);
        if (armor != null) {
            NBTTagCompound nbt = stack.getTagCompound();
            if(nbt == null)
                return Collections.emptyList();
            int[] moduleList = nbt.getIntArray("Modules");
            List<ItemStack> modules = new ArrayList<>();
            for (int moduleId : moduleList) {
                IArmorModule module = getModule(moduleId);
                if (module != null) {
                    modules.add(module.getAsItemStack());
                }
            }
            return modules;
        }

        return Collections.emptyList();
    }

    public static List<IArmorModule> getModulesOf(ItemStack stack) {
        ModularArmor armor = get(stack);
        if (armor != null) {
            NBTTagCompound nbt = stack.getTagCompound();
            if(nbt == null)
                return Collections.emptyList();
            int[] moduleList = nbt.getIntArray("Modules");
            List<IArmorModule> modules = new ArrayList<>();
            for (int moduleId : moduleList) {
                IArmorModule module = getModule(moduleId);
                if (module != null) {
                    modules.add(module);
                }
            }
            return modules;
        }

        return Collections.emptyList();
    }

    public static void writeModulesTo(Collection<ItemStack> modules, ItemStack stack) {
        ModularArmor armor = get(stack);
        if (armor != null) {
            List<Integer> moduleIds = new ArrayList<>();
            //List<ItemStack> batteries = new ArrayList<>();
            for (ItemStack stack1 : modules) {
                if(stack1.isEmpty())
                    continue;
                moduleIds.add(getModuleId(IArmorModule.getOf(stack1)));
                /*if(stack1.hasCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null)) {
                    batteries.add(stack1);
                }*/
            }
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
            }
            nbt.setTag("Modules", new NBTTagIntArray(moduleIds));
            stack.setTagCompound(nbt);
            //setBatteries(stack, batteries);
        }
    }
}
