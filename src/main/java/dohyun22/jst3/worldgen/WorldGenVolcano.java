package dohyun22.jst3.worldgen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import dohyun22.jst3.blocks.JSTBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;

public class WorldGenVolcano extends WorldGenerator {
	private HashMap mHM = new HashMap();
	private LinkedList mLL = new LinkedList();

	@Override
	public boolean generate(World w, Random r, BlockPos p) {
		int ac = 0;
        int h = w.getHeight(p.getX(), p.getZ());
        
        while (rep(w.getBlockState(new BlockPos(p.getX(), h - 1, p.getZ()))))
        	h--;
        
        for (int n = 0; n < h; n++) {
        	if (w.isAirBlock(new BlockPos(p.getX(), n, p.getZ()))) 
        		ac++;
        	else
        		ac /= 2;
            if (ac > 16)
            	return false;
        }
        
	    int yh;
        for (yh = p.getY(); yh < h; yh++) {
        	BlockPos p2 = new BlockPos(p.getX(), yh, p.getZ());
			w.setBlockState(p2, Blocks.FLOWING_LAVA.getDefaultState());
        	for (EnumFacing d : EnumFacing.HORIZONTALS)
        		w.setBlockState(p2.offset(d), Blocks.STONE.getDefaultState());
        }
        int dr = 3 + r.nextInt(4);
        int sr = r.nextInt(3);
		int num = r.nextInt(32768) + 2048;
        label1 : while (num > 0) {
            int val;
            IBlockState lb;
            while (this.mLL.size() == 0) {
            	BlockPos p3 = new BlockPos(p.getX(), yh, p.getZ());
				w.setBlockState(p3 , Blocks.FLOWING_LAVA.getDefaultState());
                mHM.clear();
                check(p3, dr, r);
                if (yh++ <= 140)
                	continue;
                break label1;
            }
            List ls1 = (List)this.mLL.removeFirst();
            Integer[] ar1 = (Integer[])ls1.toArray();
            
            int wr = (Integer)this.mHM.get(Arrays.asList(ar1[0], ar1[2]));
            for (val = w.getHeight((int)ar1[0].intValue(), (int)ar1[2].intValue()) + 1; val > 0 && this.rep(w.getBlockState(new BlockPos(ar1[0].intValue(), val - 1, ar1[2].intValue()))); val--) {}
            if (val > ar1[1] || !this.rep(lb = w.getBlockState(new BlockPos(ar1[0].intValue(), val, ar1[2].intValue())))) continue;

            remwd(w, new BlockPos(ar1[0], val, ar1[2]));
            
            w.setBlockState(new BlockPos(ar1[0].intValue(), val, ar1[2].intValue()), JSTBlocks.block2.getStateFromMeta(2), 2);
           
            if (ar1[1] > val) wr = Math.max(wr, sr);
            
            check(new BlockPos(ar1[0], val, ar1[2]), wr, r);
            num--;
        }

		int dm = 2 + r.nextInt(3);
		for (int i = 0; i < 2; i++) {
			dm -= i;
			for (int xr = -dm; xr < dm; xr++) {
				for (int zr = -dm; zr < dm; zr++) {
					int ds = xr * xr + zr * zr;
					if (ds < (dm * dm)) {
						BlockPos p3 = new BlockPos(xr + p.getX(), yh - i, zr + p.getZ());
						w.setBlockState(p3, Blocks.FLOWING_LAVA.getDefaultState());
						w.scheduleBlockUpdate(p3, w.getBlockState(p3).getBlock(), 10, 10);
					}
				}
			}
		}
        
		dm = 5 + r.nextInt(8);
		for (int yr = dm; yr > -dm; yr--) {
			int yb = yr + p.getY();
			if (yb <= 0)
				break;

			for (int xr = -dm; xr <= dm; xr++) {
				for (int zr = -dm; zr <= dm; zr++) {
					int ds = xr * xr + yr * yr + zr * zr;
					if (ds < (dm * dm)) {
						BlockPos p4 = new BlockPos(xr + p.getX(), yb, zr + p.getZ());
						if (w.getBlockState(p4).getBlockHardness(w, p4) > 0.0F)
							w.setBlockState(p4, Blocks.LAVA.getDefaultState());
					}
				}
			}
		}
        
        return true;
	}

	private static boolean rep(IBlockState bs) {
	    if (bs instanceof IPlantable)
	    	return true;
	    Material m = bs.getMaterial();
	    if (m == Material.AIR || m == Material.WATER || m == Material.LEAVES || m == Material.WOOD || m == Material.PLANTS || m == Material.VINE || m == Material.SNOW || m == Material.ICE || m == Material.PACKED_ICE) {
	    	return true;
	    }
	    return false;
	}
	
    private static void remwd(World w, BlockPos p) {
        IBlockState b = w.getBlockState(p);
        if (b == Blocks.SNOW) {
            w.setBlockToAir(p);
            return;
        }
        Material m = b.getMaterial();
        if (m == Material.WOOD || m == Material.LEAVES || m == Material.VINE) {
        	w.setBlockToAir(p);
        	remwd(w, p.up());
        }
    }
	
    private void addls(BlockPos p, int w) {
        if (w <= 0) return;
        
        List<Integer> il = Arrays.asList(p.getX(), p.getZ());
        Integer i = (Integer)mHM.get(il);
        
        if (i != null && w <= i) return;
        
        mLL.addLast(Arrays.asList(p.getX(), p.getY(), p.getZ()));
        mHM.put(il, w);
    }

    private void check(BlockPos po, int p, Random r) {
    	for (EnumFacing d : EnumFacing.HORIZONTALS)
    		this.addls(po.offset(d), r.nextInt(2) > 0 ? p - 1 : p);
    }
}
