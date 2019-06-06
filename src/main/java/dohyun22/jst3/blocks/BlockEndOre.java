package dohyun22.jst3.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEndOre extends BlockBase {

	public BlockEndOre() {
		super("blockeo1", Material.ROCK);
		setHardness(3.0F);
		setResistance(5.0F);
		
		for (int i = 0; i < 16; i++) {
			allowedMetas.add(i);
		}
		
	    setHarvestLevel("pickaxe", 2, 0);
	    setHarvestLevel("pickaxe", 2, 1);
	    setHarvestLevel("pickaxe", 2, 2);
	    setHarvestLevel("pickaxe", 1, 3);
	    setHarvestLevel("pickaxe", 1, 4);
	    setHarvestLevel("pickaxe", 2, 5);
	    setHarvestLevel("pickaxe", 1, 6);
	    setHarvestLevel("pickaxe", 1, 7);
	    setHarvestLevel("pickaxe", 2, 8);
	    setHarvestLevel("pickaxe", 1, 9);
	    setHarvestLevel("pickaxe", 2, 10);
	    setHarvestLevel("pickaxe", 2, 11);
	    setHarvestLevel("pickaxe", 2, 12);
	    setHarvestLevel("pickaxe", 2, 13);
	    setHarvestLevel("pickaxe", 3, 14);
	    setHarvestLevel("pickaxe", 3, 15);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess w, BlockPos p, IBlockState s, int f) {
		List arr = new ArrayList();
		int m = getMeta(s);
		Random rnd = new Random();
		switch (m) {
		case 1:
			arr.add(new ItemStack(Items.DIAMOND, 1 + rnd.nextInt(f + 1)));
			break;
		case 4:
			arr.add(new ItemStack(Items.DYE, 4 + rnd.nextInt(5) + rnd.nextInt(f + 1), 4));
			break;
		case 5:
			arr.add(new ItemStack(Items.REDSTONE, 4 + rnd.nextInt(2) + rnd.nextInt(f + 1)));
			break;
		case 8:
			arr.add(new ItemStack(Items.EMERALD, 1 + rnd.nextInt(f + 1)));
			break;
		case 12:
			arr.add(new ItemStack(JSTItems.item1, 4 + rnd.nextInt(2) + rnd.nextInt(f + 1), 27));
			break;
		case 13:
			arr.add(new ItemStack(JSTItems.item1, 1 + rnd.nextInt(f + 1), 17));
			break;
		case 15:
			ItemStack st = JSTUtils.getModItemStack("ic2:misc_resource", 1 + rnd.nextInt(f + 1), 1);
			if (!st.isEmpty()) {
				arr.add(st);
			} else {
				arr.add(new ItemStack(this, 1, m));
			}
			break;
		default:
			arr.add(new ItemStack(this, 1, m));
		}
		return arr;
	}
	
	@Override
	public boolean canEntityDestroy(IBlockState s, IBlockAccess w, BlockPos p, Entity e) {
		return !(e instanceof net.minecraft.entity.boss.EntityDragon);
	}
	
	@Override
	public float getBlockHardness(IBlockState s, World w, BlockPos p) {
		int m = getMeta(s);
		switch (m) {
		case 15:
			return 20.0F;
		default:
			return super.getBlockHardness(s, w, p);
		}
	}
	
    @Override
    public int getExpDrop(IBlockState bs, IBlockAccess w, BlockPos p, int fortune) {
    	int ret = 0;
    	Random rnd = w instanceof World ? ((World)w).rand : new Random();
    	switch (getMeta(w, p)) {
    	case 1:
    	case 8:
    		ret = MathHelper.getInt(rnd, 3, 7);
    		break;
    	case 4:
    		ret = MathHelper.getInt(rnd, 2, 5);
    		break;
    	case 5:
    	case 12:
    		ret = MathHelper.getInt(rnd, 1, 5);
    		break;
    	case 13:
    		ret = MathHelper.getInt(rnd, 2, 6);
    		break;
    	case 15:
    		ret = MathHelper.getInt(rnd, 6, 14);
    		break;
    	}
		return ret ;
    }
}
