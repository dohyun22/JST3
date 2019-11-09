package dohyun22.jst3.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDCGenerator {
	
	/** This method is used for calculating how much power generated from connected DC generators.
	 * @return power generated in this DC generator
	 * */
	double getPower(World w, BlockPos p);
}
