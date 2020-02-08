package dohyun22.jst3.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import dohyun22.jst3.evhandler.DustHandler;
import dohyun22.jst3.loader.JSTCfg;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public final class JSTChunkData {
	/** Used for saving chunk data (i.e Oil in chunk, FineDust) */
	private static final Map<Integer, Map<ChunkPos, NBTTagCompound>> DATA_CACHE = new HashMap();
	public static final String FLUID_TAG_NAME = "Fluid", DUST_TAG_NAME = "Dust", BROKEN_TAG_NAME = "Broken";
	public static final List<FRType> FLUID_RESOURCES = new ArrayList();
	public static String[] data;
	
	private JSTChunkData() {}
	
	public static void init() {
		if (data == null) return;
		for (String str : data) {
			// Weight;FluidName;Min;Range;Dims;DimBL;Biomes;BiomeBL
			try {
				String[] ls = str.split(";");
				if (ls.length < 8) {
					String[] ls2 = new String[8];
					for (int n = 0; n < 8; n++)
						ls2[n] = n >= ls.length ? "" : ls[n];
					ls = ls2;
				}
				
				if (ls.length == 8) {
					int weight;
					Fluid f = null;
					
					weight = Integer.parseInt(ls[0]);
					String str2 = ls[1].toLowerCase();
					if (str2.equals("none") || str2.equals("empty")) {
						f = null;
					} else {
						f = FluidRegistry.getFluid(ls[1]);
						if (f == null)
							continue;
					}
					int min = Integer.parseInt(ls[2]);
					int rng = Integer.parseInt(ls[3]);
					String[] strs;
					int[] dim = null;
					if (!"".equals(ls[4])) {
						strs = ls[4].split(",");
						dim = new int[strs.length];
						for (int n = 0; n < strs.length; n++)
							dim[n] = Integer.parseInt(strs[n]);
					}
					
					int[] dimbl = null;
					if (!"".equals(ls[5])) {
						strs = ls[5].split(",");
						dimbl = new int[strs.length];
						for (int n = 0; n < strs.length; n++)
							dimbl[n] = Integer.parseInt(strs[n]);
					}
					
					Object[] biome = null;
					if (!"".equals(ls[6])) {
						strs = ls[6].split(",");
						biome = new Object[strs.length];
						for (int n = 0; n < strs.length; n++) {
							try {
								biome[n] = new Integer(Integer.parseInt(strs[n]));
							} catch (NumberFormatException e) {
								biome[n] = strs[n].toUpperCase();
							}
						}
					}
					
					Object[] biomebl = null;
					if (!"".equals(ls[7])) {
						strs = ls[7].split(",");
						biomebl = new Object[strs.length];
						for (int n = 0; n < strs.length; n++) {
							try {
								biomebl[n] = new Integer(Integer.parseInt(strs[n]));
							} catch (NumberFormatException e) {
								biomebl[n] = strs[n].toUpperCase();
							}
						}
					}
					
					FRType frt = new FRType(weight, f, min, rng, dim, dimbl, biome, biomebl);
					FLUID_RESOURCES.add(frt);
				} else {
					JSTUtils.LOG.error("[JST3] Failed to parse " + str);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** 
	 * @param doGen set true if you're prospecting the Fluid contained in the Chunk
	 * @return Will return {@code FluidStack[]} that contains {@link FluidStack} contained in the specific chunk.<BR>
	 * array with {@code null} FluidStack will be returned if there are no FluidStack at all.<BR>
	 * {@code null} will be returned if the chunk is not prospected.
	 * */
	@Nullable
	public static FluidStack[] getOrCreateFluid(World w, ChunkPos cp, boolean doGen) {
		NBTTagCompound tag = getChunkData(w, cp);
		boolean flag = false;
		if (tag == null) {
			flag = true;
		} else if (tag.hasKey(FLUID_TAG_NAME, Constants.NBT.TAG_COMPOUND)) {
			FluidStack fs = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(FLUID_TAG_NAME));
			return new FluidStack[] {fs};
		} else {
			flag = true;
		}
		
		if (flag && doGen) {
			FluidStack fl = getFluidForChunk(w, cp);
			putFluidData(w, cp, fl, true);
			return new FluidStack[] {fl};
		}
		return null;
	}
	
	public static boolean putFluidData(World w, ChunkPos cp, @Nullable FluidStack fs, boolean create) {
		NBTTagCompound tag = getChunkData(w, cp);
		if (tag == null) {
			if (!create) return false;
			tag = new NBTTagCompound();
		}
		NBTTagCompound tag2 = new NBTTagCompound();
		if (fs != null)
			fs.writeToNBT(tag2);
		tag.setTag(FLUID_TAG_NAME, tag2);
		setChunkData(w, cp, tag, true);
		return true;
	}

	/** @return fine dust level in ng */
	public static int getFineDust(World w, ChunkPos cp) {
		if (JSTCfg.fineDust) {
			NBTTagCompound tag = getChunkData(w, cp);
			if (tag != null)
				return tag.getInteger(DUST_TAG_NAME);
		}
		return 0;
	}

	public static void setFineDust(World w, ChunkPos cp, int ng, boolean tr) {
		if (!JSTCfg.fineDust) return;
		NBTTagCompound tag = getChunkData(w, cp);
		if (tag == null)
			tag = new NBTTagCompound();
		if (ng > 0)
			tag.setInteger(DUST_TAG_NAME, ng);
		else
			tag.removeTag(DUST_TAG_NAME);
		if (tr)
			DustHandler.addToTracker(w, cp, ng > 0);
		setChunkData(w, cp, tag, true);
	}

	public static void addFineDust(World w, ChunkPos cp, int ng, boolean tr) {
		if (ng != 0)
			setFineDust(w, cp, getFineDust(w, cp) + ng, tr);
	}

	@Nullable
	public static NBTTagCompound getChunkData(World w, ChunkPos cp) {
		Map<ChunkPos, NBTTagCompound> map = DATA_CACHE.get(w.provider.getDimension());
		return map == null ? null : map.get(cp);
	}
	
	@Nullable
	public static void setChunkData(World w, ChunkPos cp, NBTTagCompound tag, boolean mark) {
		int dim = w.provider.getDimension();
		Map<ChunkPos, NBTTagCompound> map = DATA_CACHE.get(dim);
		boolean noTag = tag == null || tag.hasNoTags();
		if (noTag) {
			if (map != null) {
				map.remove(cp);
				if (map.isEmpty())
					DATA_CACHE.remove(dim);
			}
		} else {
			if (map == null) {
				map = new HashMap();
				DATA_CACHE.put(dim, map);
			}
			map.put(cp, tag);
		}
		
		if (mark) {
			Chunk c = w.getChunkFromChunkCoords(cp.x, cp.z);
			if (c != null) c.markDirty();
		}
	}

	public static List<BlockPos> getBrokenMachines(World w, ChunkPos cp) {
		NBTTagCompound tag = getChunkData(w, cp);
		List<BlockPos> r = new ArrayList();
		if (tag != null) {
			short[] a = JSTUtils.nbtToShortArray(tag, BROKEN_TAG_NAME);
			for (short s : a) r.add(JSTUtils.backToBlockPos(cp, s));
		}
		return r;
	}

	public static void setBrokenMachine(World w, BlockPos p, boolean fix) {
		ChunkPos cp = new ChunkPos(p);
		NBTTagCompound tag = getChunkData(w, cp);
		if (tag == null)
			tag = new NBTTagCompound();
		short[] a = JSTUtils.nbtToShortArray(tag, BROKEN_TAG_NAME);
		short rp = JSTUtils.toRelativePos(p);
		HashSet<Short> l = new HashSet();
		for (short s : a) l.add(s);
		if (fix) l.remove(rp);
		else l.add(rp);
		if (l.isEmpty())
			tag.removeTag(BROKEN_TAG_NAME);
		else {
			a = new short[l.size()];
			int n = 0;
			for (Short s : l) {
				a[n] = s;
				n++;
			}
			JSTUtils.shortArrayToNBT(tag, a, BROKEN_TAG_NAME);
		}
		setChunkData(w, cp, tag, true);
	}

	@Nullable
	public static FluidStack getFluidForChunk(World w, ChunkPos cp) {
		List<FRType> ls = getFluidsForChunk(w, cp);
		if (ls.isEmpty()) return null;
		long s = 1;
	    s = 23 * s + w.getSeed();
	    s = 23 * s + w.provider.getDimension();
	    s = 23 * s + cp.x;
	    s = 23 * s + cp.z;
		Random r = new Random(s);
		FRType frt = WeightedRandom.getRandomItem(r, ls);
		return frt.getResource(w, cp, r);
	}

	public static List<FRType> getFluidsForChunk(World w, ChunkPos cp) {
		ArrayList r = new ArrayList();
		for (FRType frt : FLUID_RESOURCES)
			if (frt.isRightChunk(w, cp))
				r.add(frt);
		return r;
	}

	public static class FRType extends WeightedRandom.Item {
		public final String fluid;
		public final int min;
		public final int range;
		public final Object[] biomes;
		public final Object[] biomesBL;
		public final int[] dims;
		public final int[] dimsBL;

		public FRType(int weight, String type, int min, int rng, int[] dims, int[] dimbl, Object[] biomes, Object[] biomebl) {
			super(weight);
			this.fluid = type;
			this.min = Math.max(min, 0);
			this.range = Math.max(rng, 0);
			this.dims = dims;
			this.dimsBL = dimbl;
			this.biomes = biomes;
			this.biomesBL = biomebl;
		}
		
		public FRType(int weight, Fluid type, int min, int rng, int[] dims, int[] dimbl, Object[] biomes, Object[] biomebl) {
			this(weight, type == null ? (String)null : FluidRegistry.getFluidName(type), min, rng, dims, dimbl, biomes, biomebl);
		}

		@Override
		public boolean equals(Object tgt) {
			return tgt instanceof Fluid && this.fluid.equals(tgt);
		}
		
		@Nullable
		public FluidStack getResource(World w, ChunkPos cp, Random rnd) {
			if (fluid == null) return null;
			Fluid f = FluidRegistry.getFluid(fluid);
			int amt = min + (range <= 0 ? 0 : rnd.nextInt(range));
			if (f == null || amt <= 0) return null;
			return new FluidStack(f, amt);
		}

		public boolean isRightChunk(World w, ChunkPos p) {
			int dimID = w.provider.getDimension();
			boolean flag1 = true;
			if (dims != null) {
				flag1 = false;
				for (int n : dims) {
					if (n == dimID) {
						flag1 = true;
						break;
					}
				}
			} else if (dimsBL != null) {
				for (int n : dimsBL) {
					if (n == dimID) {
						flag1 = false;
						break;
					}
				}
			}
			
			boolean flag2 = true;
			Biome biome = w.getBiome(new BlockPos(p.x * 16, 64, p.z * 16));
			if (biome != null) {
				if (biomes != null) {
					flag2 = false;
					for (Object obj : biomes) {
						if (matchBiome(obj, biome)) {
							flag2 = true;
							break;
						}
					}
				}
				if (biomesBL != null) {
					for (Object obj : biomesBL) {
						if (matchBiome(obj, biome)) {
							flag2 = false;
							break;
						}
					}
				}
			}
			return flag1 && flag2;
		}
		
		private boolean matchBiome(Object obj, Biome bIn) {
			if (obj instanceof Integer && ((Integer)obj).intValue() == Biome.getIdForBiome(bIn)) {
				return true;
			} else if (obj instanceof String) {
				String s = (String)obj;
				Biome b = Biome.REGISTRY.getObject(new ResourceLocation(s));
				if (b != null && b == bIn) return true;
				s = s.toUpperCase();
				for (BiomeDictionary.Type bt : BiomeDictionary.getTypes(bIn))
					if (s.equals(bt.toString()))
						return true;
			}
			return false;
		}
		
		@Override
		public String toString() {
			String ret = "";
			ret += itemWeight + ";" + fluid + ";" + min + ";" + range + ";" + Arrays.toString(dims == null ? new int[0] : dims) + ";" + Arrays.toString(dimsBL == null ? new int[0] : dimsBL) + ";" + Arrays.toString(biomes == null ? new Object[0] : biomes) + ";" + Arrays.toString(biomesBL == null ? new Object[0] : biomesBL);
			return ret;
		}
	}

	public static void clear() {
		DATA_CACHE.clear();
	}
	
	//Seed: -5061253281198407478
}
