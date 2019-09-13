package dohyun22.jst3.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class EffectBlocks {
	public static final Set<Block> netherOres = new HashSet();
	public static final Set<Block> endOres = new HashSet();
	private static final HashMap<Block, int[]> dustBlocks = new HashMap();

	public static void addOre(Block b, boolean end) {
		if (b == null || b == Blocks.AIR) return;
		if (end)
			endOres.add(b);
		else
			netherOres.add(b);
	}

	public static void addDustBlock(Block b, int bm, int ng) {
		if (b == null || b == Blocks.AIR) return;
		if (bm == 0) bm = 0xFFFF;
		dustBlocks.put(b, new int[] {bm, ng});
	}

	public static int getDust(IBlockState bs) {
		if (bs != null) {
			Block b = bs.getBlock();
			int[] i = dustBlocks.get(b);
			if (i != null) {
				int m = b.getMetaFromState(bs);
				if (((i[0] >> m) & 1) == 1)
					return i[1];
			}
		}
		return 0;
	}
}
