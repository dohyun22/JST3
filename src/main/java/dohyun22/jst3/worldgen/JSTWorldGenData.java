package dohyun22.jst3.worldgen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class JSTWorldGenData extends WorldSavedData {

	public JSTWorldGenData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return tag;
	}

}
