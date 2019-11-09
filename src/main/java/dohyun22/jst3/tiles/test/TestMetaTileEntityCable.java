package dohyun22.jst3.tiles.test;

import java.util.ArrayList;
import java.util.List;

import dohyun22.jst3.api.IDust;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.api.IScrewDriver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

public class TestMetaTileEntityCable extends MetaTileBase implements IScrewDriver, IDust{
	
	public TestMetaTileEntityCable(int tier, boolean rsc) {
		
	}
	
	@Override
	public boolean canGen() {
		return false;
	}

	@Override
	public int getDust() {
		return 0;
	}

	@Override
	public boolean onScrewDriver(EntityPlayer pl, EnumFacing f, double px, double py, double pz) {
		return false;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return null;
	}
	
}
