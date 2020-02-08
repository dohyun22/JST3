package dohyun22.jst3.worldgen;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nullable;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.loader.JSTCfg;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

public class JSTWorldgenHandler implements IWorldGenerator {
	private static final ArrayList<OreGen> gens = new ArrayList();
	
	public static class OreGen {
		private int mY;
		private int xY;
		private int cc;
		private int cpc;
		private WorldGenMinableJST wgm;

		private boolean blacklist;
		/** set null to allow all dims. */
		@Nullable
		private int[] allowedDims;

		public OreGen(IBlockState bs, int size, Block rep, int minY, int maxY, int cc, int cpc, int[] allowedDims, boolean bl) {
			this(bs, size, 100000, rep, minY, maxY, cc, cpc, allowedDims, bl);
		}
		
		public OreGen(IBlockState bs, int size, int density, Block rep, int minY, int maxY, int cc, int cpc, int[] allowedDims, boolean bl) {
			this.wgm = new WorldGenMinableJST(bs, size, density, BlockMatcher.forBlock(rep));
			this.mY = minY;
			this.xY = maxY;
			this.cc = cc;
			this.cpc = cpc;
			this.allowedDims = allowedDims;
			this.blacklist = bl;
		}

		public void generate(World w, Random r, int x, int z) {
			for (int i = 0; i < this.cc; i++) {
				if (r.nextInt(10000) < this.cpc) {
					BlockPos p = new BlockPos(x + r.nextInt(16), this.mY + r.nextInt(this.xY - this.mY), z + r.nextInt(16));
					wgm.generate(w, r, p);
				}
			}
		}

		public boolean isAllowed(int dimId) {
			if (allowedDims == null || allowedDims.length <= 0) return true;
			boolean ret = false;
			for (int id : allowedDims) {
				if (id == dimId) {
					ret = true;
					break;
				}
			}
			return blacklist ? !ret  : ret;
		}
	}

	@Override
	public void generate(Random rnd, int cx, int cz, World w, IChunkGenerator icg, IChunkProvider icp) {
		int dim = w.provider.getDimension();
		if (dim  == 0) {
			if (w.getWorldType() != WorldType.FLAT && JSTCfg.volcCnc > 0 && rnd.nextInt(JSTCfg.volcCnc) == 0) {
	            int lx = cx * 16 + 8;
	            int ly = rnd.nextInt(24) + 5;
	            int lz = cz * 16 + 8;
	            new WorldGenVolcano().generate(w, rnd, new BlockPos(lx, ly, lz));
			}
			
			if (JSTCfg.marbCnc > 0 && rnd.nextInt(JSTCfg.marbCnc) == 0) {
				int n = 2 + rnd.nextInt(3);
				for (int a = 0; a < n; a++) {
		            int lx = cx * 16 + 6 + rnd.nextInt(4);
		            int ly = rnd.nextInt(30) + 16;
		            int lz = cz * 16 + 6 + rnd.nextInt(4);
		            new WorldGenMarbleCave().generate(w, rnd, new BlockPos(lx, ly, lz));
		        }
			}
			
			for (int i = 0; i < 2; i++) {
				int x = cx * 16 + rnd.nextInt(14) + 1;
				int y = rnd.nextInt(80);
				int z = cz * 16 + rnd.nextInt(14) + 1;
				if (w.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.STONE) 
					w.setBlockState(new BlockPos(x, y, z), JSTBlocks.blockOre.getStateFromMeta(3), 0);
			}
			
			if (JSTCfg.oilSandCnc > 0 && rnd.nextInt(JSTCfg.oilSandCnc) == 0) {
	            int lx = cx * 16 + 8;
	            int lz = cz * 16 + 8;
	            int ly = w.getHeight(lx, lz) - 2;
	            BlockPos p = new BlockPos(lx, ly, lz);
				if (w.getBlockState(p).getBlock() == Blocks.SAND)
					new WorldGenMinableJST(JSTBlocks.blockOre.getStateFromMeta(5), 36, 100000, BlockMatcher.forBlock(Blocks.SAND)).generate(w, rnd, p);
			}
		} else if (dim == 1) {
			if (JSTCfg.endBedrockOreCnc > 0 && rnd.nextInt(JSTCfg.endBedrockOreCnc) == 0) {
				BlockPos p = new BlockPos(cx * 16 + 8, rnd.nextInt(40) + 10, cz * 16 + 8);
				if (w.getBlockState(p).getBlock() == Blocks.END_STONE) {
					int t = 0;
					switch (rnd.nextInt(4)) {
					case 2:
						t = 1;
						break;
					case 3:
						t = 2;
						break;
					}
					int r = rnd.nextInt(3) + (t > 0 ? 3 : 5);
					for (int x = -r; x <= r; x++) {
						for (int y = -r; y <= r; y++) {
							for (int z = -r; z <= r; z++) {
								int ds = x * x + y * y + z * z;
								if (ds >= (r * r) || rnd.nextInt(4) != 0) continue;
								BlockPos p2 = p.add(x, y, z);
								if (w.getBlockState(p2).getBlock() == Blocks.END_STONE)
									w.setBlockState(p2, rnd.nextInt(4) == 0 ? JSTBlocks.blockOre.getStateFromMeta(t) : Blocks.BEDROCK.getDefaultState());
							}
						}
					}
				}
			}
		}
		
		for (OreGen gen : gens)
			if (gen.isAllowed(w.provider.getDimension())) gen.generate(w, rnd, cx * 16, cz * 16);
	}

	public static void addGen(IBlockState bs, int size, Block rep, int minY, int maxY, int cnt, int chance, int dim) {
		gens.add(new OreGen(bs, size, rep, minY, maxY, cnt, chance, new int[] {dim}, false));
	}
	
	public static void addGen(IBlockState bs, int size, Block rep, int minY, int maxY, int cnt, int chance, int[] dim) {
		gens.add(new OreGen(bs, size, rep, minY, maxY, cnt, chance, dim, false));
	}
	
	public static void addGen(IBlockState bs, int size, Block rep, int minY, int maxY, int cnt, int chance, int[] dim, boolean bl) {
		gens.add(new OreGen(bs, size, rep, minY, maxY, cnt, chance, dim, bl));
	}
}
