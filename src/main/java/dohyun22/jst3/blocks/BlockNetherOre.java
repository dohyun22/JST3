package dohyun22.jst3.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class BlockNetherOre extends BlockBase {

	public BlockNetherOre() {
		super("blockno1", Material.ROCK);
		setHardness(3.0F);
		setResistance(5.0F);
		
		for (int i = 0; i < 16; i++) {
			allowedMetas.add(i);
		}
		
	    setHarvestLevel("pickaxe", 0, 0);
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
	    setHarvestLevel("pickaxe", 1, 14);
	    setHarvestLevel("pickaxe", 2, 15);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess w, BlockPos p, IBlockState s, int f) {
		List arr = new ArrayList();
		int m = getMeta(s);
		Random rand = new Random();
		switch (m) {
		case 0:
			arr.add(new ItemStack(Items.COAL, 1 + rand.nextInt(f + 1)));
			break;
		case 1:
			arr.add(new ItemStack(Items.DIAMOND, 1 + rand.nextInt(f + 1)));
			break;
		case 4:
			arr.add(new ItemStack(Items.DYE, 4 + rand.nextInt(5) + rand.nextInt(f + 1), 4));
			break;
		case 5:
			arr.add(new ItemStack(Items.REDSTONE, 4 + rand.nextInt(2) + rand.nextInt(f + 1)));
			break;
		case 8:
			arr.add(new ItemStack(Items.EMERALD, 1 + rand.nextInt(f + 1)));
			break;
		case 12:
			arr.add(new ItemStack(JSTItems.item1, 4 + rand.nextInt(2) + rand.nextInt(f + 1), 27));
			break;
		case 13:
			arr.add(new ItemStack(JSTItems.item1, 1 + rand.nextInt(f + 1), 16));
			break;
		case 14:
			List<ItemStack> sl = OreDictionary.getOres("dustSulfur");
			if (!sl.isEmpty()) {
				arr.add(new ItemStack(sl.get(0).getItem(), 1 +  rand.nextInt(2) + rand.nextInt(f + 1), sl.get(0).getItemDamage()));
				break;
			}
		default:
			arr.add(new ItemStack(this, 1, m));
		}
		return arr;
	}
	
    @Override
    public int getExpDrop(IBlockState bs, IBlockAccess w, BlockPos p, int fortune) {
    	int ret = 0;
    	Random rnd = w instanceof World ? ((World)w).rand : new Random();
    	switch (getMeta(w, p)) {
    	case 0:
    		ret = MathHelper.getInt(rnd, 0, 2);
    		break;
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
    	case 14:
    		ret = JSTUtils.oreValid("dustSulfur") ? MathHelper.getInt(rnd, 1, 3) : 0;
    		break;
    	}
		return ret ;
    }
}
