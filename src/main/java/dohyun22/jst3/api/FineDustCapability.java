package dohyun22.jst3.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class FineDustCapability {
	@CapabilityInject(IDust.class)
	public static Capability<IDust> FINEDUST = null;

	public static class Impl implements Capability.IStorage<IDust> {
		@Override
		public NBTBase writeNBT(Capability<IDust> c, IDust d, EnumFacing s) {return null;}

		@Override
		public void readNBT(Capability<IDust> c, IDust d, EnumFacing s, NBTBase t) {}
	}

	public static class Default implements IDust {
		@Override
		public boolean canGen() {return false;}

		@Override
		public int getDust() {return 0;}
	}

	public static String toMicrogram(int ng) {
		return Double.valueOf((long)ng / 1000.0D).toString();
	}
}
