package com.brachy84.mechtech.api.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.armor.IArmorLogic;
import gregtech.api.items.armor.ISpecialArmorLogic;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;

public class ModularArmor implements ISpecialArmorLogic {

    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

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

    public static final String BATTERIES = "Batteries";
    public static final String MODULES = "Modules";
    private final int moduleSlots;
    private final EntityEquipmentSlot slot;

    public ModularArmor(EntityEquipmentSlot slot, int moduleSlots) {
        this.slot = slot;
        this.moduleSlots = moduleSlots;
    }

    public int getModuleSlots() {
        return moduleSlots;
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase entityLivingBase, @Nonnull ItemStack itemStack, DamageSource damageSource, double v, EntityEquipmentSlot entityEquipmentSlot) {
        Collection<IArmorModule> modules = getModulesOf(itemStack);
        ISpecialArmor.ArmorProperties properties = new ISpecialArmor.ArmorProperties(0, 0, Integer.MAX_VALUE);
        properties.Slot = entityEquipmentSlot.getIndex();
        for (IArmorModule module : modules) {
            ActionResult<ISpecialArmor.ArmorProperties> result = module.modifyArmorProperties(properties, entityLivingBase, itemStack, damageSource, v, entityEquipmentSlot);
            properties = result.getResult();
            if (result.getType() != EnumActionResult.PASS)
                break;
        }
        return properties;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        Collection<IArmorModule> modules = getModulesOf(itemStack);
        NBTTagCompound armorNbt = itemStack.getTagCompound();
        NBTTagCompound nbt;
        if (armorNbt != null) {
            nbt = armorNbt.getCompoundTag("ModuleData");
        } else {
            armorNbt = new NBTTagCompound();
            nbt = new NBTTagCompound();
            itemStack.setTagCompound(armorNbt);
        }
        for (IArmorModule module : modules) {
            module.onTick(world, player, itemStack, nbt);
        }
        armorNbt.setTag("ModuleData", nbt);
    }

    public void onUnequip(World world, EntityLivingBase player, ItemStack modularArmorPiece, ItemStack newStack) {
        Collection<IArmorModule> modules = getModulesOf(modularArmorPiece);
        for (IArmorModule module : modules) {
            module.onUnequip(world, player, modularArmorPiece, newStack);
        }
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, @Nonnull ItemStack armorPiece, int i) {
        List<IArmorModule> modules = ModularArmor.getModulesOf(armorPiece);
        int armor = 0;
        for (IArmorModule module : modules) {
            armor += module.getArmorDisplay(player, armorPiece, i);
        }
        return armor;
    }

