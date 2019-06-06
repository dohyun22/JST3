package dohyun22.jst3.worldgen;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMinableJST extends WorldGenerator {
	private final IBlockState ore;
    private final int num;
    private final int density;
    private final Predicate<IBlockState> rep;

    public WorldGenMinableJST(IBlockState bs, int cnt) {
        this(bs, cnt, 100000, BlockMatcher.forBlock(Blocks.STONE));
    }

    public WorldGenMinableJST(IBlockState bs, int cnt, int d, Predicate<IBlockState> rep) {
        this.ore = bs;
        this.num = cnt;
        this.density = d;
        this.rep = rep;
    }

    @Override
    public boolean generate(World w, Random rnd, BlockPos p) {
    	if (!canReplace(w, p)) return false;
        if (num < 4) {
        	w.setBlockState(p, this.ore, 2);
            for (int i = 1; i < num; i++)
            	w.setBlockState(new BlockPos(p.getX() + rnd.nextInt(2), p.getY() + rnd.nextInt(2), p.getZ() + rnd.nextInt(2)), this.ore, 2);
            return true;
        }
        float f = rnd.nextFloat() * (float)Math.PI;
        double d0 = (double)((float)(p.getX() + 8) + MathHelper.sin(f) * (float)this.num / 8.0F);
        double d1 = (double)((float)(p.getX() + 8) - MathHelper.sin(f) * (float)this.num / 8.0F);
        double d2 = (double)((float)(p.getZ() + 8) + MathHelper.cos(f) * (float)this.num / 8.0F);
        double d3 = (double)((float)(p.getZ() + 8) - MathHelper.cos(f) * (float)this.num / 8.0F);
        double d4 = (double)(p.getY() + rnd.nextInt(3) - 2);
        double d5 = (double)(p.getY() + rnd.nextInt(3) - 2);

        for (int n = 0; n < num; n++) {
            float f1 = (float)n / (float)num;
            double d6 = d0 + (d1 - d0) * (double)f1;
            double d7 = d4 + (d5 - d4) * (double)f1;
            double d8 = d2 + (d3 - d2) * (double)f1;
            double d9 = rnd.nextDouble() * (double)num / 16.0D;
            double d10 = (double)(MathHelper.sin((float)Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (double)(MathHelper.sin((float)Math.PI * f1) + 1.0F) * d9 + 1.0D;
            int j = MathHelper.floor(d6 - d10 / 2.0D);
            int k = MathHelper.floor(d7 - d11 / 2.0D);
            int l = MathHelper.floor(d8 - d10 / 2.0D);
            int i1 = MathHelper.floor(d6 + d10 / 2.0D);
            int j1 = MathHelper.floor(d7 + d11 / 2.0D);
            int k1 = MathHelper.floor(d8 + d10 / 2.0D);

            for (int px = j; px <= i1; px++) {
                double d12 = ((double)px + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D) {
                    for (int py = k; py <= j1; ++py) {
                        double d13 = ((double)py + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int pz = l; pz <= k1; pz++) {
                                double d14 = ((double)pz + 0.5D - d8) / (d10 / 2.0D);

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    BlockPos p2 = new BlockPos(px, py, pz);
                                    if (rnd.nextInt(100000) < this.density && canReplace(w, p2))
                                        w.setBlockState(p2, this.ore, 2);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public boolean canReplace(World w, BlockPos p) {
    	IBlockState bs = w.getBlockState(p);
    	return bs.getBlock().isReplaceableOreGen(bs, w, p, this.rep);
    }
}
