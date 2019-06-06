package dohyun22.jst3.tiles.energy;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class MetaTileCableSwitch extends MetaTileCable {

	public MetaTileCableSwitch() {
		super("cable_sw", 8192, (byte)2, (byte)4, 5, (byte)6);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileCableSwitch();
	}
	
	@Override
	public void onBlockUpdate() {
		super.onBlockUpdate();
		baseTile.setActive(isRSPowered());
	}

	@Override
	protected long getTransferrablePower(long v, long dist) {
		if (baseTile.isActive()) return 0;
		return super.getTransferrablePower(v, dist);
	}
	
	@Override
	public boolean canConnectRedstone(EnumFacing f) {
		return true;
	}
}
