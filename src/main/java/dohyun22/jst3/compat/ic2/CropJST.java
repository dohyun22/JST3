package dohyun22.jst3.compat.ic2;

import java.util.ArrayList;
import java.util.List;

import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.WeightedItem;
import ic2.api.crops.CropCard;
import ic2.api.crops.CropProperties;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.oredict.OreDictionary;

public class CropJST extends CropJSTBase {
	protected int maxSize, afterHarvestSize, growthSpeed;
	protected Object[] blockBelow;
	protected BiomeDictionary.Type[] biomes;
	protected byte light, glow;
	private String name, discover;
	private ItemStack[] seeds;
	private String[] attribute;
	private CropProperties prop;
	private String migration_id;
	private ArrayList<WeightedItem> drops = new ArrayList();
	

	public CropJST(String name, String disc, int mxs, int ahs, int gs, String[] att, int lvl, int che, int con, int def, int col, int wed) {
		this.name = name;
		this.discover = disc;
		this.maxSize = mxs;
		this.afterHarvestSize = ahs;
		this.growthSpeed = gs == 0 ? 200 : gs;
		this.attribute = att;
		this.prop = new CropProperties(lvl, che, con, def, col, wed);
	}

	public CropJST drop(int w, Object... obj) {
		for (Object o : obj) {
			ItemStack st = null;
			boolean flag = false;
			if (o instanceof ItemStack) { st = (ItemStack)o; flag = true; }
			else if (o instanceof Item) st = new ItemStack((Item)o);
			else if (o instanceof Block) st = new ItemStack((Block)o);
			else if (o instanceof String) st = JSTUtils.getFirstItem((String)o);
			if (st != null && (flag || !st.isEmpty())) drops.add(new WeightedItem(st, w));
		}
		return this;
	}

	public CropJST drop(List<WeightedItem> ls) {
		if (ls != null) drops.addAll(ls);
		return this;
	}

	
	public CropJST mod(int w, String s, int m) {
		ItemStack st = JSTUtils.getModItemStack(s, 1, m);
		if (!st.isEmpty()) drops.add(new WeightedItem(st, w));
		return this;
	}

	public CropJST mod(int w, String s) {
		return mod(w, s, 0);
	}

	public CropJST block(Object... o) {
		blockBelow = o;
		return this;
	}

	public CropJST seed(ItemStack... st) {
		seeds = st;
		return this;
	}

	public CropJST light(int n) {
		light = (byte)n;
		return this;
	}

	public CropJST glow(int n) {
		glow = (byte)n;
		return this;
	}

	public CropJST biome(BiomeDictionary.Type... b) {
		biomes = b;
		return this;
	}

	public CropJST migr(String id, boolean migrate) {
		if (migrate) migration_id = id;
		return this;
	}

	public void fin() {
		Crops.instance.registerCrop(this);
		if (seeds != null && migration_id == null)
			for (ItemStack s : seeds)
				if (s != null && !s.isEmpty())
					Crops.instance.registerBaseSeed(s, this, 1, 1, 1, 1);
		seeds = null;
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
		ItemStack st = WeightedRandom.getRandomItem(cr.getWorldObj().rand, drops).getStack();
		return st.isEmpty() ? null : st;
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
		if (migration_id != null)
			return false;
		if (light != 0) {
			if (light > 0) {
				if (cr.getLightLevel() < light) return false;
			} else {
				if (cr.getLightLevel() > -light) return false;
			}
		}
		if (cr.getCurrentSize() < maxSize - 1)
			return true;
		if (biomes != null) {
			boolean pass = false;
			Biome b = cr.getWorldObj().getBiome(cr.getPosition());
			for (BiomeDictionary.Type t : biomes)
				if (BiomeDictionary.hasType(b, t))
					pass = true;
			if (!pass) return false;
		}
		if (cr.getCurrentSize() == maxSize - 1) {
			if (blockBelow == null || blockBelow.length == 0) return true;
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

	@Override
	public void tick(ICropTile cr) {
		if (migration_id != null) {
			String[] sp = migration_id.split(":");
			try {
				CropCard cc = Crops.instance.getCropCard(sp[0], sp[1]);
				if (cc != null) {
					cr.setCrop(cc);
					CompatIC2.updateCrop((TileEntity)cr);
				}
			} catch (Throwable t) {}
		}
	}

	@Override
	public int getEmittedLight(ICropTile cr) {
		return (cr.getCurrentSize() == maxSize) ? glow : 0;
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
