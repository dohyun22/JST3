package dohyun22.jst3.blocks;

import java.util.ArrayList;
import java.util.Random;

import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOre extends BlockBase {

	protected BlockOre() {
		super("blockore1", MaterialJST.INSTANCE);
		setHardness(3.0F);
		setResistance(5.0F);
		
		for (int i = 0; i < 16; i++) {
			allowedMetas.add(i);
		}
		
	    setHarvestLevel("pickaxe", 100, 0);
	    setHarvestLevel("pickaxe", 100, 1);
	    setHarvestLevel("pickaxe", 100, 2);
	    setHarvestLevel("pickaxe", 3, 3);
	    setHarvestLevel("pickaxe", 2, 4);
	    setHarvestLevel("shovel", -1, 5);
	    setHarvestLevel("pickaxe", 3, 6);
	    setHarvestLevel("pickaxe", 1, 7);
	    setHarvestLevel("shovel", -1, 8);
	    setHarvestLevel("shovel", -1, 9);
	    setHarvestLevel("pickaxe", 1, 10);
	    setHarvestLevel("pickaxe", 2, 11);
	    setHarvestLevel("pickaxe", 2, 12);
	    setHarvestLevel("pickaxe", 2, 13);
	    setHarvestLevel("pickaxe", 2, 14);
	    setHarvestLevel("pickaxe", 2, 15);
	}

	@Override
	public boolean canEntityDestroy(IBlockState s, IBlockAccess w, BlockPos p, Entity e) {
		int m = getMeta(w, p);
		if (m == 0 || m == 1 || m == 2)
			return false;
		return super.canEntityDestroy(s, w, p, e);
	}
	
	public ArrayList<ItemStack> getDrops(IBlockAccess w, BlockPos p, IBlockState s, int f) {
		ArrayList arr = new ArrayList();
		int m = getMeta(s);
		Random rnd = w instanceof World ? ((World)w).rand : new Random();
		switch (m) {
		case 0:
		case 1:
		case 2:
			break;
		case 3: {
			int n = rnd.nextInt(5);
			if (n == 0) {
				arr.add(new ItemStack(JSTItems.item1, 1, 6));
			} else if (n == 1) {
				arr.add(new ItemStack(JSTItems.item1, 1, 7));
			} else if (n == 2) {
				arr.add(new ItemStack(JSTItems.item1, 1, 8));
			} else if (n == 3) {
				arr.add(new ItemStack(JSTItems.item1, 1, 9));
			} else if (n == 4) {
				arr.add(new ItemStack(JSTItems.item1, 1, 10));
			}
			break;
		}
		case 4: {
			arr.add(new ItemStack(JSTItems.item1, 4 + rnd.nextInt(2) + rnd.nextInt(f + 1), 27));
			break;
		}
		case 6: {
			ItemStack st = JSTUtils.getModItemStack("ic2:misc_resource", 1 + rnd.nextInt(f + 1), 1);
			if (!st.isEmpty()) {
				arr.add(st);
			} else {
				arr.add(new ItemStack(JSTItems.item1, 1, 30));
			}
			break;
		}
		case 9: {
			arr.add(new ItemStack(JSTItems.item1, 1 + rnd.nextInt(2) + rnd.nextInt(f + 1), 0));
			break;
		}
		case 13: {
			arr.add(new ItemStack(JSTItems.item1, 1 + rnd.nextInt(f + 1), 16));
			break;
		}
		case 14: {
			arr.add(new ItemStack(JSTItems.item1, 1 + rnd.nextInt(f + 1), 17));
			break;
		}
		case 15: {
			arr.add(new ItemStack(JSTItems.item1, 1 + rnd.nextInt(f + 1), 18));
			break;
		}
		default:
			arr.add(new ItemStack(this, 1, m));
		}
		return arr;
	}
	
	@Override
	public void onBlockAdded(World w, BlockPos p, IBlockState bs) {
		int m = getMeta(bs);
		if (m == 5 || m == 8 || m == 9)
			JSTUtils.tryToFall(w, p);
	}
		  
	@Override
	public void neighborChanged(IBlockState bs, World w, BlockPos p, Block b, BlockPos f) {
		int m = getMeta(w, p);
		if (m == 5 || m == 8 || m == 9)
			JSTUtils.tryToFall(w, p);
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState s, World w, BlockPos p, Random r) {
    	int m = getMeta(s);
    	if (m == 9) {
    		int x = p.getX();
    		int y = p.getX();
    		int z = p.getX();
    		for (int py = y; (w.getBlockState(new BlockPos(x, py + 1, z)).getBlock() == Blocks.WATER) && (py < (8 + y)); py++) {
    			if (r.nextInt(8) == 0) {
        			float a_3 = r.nextFloat() - r.nextFloat();
        			float a_4 = r.nextFloat() - r.nextFloat();
        			float a_5 = r.nextFloat() - r.nextFloat();
    				w.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x + 0.5D + a_3, py + a_4, z + 0.5D + a_5, 0.0D, 0.0D, 0.0D);
    			}
    		}
    	}
    }

	@Override
	public float getExplosionResistance(World w, BlockPos p, Entity e, Explosion x) {
		int m = getMeta(w, p);
		switch (m) {
		case 0:
		case 1:
		case 2:
			return 3600000.0F;
		case 5:
		case 8:
		case 9:
			return 1.0F;
		default:
			return this.blockResistance;
		}
	}

	@Override
	public float getBlockHardness(IBlockState s, World w, BlockPos p) {
		int m = getMeta(s);
		switch (m) {
		case 0:
		case 1:
		case 2:
			return -1.0F;
		case 6:
			return 20.0F;
		case 5:
		case 8:
		case 9:
			return 1.0F;
		default:
			return super.getBlockHardness(s, w, p);
		}
	}

	@Override
	public void onBlockExploded(World w, BlockPos p, Explosion va_5) {
		int m = getMeta(w, p);
		if (m != 0 && m != 1 && m != 2) {
			super.onBlockExploded(w, p, va_5);
		}
	}
	
    @Override
    public SoundType getSoundType(IBlockState bs, World w, BlockPos p, Entity e) {
        switch (getMeta(bs)) {
        case 5:
        case 8:
        case 9:
        	return SoundType.SAND;
        }
        return super.getSoundType(bs, w, p, e);
    }
    
    @Override
    public int getExpDrop(IBlockState bs, IBlockAccess w, BlockPos p, int fortune) {
    	int ret = 0;
    	Random rnd = w instanceof World ? ((World)w).rand : new Random();
    	switch (getMeta(w, p)) {
    	case 3:
    		ret = MathHelper.getInt(rnd, 3, 7);
    		break;
    	case 4:
    	case 9:
    		ret = MathHelper.getInt(rnd, 1, 5);
    		break;
    	case 6:
    		ret = MathHelper.getInt(rnd, 6, 14);
    		break;
    	case 13:
    	case 14:
    	case 15:
    		ret = MathHelper.getInt(rnd, 2, 6);
    		break;
    	}
		return ret;
    }
}
