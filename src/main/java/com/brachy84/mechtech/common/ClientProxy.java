package com.brachy84.mechtech.common;

import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.api.armor.modules.Binoculars;
import com.brachy84.mechtech.client.ClientHandler;
import com.brachy84.mechtech.common.items.MTMetaItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.List;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preLoad() {
        super.preLoad();
        ClientHandler.preInit();
    }

    @SubscribeEvent
    public static void onRender(final TickEvent.RenderTickEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.inGameHasFocus && mc.world != null && !mc.gameSettings.showDebugInfo && Minecraft.isGuiEnabled()) {
            for (int i = 0; i < 4; i++) {
                ItemStack stack = mc.player.inventory.armorInventory.get(i);
                ModularArmor modularArmor = ModularArmor.get(stack);
                if (modularArmor != null) {
                    modularArmor.drawHUD(stack);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW) //set to low so other mods don't accidentally destroy it easily
    public static void handleFovEvent(FOVUpdateEvent event) {

        IAttributeInstance iattributeinstance = event.getEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        float f = 1 / ((float) (((iattributeinstance.getAttributeValue() / (double) event.getEntity().capabilities.getWalkSpeed() + 1.0D) / 2.0D)));

        EntityPlayerSP player = Minecraft.getMinecraft().player;

        float zoom = (float) (1 / MTConfig.modularArmor.modules.binocularZoom);

        if (Mouse.isButtonDown(1)) {
            ItemStack binoculars = MTMetaItems.BINOCULARS.getStackForm();
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() != binoculars.getItem() || stack.getMetadata() != binoculars.getMetadata()) {
                stack = player.getHeldItemOffhand();
            }
            if (stack.getItem() == binoculars.getItem() && stack.getMetadata() == binoculars.getMetadata()) {
                event.setNewfov(event.getNewfov() * zoom * f);//*speedFOV;
                return;
            }
        }

        ItemStack helmet = player.inventory.armorInventory.get(3);
        ModularArmor modularArmor = ModularArmor.get(helmet);
        if (modularArmor != null) {
            NBTTagCompound armorData = ModularArmor.getArmorData(helmet);
            if (!armorData.getBoolean("zoom"))
                return;
            List<IModule> modules = ModularArmor.getModulesOf(helmet);
            for (IModule module : modules) {
                if (module instanceof Binoculars) {
                    event.setNewfov(event.getNewfov() * zoom * f);//*speedFOV;
                    break;
                }
            }
        }
    }
}
