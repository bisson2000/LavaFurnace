package bisson2000.LavaFurnace.network;

import bisson2000.LavaFurnace.LavaFurnace;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {

    private static SimpleChannel INSTANCE;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(LavaFurnace.MOD_ID, "lavafurnace"),
                () -> "1.0",
                s -> true,
                s -> true);

        INSTANCE.messageBuilder(DumpLavaFurnace.class, nextID())
                .encoder(DumpLavaFurnace::toBytes)
                .decoder(DumpLavaFurnace::new)
                .consumer(DumpLavaFurnace::handle)
                .add();
    }

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        INSTANCE.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

}
