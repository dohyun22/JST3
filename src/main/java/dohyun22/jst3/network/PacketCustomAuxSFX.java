package dohyun22.jst3.network;

import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCustomAuxSFX implements IMessage {
	private long xyz;
	private byte id;
	private long data;

	public PacketCustomAuxSFX() {}

	public PacketCustomAuxSFX(BlockPos p, int id, long data) {
		this.xyz = p.toLong();
		this.id = (byte) id;
		this.data = data;
	}

	@Override
	public void toBytes(ByteBuf b) {
		b.writeLong(xyz);
		b.writeByte(id);
		b.writeLong(data);
	}

	@Override
	public void fromBytes(ByteBuf b) {
		xyz = b.readLong();
		id = b.readByte();
		data = b.readLong();
	}

	public static class Handler implements IMessageHandler<PacketCustomAuxSFX, IMessage> {
		@Override
		public IMessage onMessage(PacketCustomAuxSFX msg, MessageContext ctx) {
			if (JSTUtils.isClient()) handle(msg);
			return null;
		}

		@SideOnly(Side.CLIENT)
		private void handle(PacketCustomAuxSFX msg) {
			try {
				World w = FMLClientHandler.instance().getClient().world;
				long d = msg.data;
				BlockPos p = BlockPos.fromLong(msg.xyz);
				switch (msg.id) {
				case 1:
					if (d == 0)
						d = 10;
					if (d < 0) {
						w.playSound(p.getX() + 0.5F, p.getY() + 0.5F, p.getZ() + 0.5F, JSTSounds.SHOCK, SoundCategory.BLOCKS, 1.5F, 1.0F, false);
						d *= -1;
					}
					for (int i = 0; i < d; i++)
						w.spawnParticle(EnumParticleTypes.REDSTONE, true, p.getX() + w.rand.nextFloat(), p.getY() + w.rand.nextFloat(), p.getZ() + w.rand.nextFloat(), 0.1D, 1.0D, 1.0D);
					break;
				case 2:
				{
					BlockPos t = BlockPos.fromLong(msg.data);
					double xd = (p.getX() + 0.5D) - (t.getX() + 0.5D), yd = (p.getY() + 0.5D) - (t.getY()), zd = (p.getZ() + 0.5D) - (t.getZ());
					int cnt = (int)(Math.sqrt(xd * xd + yd * yd + zd * zd) / 0.3);
					if (cnt < 4) return;
					xd = xd / (double) cnt;
					yd = yd / (double) cnt;
					zd = zd / (double) cnt;
					for (int i = 0; i < cnt; i++)
						w.spawnParticle(EnumParticleTypes.REDSTONE, true, p.getX() + 0.5 - xd * i, p.getY() + 0.5 - yd * i, p.getZ() + 0.5 - zd * i, 0.1D, 1.0D, 1.0D);
					break;
				}
				case 3:
					int cx = (p.getX() >> 4) * 16, cz = (p.getZ() >> 4) * 16;
					MutableBlockPos p2 = new MutableBlockPos();
					for (int x = 0; x < 16; x++)
						for (int z = 0; z < 16; z++)
							if (x == 0 || x == 15 || z == 0 || z == 15) {
								p2.setPos(cx + x, p.getY(), cz + z);
								w.spawnParticle(EnumParticleTypes.REDSTONE, true, p2.getX() + 0.5, p2.getY() + 0.5, p2.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
							}
					break;
				}
			} catch (Throwable t) {}
		}
	}
}
