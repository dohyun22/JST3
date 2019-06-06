package dohyun22.jst3.compat.ic2;

import java.util.List;

import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.CropProperties;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class CropJST extends CropJSTBase {
	private final String name;
	private final String discover;
	private final int maxSize;
	private final int afterHarvestSize;
	private final int growthSpeed;
	private final Object[] blockBelow;
	private final ItemStack[] drops;
	private final String[] attribute;
	private final CropProperties prop;
	
	public CropJST(String name, String disc, int mxs, int ahs, int gs, ItemStack seed, Object[] blb, ItemStack[] drops, String[] att, int lvl, int che, int con, int def, int col, int wed) {
		this.name = name;
		this.discover = disc;
		this.maxSize = mxs;
		this.afterHarvestSize = ahs;
		this.growthSpeed = gs == 0 ? 200 : gs;
		this.drops = drops;
		this.blockBelow = blb;
		this.attribute = att;
		this.prop = new CropProperties(lvl, che, con, def, col, wed);
		Crops.instance.registerCrop(this);
		if (seed != null && !seed.isEmpty()) Crops.instance.registerBaseSeed(seed, this, 1, 1, 1, 1);
		loadTex();
	}

	@Override
	public String getId() {
		return name;
	}

	@Override
	public CropProperties getProperties() {
		return prop;
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public ItemStack getGain(ICropTile cr) {
		if (drops == null || drops.length == 0) return null;
		if (drops.length > 1) {
			int rd = cr.getWorldObj().rand.nextInt(drops.length * 2);
			if (rd > 0 && rd < drops.length)
				return drops[rd] == null || drops[rd].isEmpty() ? null : drops[rd].copy();
		}
	    return drops[0] == null || drops[0].isEmpty() ? null : drops[0].copy();
	}

	@Override
	public String getDiscoveredBy() {
		return discover == null ? super.getDiscoveredBy() : discover;
	}
	
	@Override
	public String[] getAttributes() {
		return attribute;
	}
	
	@Override
	public int getGrowthDuration(ICropTile cr) {
		return growthSpeed < 0 ? -growthSpeed : getProperties().getTier() * growthSpeed;
	}
	
	@Override
	public boolean canGrow(ICropTile cr) {
		if (cr.getCurrentSize() < maxSize - 1)
			return true;
		if (cr.getCurrentSize() == maxSize - 1) {
			if (this.blockBelow == null || this.blockBelow.length == 0) return true;
			World w = cr.getWorldObj();
			BlockPos p = cr.getPosition();
			for (Object o : blockBelow) if (doesMatch(o, w, p.offset(EnumFacing.DOWN, 2))) return true;
		}
		return false;
	}
	
	@Override
	public int getSizeAfterHarvest(ICropTile cr) {
		return afterHarvestSize;
	}
	
	@Override
	public boolean canCross(ICropTile cr) {
		return cr.getCurrentSize() + 2 > getMaxSize();
	}
	
	protected static boolean doesMatch(Object o, World w, BlockPos p) {
		IBlockState bs = w.getBlockState(p);
		if (o instanceof String) {
			ItemStack st = bs.getBlock().getItem(w, p, bs);
			if (st == null || st.isEmpty()) return false;
			return OreDictionary.containsMatch(false, OreDictionary.getOres((String)o), st);
		}
		if (o instanceof ItemStack) {
			Block b = Block.getBlockFromItem(((ItemStack)o).getItem());
			return bs.getBlock() == b && b.getMetaFromState(bs) == ((ItemStack)o).getMetadata();
		}
		if (o instanceof Block) return bs.getBlock() == ((Block)o);
		return false;
	}
}
