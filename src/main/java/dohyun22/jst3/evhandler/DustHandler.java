package dohyun22.jst3.evhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.FineDustCapability;
import dohyun22.jst3.api.IDust;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTPotions;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DustHandler {
	private static HashMap<Integer, HashSet<ChunkPos>> pollutedChunks = new HashMap();
	private static final ResourceLocation RL = new ResourceLocation(JustServerTweak.MODID, "dust");
	public static HashMap<Class, Object[]> TEs = new HashMap();

	public static void initTEs() {
		if (JSTCfg.fineDustTEs == null) return;
		RegistryNamespaced<ResourceLocation, Class<? extends TileEntity>> reg = null;
		try {
			reg = (RegistryNamespaced) ReflectionUtils.getFieldObf(TileEntity.class, "field_190562_f", "REGISTRY").get(null);
			if (reg == null) throw new RuntimeException();
		} catch (Throwable t) {
			JSTUtils.LOG.error("Can't get TE registry");
			JSTUtils.LOG.catching(t);
			return;
		}
		
		for (String s : JSTCfg.fineDustTEs) {
			if (s.startsWith("#") || s.equals(""))
				continue;
			String[] arr = s.split(";");
			if (arr.length != 3) {
				JSTUtils.LOG.error("Invalid arguments: " + s);
				continue;
			}
			Class c = reg.getObject(new ResourceLocation(arr[0]));
			if (c == null)
				continue;
			int p;
			try {
				p = Integer.parseInt(arr[1]);
			} catch (NumberFormatException e) {
				JSTUtils.LOG.error("Invalid Number: " + arr[1]);
				continue;
			}
			String[] tags = arr[2].split(",");
			if (tags.length <= 0) {
				JSTUtils.LOG.error("At least 1 tag name required:" + s);
				continue;
			}
			TEs.put(c, new Object[] {Integer.valueOf(p), tags});
		}
		JSTCfg.fineDustTEs = null;
	}

	public static void update(World w, long t) {
		if (t % 1200 == 0) {
			int dim = w.provider.getDimension();
			HashSet<ChunkPos> ls = pollutedChunks.get(dim);
			if (ls == null) return;
			ls = new HashSet(ls);
			HashSet<ChunkPos> ls2 = new HashSet(), ls3 = new HashSet();
			MutableBlockPos mp = new MutableBlockPos();
			int rain = w.isRaining() ? (w.rand.nextInt(4000) + 500) : 0;
			int sl = w.getSeaLevel();
			for (ChunkPos p : ls) {
				int ng = JSTChunkData.getFineDust(w, p);
				if (ng > 0) {
					if (ng <= 10000) {
						if (w.rand.nextInt(ng < 5000 ? 4 : 12) == 0) {
							JSTChunkData.setFineDust(w, p, 0, false);
							ls3.add(p);
						}
					} else if (w.isBlockLoaded(mp.setPos(p.x << 4, 0, p.z << 4))) {
						if (ng >= 80000) {
							AxisAlignedBB ab = new AxisAlignedBB(p.x * 16, 0.0D, p.z * 16, (p.x * 16) + 16, 256.0D, (p.z * 16) + 16);
							int str = getEffectLvl(ng);
							for (EntityLivingBase elb : w.getEntitiesWithinAABB(EntityLivingBase.class, ab)) {
								elb.addPotionEffect(new PotionEffect(JSTPotions.finedust, 1600, str));
								if (elb instanceof EntityPlayer)
									JSTUtils.clearAdvancement((EntityPlayer) elb, JustServerTweak.MODID, "main/finedust");
							}
						}
						if (ng >= 250000)
							for (int f = 0; f < Math.min(ng / 10000, 150); f++)
								affectTerrain(w, mp.setPos((p.x * 16) + w.rand.nextInt(16), sl + (-f + w.rand.nextInt(f * 2 + 1)), (p.z * 16) + w.rand.nextInt(16)), rain > 0);
						if (rain > 0) {
							ng = Math.max(0, ng - rain);
							JSTChunkData.setFineDust(w, p, ng, false);
						}
						spread(w, p, ng, ls2, ls3);
					}
				} else {
					ls3.add(p);
				}
			}
			ls.removeAll(ls3);
			ls.addAll(ls2);
			pollutedChunks.put(dim, ls);
		}
		if (t % 400 == 0) {
			List<TileEntity> tes = w.loadedTileEntityList.stream().filter(te -> te.hasCapability(FineDustCapability.FINEDUST, EnumFacing.UP)).collect(Collectors.toList());
			for (TileEntity te2 : tes) {
                IDust d = te2.getCapability(FineDustCapability.FINEDUST, EnumFacing.UP);
                if (d.canGen()) JSTChunkData.addFineDust(w, new ChunkPos(te2.getPos()), d.getDust() * 20, true);
            }
		}
	}

	private static void affectTerrain(World w, BlockPos p, boolean rain) {
		IBlockState bs = w.getBlockState(p);
		Block bl = bs.getBlock();
		int m = bl.getMetaFromState(bs);
		if (bl == Blocks.AIR || bl == Blocks.DEADBUSH)
			return;
		if (bl == Blocks.LEAVES || bl == Blocks.LEAVES2 || bs.getMaterial() == Material.LEAVES)
			w.setBlockToAir(p);
		if (bl == Blocks.REEDS || bl == Blocks.VINE || bl == Blocks.WATERLILY || bl == Blocks.CACTUS || bl == Blocks.MELON_STEM || bl == Blocks.PUMPKIN_STEM || bl == Blocks.WHEAT || bl == Blocks.POTATOES || bl == Blocks.CARROTS || bl == Blocks.CHORUS_PLANT || bl == Blocks.CHORUS_FLOWER || bl == Blocks.BEETROOTS || bl == Blocks.COCOA || bl == Blocks.RED_FLOWER || bl == Blocks.YELLOW_FLOWER) {
			bl.dropBlockAsItem(w, p, bs, 0);
			w.setBlockToAir(p);
		}
		if (bl == Blocks.TALLGRASS || bl == Blocks.SAPLING || bs.getMaterial() == Material.PLANTS)
			w.setBlockState(p, Blocks.DEADBUSH.getDefaultState());
		if (bl == Blocks.MOSSY_COBBLESTONE)
			w.setBlockState(p, Blocks.COBBLESTONE.getDefaultState());
		if (bl == Blocks.GRASS || bs.getMaterial() == Material.GRASS || bl == Blocks.FARMLAND || bl == Blocks.DIRT)
			w.setBlockState(p, JSTBlocks.block2.getStateFromMeta(0));
		if (bl == Blocks.SAND)
			w.setBlockState(p, JSTBlocks.block2.getStateFromMeta(1));
		if (bl == Blocks.SANDSTONE)
			w.setBlockState(p, JSTBlocks.block2.getStateFromMeta(8));
		if (rain && (bl == Blocks.STONE || bl == Blocks.GRAVEL || bl == Blocks.COBBLESTONE) && w.canBlockSeeSky(p.up())) {
			if (bl == Blocks.STONE)
				w.setBlockState(p, Blocks.COBBLESTONE.getDefaultState());
			else if (bl == Blocks.COBBLESTONE)
				w.setBlockState(p, Blocks.GRAVEL.getDefaultState());
			else if (bl == Blocks.GRAVEL)
				w.setBlockState(p, JSTBlocks.block2.getStateFromMeta(1));
		}
	}

	private static void spread(World w, ChunkPos l, int ng, HashSet<ChunkPos> add, HashSet<ChunkPos> rem) {
		if (ng <= 0) return;
		for (EnumFacing ef : EnumFacing.HORIZONTALS) {
			ChunkPos l2 = new ChunkPos(l.x + ef.getFrontOffsetX(), l.z + ef.getFrontOffsetZ());
			add.add(l2);
			int nd = JSTChunkData.getFineDust(w, l2);
			if (nd * 6 < ng * 5) {
				int d = (ng - nd) / 10;
				nd += d;
				ng -= d;
			}
			JSTChunkData.setFineDust(w, l2, nd, false);
		}
		JSTChunkData.setFineDust(w, l, ng, false);
	}

	public static void addToTracker(World w, ChunkPos cp, boolean b) {
		int dim = w.provider.getDimension();
		HashSet<ChunkPos> ls = pollutedChunks.get(w.provider.getDimension());
		if (b) {
			if (ls == null) {
				ls = new HashSet();
				pollutedChunks.put(dim, ls);
			}
			if (!ls.contains(cp))
				ls.add(cp);
		} else {
			if (ls != null) {
				ls.remove(cp);
				if (ls.isEmpty())
					pollutedChunks.remove(dim);
			}
		}
	}

	public static void resetTracker() {
		pollutedChunks.clear();
	}

	public static int getEffectLvl(int ng) {
		return MathHelper.clamp((ng - 100000) / 100000, 0, 8);
	}

	@SubscribeEvent
    public void onAttach(AttachCapabilitiesEvent<TileEntity> ev) {
		TileEntity te = (TileEntity) ev.getObject();
		Object[] obj = TEs.get(te.getClass());
		if (obj != null) ev.addCapability(RL, new NBTDust(te, (Integer)obj[0], (String[])obj[1]));
	}

	public static class NBTDust implements IDust, ICapabilityProvider {
		private final TileEntity te;
		private final int amt;
		private final String[] tags;

		NBTDust(TileEntity t, int a, String... n) {
			te = t;
			amt = a;
			tags = n;
		}

		@Override
		public boolean canGen() {
			NBTTagCompound tag = te.writeToNBT(new NBTTagCompound());
			int c = 0;
			for (String s : tags) {
				NBTBase st = tag.getTag(s);
				if (st instanceof NBTPrimitive)
					if (((NBTPrimitive)st).getDouble() > 0.0D)
						c++;
				else if (st instanceof NBTTagList || st instanceof NBTTagCompound)
					if (!st.hasNoTags())
						c++;
				else if (st != null) c++;
			}
			return c == tags.length;
		}

		@Override
		public int getDust() {
			return amt;
		}

		@Override
		public boolean hasCapability(Capability<?> c, EnumFacing f) {
			return c == FineDustCapability.FINEDUST;
		}

		@Override
		public <T> T getCapability(Capability<T> c, EnumFacing f) {
			if (c == FineDustCapability.FINEDUST)
				return (T) this;
			return null;
		}
	}
}
