package dohyun22.jst3.network;

import dohyun22.jst3.JustServerTweak;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class JSTPacketHandler {
	private static SimpleNetworkWrapper CHANNEL;
	
	public static void preInit() {
		CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(JustServerTweak.MODID + "_nh");
	}

	public static void init() {
		CHANNEL.registerMessage(PacketBiome.Handler.class, PacketBiome.class, 1, Side.CLIENT);
		CHANNEL.registerMessage(PacketCustomAuxSFX.Handler.class, PacketCustomAuxSFX.class, 2, Side.CLIENT);
	}

	public static void sendBiomeChange(World w, int x, int z, Biome b) {
		if (!w.isRemote) {
			IMessage p = new PacketBiome(x, z, b == null ? 0 : (short)Biome.getIdForBiome(b));
			TargetPoint loc = new NetworkRegistry.TargetPoint(w.provider.getDimension(), x, w.getHeight(x, z), z, 128.0D);
			sendPacket(p, loc);
		}
	}

	public static void playCustomEffect(World w, BlockPos p, int id, long data) {
		if (!w.isRemote) {
			IMessage pk = new PacketCustomAuxSFX(p, (short) id, data);
			TargetPoint loc = new NetworkRegistry.TargetPoint(w.provider.getDimension(), p.getX(), p.getY(), p.getZ(), 24.0D);
			sendPacket(pk, loc);
		}
	}

	private static void sendPacket(IMessage msg, TargetPoint p) {
		JSTPacketHandler.CHANNEL.sendToAllAround(msg, p);
	}
}
