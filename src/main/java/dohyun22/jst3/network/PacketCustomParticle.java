package dohyun22.jst3.network;

import dohyun22.jst3.client.ParticleFlamethrower;
import dohyun22.jst3.utils.JSTUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCustomParticle implements IMessage {
	private byte type, cnt, diff;
	private double x, y, z, sx, sy, sz;

	public PacketCustomParticle() {}

	public PacketCustomParticle(int t, int cnt, int diff, double x, double y, double z, double sx, double sy, double sz) {
		type = (byte)t; this.cnt = (byte)cnt; this.diff = (byte)diff;
		this.x = x; this.y = y; this.z = z;
		this.sx = sx; this.sy = sy; this.sz = sz;
	}

	@Override
	public void toBytes(ByteBuf b) {
		b.writeByte(type);
		b.writeByte(cnt);
		b.writeByte(diff);
		b.writeDouble(x);
		b.writeDouble(y);
		b.writeDouble(z);
		b.writeDouble(sx);
		b.writeDouble(sy);
		b.writeDouble(sz);
	}

	@Override
	public void fromBytes(ByteBuf b) {
		type = b.readByte();
		cnt = b.readByte();
		diff = b.readByte();
		x = b.readDouble();
		y = b.readDouble();
		z = b.readDouble();
		sx = b.readDouble();
		sy = b.readDouble();
		sz = b.readDouble();
	}

	public static class Handler implements IMessageHandler<PacketCustomParticle, IMessage> {
		@Override
		public IMessage onMessage(PacketCustomParticle msg, MessageContext ctx) {
			if (JSTUtils.isClient()) handle(msg);
			return null;
		}

		@SideOnly(Side.CLIENT)
		private void handle(PacketCustomParticle msg) {
			try {
				if (msg.cnt <= 0) msg.cnt = 1;
				World w = FMLClientHandler.instance().getClient().world;
				EnumParticleTypes[] v = EnumParticleTypes.values();
				float d = msg.diff * 0.05F;
				float vx = 0, vy = 0, vz = 0;
				if (msg.type >= 0 && msg.type < v.length) {
					for (int n = 0; n < msg.cnt; n++) {
						vx = w.rand.nextFloat() * d - d / 2;
						vy = w.rand.nextFloat() * d - d / 2;
						vz = w.rand.nextFloat() * d - d / 2;
						w.spawnParticle(v[msg.type], true, msg.x, msg.y, msg.z, msg.sx + vx, msg.sy + vy, msg.sz + vz);
					}
				} else if (msg.type < 0) {
					ParticleManager pm = Minecraft.getMinecraft().effectRenderer;
					for (int n = 0; n < msg.cnt; n++) {
						vx = w.rand.nextFloat() * d - d / 2;
						vy = w.rand.nextFloat() * d - d / 2;
						vz = w.rand.nextFloat() * d - d / 2;
						Particle p = getParticle(msg.type, w, msg.x, msg.y, msg.z, msg.sx + vx, msg.sy + vy, msg.sz + vz);
						if (p != null) pm.addEffect(p);
					}
				}
			} catch (Throwable t) {}
		}

		@SideOnly(Side.CLIENT)
		private Particle getParticle(byte t, World w, double x, double y, double z, double sx, double sy, double sz) {
			switch (t) {
			case -1: return new ParticleFlamethrower(w, x, y, z, sx, sy, sz);
			}
			return null;
		}
	}
}