    @Override
    public void addToolComponents(ArmorMetaItem.ArmorMetaValueItem mvi) {
        mvi.addComponents(new ModularArmorStats());
        mvi.addComponents(new IItemBehaviour() {
            @Override
            public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
                if (player.getHeldItem(hand).getItem() instanceof ArmorMetaItem) {
                    ItemStack armor = player.getHeldItem(hand);
                    if (armor.getItem() instanceof ArmorMetaItem && player.inventory.armorInventory.get(slot.getIndex()).isEmpty() && !player.isSneaking()) {
                        player.inventory.armorInventory.set(slot.getIndex(), armor.copy());
                        player.setHeldItem(hand, ItemStack.EMPTY);
                        player.playSound(new SoundEvent(new ResourceLocation("item.armor.equip_generic")), 1.0F, 1.0F);
                        return ActionResult.newResult(EnumActionResult.SUCCESS, armor);
                    }
                }
                return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
            }
        });
    }

    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack itemStack) {
        return slot;
    }

    public EntityEquipmentSlot getSlot() {
        return slot;
    }

    @Override
    public boolean handleUnblockableDamage(EntityLivingBase entity, @Nonnull ItemStack armor, DamageSource source, double damage, EntityEquipmentSlot equipmentSlot) {
        return true;
    }

    // I hate how this is done, but it seems to be the best solution
    @Override
    public void damageArmor(EntityLivingBase entityLivingBase, ItemStack stack, DamageSource damageSource, int damage, EntityEquipmentSlot entityEquipmentSlot) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey(MODULES))
            return;
        List<Pair<IArmorModule, NBTTagCompound>> modules = new ArrayList<>();
        NBTTagList modulesNbt = nbt.getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < modulesNbt.tagCount(); i++) {
            NBTTagCompound moduleNbt = modulesNbt.getCompoundTagAt(i);
            IArmorModule module = Modules.getModule(moduleNbt.getInteger("ID"));
            if (module.isDamageable() && !moduleNbt.getBoolean("Destroyed")) {
                modules.add(Pair.of(module, moduleNbt));
            }
        }
        while (modules.size() > 0 && damage > 0) {
            Iterator<Pair<IArmorModule, NBTTagCompound>> iterator = modules.iterator();
            int c = damage / modules.size();
            int m = damage % modules.size();
            while (iterator.hasNext()) {
                Pair<IArmorModule, NBTTagCompound> entry = iterator.next();
                int dmg = c;
                if (m > 0) {
                    dmg++;
                    m--;
                } else if (dmg == 0) {
                    break;
                }
                int damaged = entry.getKey().damage(entityLivingBase, stack, entry.getValue(), damageSource, dmg, entityEquipmentSlot);
                damage -= damaged;
                if (damaged != dmg || entry.getValue().getBoolean("Destroyed")) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack itemStack) {
        ImmutableMultimap.Builder<String, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        return builder.build();
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        String armorTexture = "nano_muscule_suite";
        return slot != EntityEquipmentSlot.LEGS ?
                String.format("gregtech:textures/armor/%s_1.png", armorTexture) :
                String.format("gregtech:textures/armor/%s_2.png", armorTexture);
    }

    public static long fill(ItemStack stack, long amount, int tier, boolean simulate) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return 0;
        }
        long original = amount;
        if (nbt.hasKey(BATTERIES)) {
            NBTTagList list = nbt.getTagList(BATTERIES, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound batteryNbt = list.getCompoundTagAt(i);
                ItemStack batteryStack = new ItemStack(batteryNbt);
                IElectricItem electricItem = batteryStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (electricItem != null) {
                    amount -= electricItem.charge(amount, tier, false, simulate);
                    batteryNbt = batteryStack.serializeNBT();
                    list.set(i, batteryNbt);
                    if (amount == 0)
                        break;
                }
            }
            nbt.setTag(BATTERIES, list);
        }
        return original - amount;
    }

    public static long drain(ItemStack stack, long amount, int tier, boolean simulate) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return 0;
        }
        long original = amount;
        if (nbt.hasKey(BATTERIES)) {
            NBTTagList list = nbt.getTagList(BATTERIES, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound batteryNbt = list.getCompoundTagAt(i);
                ItemStack batteryStack = new ItemStack(batteryNbt);
                IElectricItem electricItem = batteryStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (electricItem != null) {
                    amount -= electricItem.discharge(amount, tier, false, false, simulate);
                    batteryNbt = batteryStack.serializeNBT();
                    list.set(i, batteryNbt);
                    if (amount == 0)
                        break;
                }
            }
            nbt.setTag(BATTERIES, list);
        }
        return original - amount;
    }

    public static long getCapacity(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return 0;
        }
        long cap = 0;
        if (nbt.hasKey(BATTERIES)) {
            NBTTagList list = nbt.getTagList(BATTERIES, Constants.NBT.TAG_COMPOUND);
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
        if (nbt.hasKey(BATTERIES)) {
            NBTTagList list = nbt.getTagList(BATTERIES, Constants.NBT.TAG_COMPOUND);
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
        nbt.setTag(BATTERIES, list);
        stack.setTagCompound(nbt);
    }

    public static List<ItemStack> getBatteries(ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (!nbt.hasKey(BATTERIES))
                return Collections.emptyList();
            List<ItemStack> batteries = new ArrayList<>();
            NBTTagList list = nbt.getTagList(BATTERIES, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
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
            if (nbt == null || !nbt.hasKey(MODULES))
                return Collections.emptyList();
            List<ItemStack> modules = new ArrayList<>();
            NBTTagList modulesNbt = nbt.getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < modulesNbt.tagCount(); i++) {
                NBTTagCompound moduleNbt = modulesNbt.getCompoundTagAt(i);
                IArmorModule module = Modules.getModule(moduleNbt.getInteger("ID"));
                if (moduleNbt.getBoolean("Destroyed")) {
                    modules.add(module.getDestroyedStack());
                } else {
                    modules.add(module.getAsItemStack(moduleNbt));
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
            if (nbt == null || !nbt.hasKey(MODULES))
                return Collections.emptyList();
            NBTTagList modulesNbt = nbt.getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
            List<IArmorModule> modules = new ArrayList<>();
            for (int i = 0; i < modulesNbt.tagCount(); i++) {
                NBTTagCompound moduleNbt = modulesNbt.getCompoundTagAt(i);
                IArmorModule module = Modules.getModule(moduleNbt.getInteger("ID"));
                if (!moduleNbt.getBoolean("Destroyed"))
                    modules.add(module);
            }
            return modules;
        }

        return Collections.emptyList();
    }

    public static void writeModulesTo(Collection<ItemStack> modules, ItemStack stack) {
        ModularArmor armor = get(stack);
        if (armor != null) {
            NBTTagList modulesNbt = new NBTTagList();
            for (ItemStack stack1 : modules) {
                NBTTagCompound moduleNbt = new NBTTagCompound();
                IArmorModule module = IArmorModule.getOf(stack1);
                moduleNbt.setInteger("ID", Modules.getModuleId(module));
                module.writeExtraData(moduleNbt, stack1);
                modulesNbt.appendTag(moduleNbt);
            }
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            nbt.setTag(MODULES, modulesNbt);
        }
    }

    public static void removeModules(ItemStack stack, Collection<Integer> indexes) {
        ModularArmor armor = get(stack);
        if (armor != null) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null || !nbt.hasKey(MODULES))
                return;
            NBTTagList modulesNbt = nbt.getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
            NBTTagList newList = new NBTTagList();
            for (int i = 0; i < modulesNbt.tagCount(); i++) {
                if (!indexes.contains(i))
                    newList.appendTag(modulesNbt.get(i));
            }
            nbt.setTag(MODULES, newList);
        }
    }

    public static double armorDamageSpread(EntityEquipmentSlot slot) {
        switch (slot) {
            case HEAD:
                return 0.20;
            case CHEST:
                return 0.375;
            case LEGS:
                return 0.275;
            case FEET:
                return 0.15;
            default:
                return 0;
        }
    }
}
