package dohyun22.jst3.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

public interface IScrewDriver {
	boolean onScrewDriver(EntityPlayer pl, EnumFacing f, double px, double py, double pz);
}
