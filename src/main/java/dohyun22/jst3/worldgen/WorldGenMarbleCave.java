package dohyun22.jst3.worldgen;

import java.util.Random;

import dohyun22.jst3.blocks.JSTBlocks;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMarbleCave extends WorldGenerator {

	@Override
	public boolean generate(World w, Random rd, BlockPos p) {
        if (!w.isAirBlock(p)) {
            return false;
        }
        int y = p.getY();
        while (w.getBlockState(new BlockPos(p.getX(), y, p.getZ())).getBlock() != Blocks.STONE) {
            if (y > 100)
                return false;
            ++y;
        }
        p.add(-8, 0, -8);
		return new WorldGenMinableJST(JSTBlocks.block2.getStateFromMeta(6), rd.nextInt(30) + 70, 100000, BlockMatcher.forBlock(Blocks.STONE)).generate(w, rd, new BlockPos(p.getX(), y, p.getZ()));
	}
	
}
