package com.brachy84.mechtech.comon;

import com.brachy84.mechtech.client.ClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy{

    @Override
    public void preLoad() {
        super.preLoad();
        ClientHandler.preInit();
    }
}
