package dohyun22.jst3.network;

import dohyun22.jst3.utils.JSTUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketBiome implements IMessage {
	private int x;
	private int z;
	private short id;

	public PacketBiome() {}

	public PacketBiome(int x, int z, short bid) {
		this.x = x;
		this.z = z;
		this.id = bid;
	}

	@Override
	public void toBytes(ByteBuf bb) {
		bb.writeInt(x);
		bb.writeInt(z);
		bb.writeShort(id);
	}

	@Override
	public void fromBytes(ByteBuf bb) {
		x = bb.readInt();
		z = bb.readInt();
		id = bb.readShort();
	}

	public static class Handler implements IMessageHandler<PacketBiome, IMessage> {
		@Override
		public IMessage onMessage(PacketBiome msg, MessageContext ctx) {
			if (JSTUtils.isClient()) handle(msg);
			return null;
		}

		@SideOnly(Side.CLIENT)
		private void handle(PacketBiome msg) {
			try {
				Biome b = Biome.getBiome(msg.id);
				if (b != null)
					JSTUtils.setBiome(FMLClientHandler.instance().getClient().world, msg.x, msg.z, b);
			} catch (Throwable t) {}
		}
	}
}