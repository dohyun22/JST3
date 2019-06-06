package dohyun22.jst3.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.util.vector.Vector3f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import dohyun22.jst3.compat.CompatBWM;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.api.IItemJEU;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.ISpecialElectricItem;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class JSTUtils {
	/** Speed of light */
	public static final double c = 299792458.0D;
	private static final int[] V = {8, 32, 128, 512, 2048, 8192, 32768, 131072, 524288, Integer.MAX_VALUE};
	public static final Logger LOG = LogManager.getLogger("JST3");
	private JSTUtils() {}
	
	/**
	 * checks player can break block at x,y,z coordinate. 
	 * @return true if player can destroy that block.
	 * */
	public static boolean canPlayerBreakThatBlock (@Nullable EntityPlayer ep, @Nullable BlockPos p) {
		return canPlayerBreakThatBlockInt(ep, p) >= 0;
	}
	
	public static int canPlayerBreakThatBlockInt (@Nullable EntityPlayer ep, @Nullable BlockPos p) {
		if (ep == null || p == null || !ep.world.canMineBlockBody(ep, p))
			return -1;
		if (ep instanceof EntityPlayerMP) {
			return ForgeHooks.onBlockBreakEvent(ep.world, GameType.SURVIVAL, (EntityPlayerMP) ep, p);
		}
		return 0;
	}
	
	@Nullable
	public static EnumFacing determineWrenchingSide(EnumFacing f, float x, float y, float z) {
		if (f == null) return null;
		int n = f.ordinal();
		if (n == 0 || n == 1) {
			if (x < 0.25D) {
				if (z < 0.25D || z > 0.75D) return f.getOpposite();
				return EnumFacing.WEST;
			}
			if (x > 0.75D) {
				if (z < 0.25D || z > 0.75D) return f.getOpposite();
				return EnumFacing.EAST;
			}
			if (z < 0.25D) return EnumFacing.NORTH;
			if (z > 0.75D) return EnumFacing.SOUTH;
			return f;
		} else if (n == 2 || n == 3) {
			if (x < 0.25D) {
				if (y < 0.25D || y > 0.75D) return f.getOpposite();
				return EnumFacing.WEST;
			}
			if (x > 0.75D) {
				if (y < 0.25D || y > 0.75D) return f.getOpposite();
				return EnumFacing.EAST;
			}
	 	    if (y < 0.25D) return EnumFacing.DOWN;
	 	    if (y > 0.75D) return EnumFacing.UP;
	 	    return f;
		} else if (n == 4 || n == 5) {
	    	if (z < 0.25D) {
	    		if (y < 0.25D || y > 0.75D) return f.getOpposite();
	    		return EnumFacing.NORTH;
	    	}
	    	if (z > 0.75D) {
	    		if (y < 0.25D || y > 0.75D) return f.getOpposite();
	    		return EnumFacing.SOUTH;
	    	}
	    	if (y < 0.25D) return EnumFacing.DOWN;
	    	if (y > 0.75D) return EnumFacing.UP;
		}
		return f;
	}

	public static boolean canHarvest(Block b, EntityPlayer pl, IBlockState bs) {
	    ItemStack st = pl.inventory.getCurrentItem();
	    String str = b.getHarvestTool(bs);
	    if (st == null || st.isEmpty() || str == null) {
	    	return pl.canHarvestBlock(bs);
	    }
	    int lv = st.getItem().getHarvestLevel(st, str, pl, bs);
	    if (lv < 0) {
	    	return pl.canHarvestBlock(bs);
	    }
	    return lv >= b.getHarvestLevel(bs);
	}

	public static boolean isOPorSP(Entity e) {
		return isOPorSP(e, true);
	}
	
	public static boolean isOPorSP(Entity e, boolean allowFP) {
		if (!(e instanceof EntityPlayer) || (e instanceof FakePlayer && !allowFP)) return false;
		return e.world.getMinecraftServer().getPlayerList().canSendCommands(((EntityPlayer) e).getGameProfile());
	}

	public static String[] getArrayFromString(String str) {
		if (str == null) return new String[] {""};
		return str.split("<n>");
	}
	
	public static List<String> getListFromTranslation(String key, Object... param) {
		if (JSTUtils.isClient()) return Arrays.asList(getArrayFromString(I18n.format(key, param)));
		return new ArrayList();
	}
	
	public static String translate(String key, Object... obj) {
		try {
			if (JSTUtils.isClient())
				return I18n.format(key, obj);
			return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(key, obj);
		} catch (Throwable t) {}
		return key;
	}

	/** converts numeric distance(in meters) to display(kilometers, megameters etc.) value. 
	 * huge distances over 300Mm will be displayed as Ls(Light-seconds) or even Ly(light-years).
	 * @param l distance in meters.
	 * @param US false: metric units, true: US/imperial unit (feet, mile etc.)
	 * */
	public static String getDist(double l, boolean US) {
		l = Math.abs(l);
		String unit = "";
		
		double[] values = US ? new double[] {31557600 * c, c, 1609.344D, 0.3048D, 0.0254D, 0.0000254D} : new double[] {31557600 * c, c, 1000000.0D, 1000.0D, 1.0D, 0.01D, 0.001D, 0.000001D, 0.000000001D};
		String[] units = US ? new String[] {"Ly", "Ls", "mi", "ft", "in", "mil"} : new String[] {"Ly", "Ls", "Mm", "km", "m", "cm", "mm", "\u03bcm", "nm"};
		
		try {
			for (int n = 0; n < values.length; n++) {
				if (l >= values[n]) {
					l /= values[n];
					unit = units[n];
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (unit.equals("")) unit = US ? "ft" : "m";
		
		return String.format("%.3f", l).replace("E", "\u00d710^") + unit;
	}
	
	public static int getVoltFromTier(int tier) {
		return getVoltFromTier(tier, false);
	}
	
	public static int getTierFromVolt(int volt) {
		return getTierFromVolt(volt, false);
	}
	
	public static String getTierNameFromVolt(int tier) {
		return getTierNameFromVolt(tier, false);
	}
	
	public static String getTierNameFromVolt(int tier, boolean isIC2) {
		return getTierName(getTierFromVolt(tier, isIC2));
	}
	
	public static String getTierName(int tier) {
		if (JSTUtils.isClient())
			return I18n.format("jst.tooltip.energy.tier." + MathHelper.clamp(tier, 0, 9));
		if (tier < 0) tier = 0;
		if (tier > 9) tier = 9;
		switch (tier) {
		case 0:
			return "ULV";
		case 1:
			return "LV";
		case 2:
			return "MV";
		case 3:
			return "HV";
		case 4:
			return "EV";
		case 5:
			return "IV";
		case 6:
			return "LuV";
		case 7:
			return "ZPMV";
		case 8:
			return "UV";
		case 9:
			return "MAXV";
		}
		return "UNKNOWN";
	}
	
	public static int getVoltFromTier(int tier, boolean isIC2) {
		if (tier <= 0 && isIC2) return 5;
		return V[Math.max(Math.min(tier, V.length - 1), 0)];
	}
	
	public static int getTierFromVolt(int volt, boolean isIC2) {
		if (isIC2) {
			if (volt <= 5)
				return 0;
			else if (volt < 32)
				return 1;
		}
		for (int n = 0; n < V.length; n++) {
			if (volt <= V[n]) {
				return n;
			}
		}
		return 0;
	}
	
	public static int getNearestVolt(int volt) {
		for (int n = 0; n < V.length; n++)
			if (volt <= V[n])
				return V[n];
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public static BakedQuad createBakedQuad(VertexFormat format, Vector3f[] vertices, EnumFacing facing,
			TextureAtlasSprite sprite, float[] colour, boolean invert, float[] alpha) {
		return createBakedQuad(format, vertices, facing, sprite, new double[] { 0.0D, 0.0D, 16.0D, 16.0D }, colour,
				invert, alpha);
	}

	@SideOnly(Side.CLIENT)
	public static BakedQuad createBakedQuad(VertexFormat format, Vector3f[] vertices, EnumFacing facing,
			TextureAtlasSprite sprite, double[] uvs, float[] colour, boolean invert) {
		return createBakedQuad(format, vertices, facing, sprite, uvs, colour, invert,
				new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
	}

	@SideOnly(Side.CLIENT)
	public static BakedQuad createBakedQuad(VertexFormat vf, Vector3f[] v3, EnumFacing f, TextureAtlasSprite tex,
			double[] uvs, float[] col, boolean inv, float[] alp) {
		return createBakedQuad(vf, v3, f, tex, uvs, col, inv, alp, null);
	}

	@SideOnly(Side.CLIENT)
	public static BakedQuad createBakedQuad(VertexFormat vf, Vector3f[] v3, EnumFacing f, TextureAtlasSprite tex,
			double[] uvs, float[] col, boolean inv, float[] alp, BlockPos pos) {
		UnpackedBakedQuad.Builder ubq = new UnpackedBakedQuad.Builder(vf);
		ubq.setQuadOrientation(f);
		ubq.setTexture(tex);
		OBJModel.Normal fn = new OBJModel.Normal((float) f.getDirectionVec().getX(), (float) f.getDirectionVec().getY(), (float) f.getDirectionVec().getZ());
		int vId = inv ? 3 : 0;
		int u = (vId > 1) ? 2 : 0;
		putVertexData(vf, ubq, v3[vId], fn, uvs[u], uvs[1], tex, col, alp[inv ? 3 : 0]);
		vId = (inv ? 2 : 1);
		u = ((vId > 1) ? 2 : 0);
		putVertexData(vf, ubq, v3[inv ? 2 : 1], fn, uvs[u], uvs[3], tex, col, alp[inv ? 2 : 1]);
		vId = (inv ? 1 : 2);
		u = ((vId > 1) ? 2 : 0);
		putVertexData(vf, ubq, v3[inv ? 1 : 2], fn, uvs[u], uvs[3], tex, col, alp[inv ? 1 : 2]);
		vId = (inv ? 1 : 3);
		u = ((vId > 1) ? 2 : 0);
		putVertexData(vf, ubq, v3[inv ? 0 : 3], fn, uvs[u], uvs[1], tex, col, alp[inv ? 0 : 3]);
		BakedQuad tmp = (BakedQuad) ubq.build();
		return tmp;
	}

	@SideOnly(Side.CLIENT)
	private static void putVertexData(VertexFormat format, UnpackedBakedQuad.Builder builder, Vector3f pos,
			OBJModel.Normal faceNormal, double u, double v, TextureAtlasSprite sprite, float[] colour, float alpha) {
		for (int e = 0; e < format.getElementCount(); e++) {
			switch (format.getElement(e).getUsage()) {
			case POSITION:
				builder.put(e, new float[] { pos.getX(), pos.getY(), pos.getZ(), 0.0F });
				break;
			case COLOR:
				float d = 1.0F;
				builder.put(e, new float[] { d * colour[0], d * colour[1], d * colour[2], 1.0F * colour[3] * alpha });
				break;
			case UV:
				if (sprite == null) {
					sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
				}
				builder.put(e, new float[] { sprite.getInterpolatedU(u), sprite.getInterpolatedV(v), 0.0F, 1.0F });

				break;
			case NORMAL:
				builder.put(e, new float[] { faceNormal.x, faceNormal.y, faceNormal.z, 0.0F });
				break;
			default:
				builder.put(e, new float[0]);
			}
		}
	}
	
	public static List<BakedQuad> makeCubeAABB(TextureAtlasSprite[] texs, AxisAlignedBB aabb) {
		return JSTUtils.makeCube(texs, (float)aabb.minX, (float)aabb.minY, (float)aabb.minZ, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ);
	}
	
	@SideOnly(Side.CLIENT)
	public static List<BakedQuad> makeFullCube(TextureAtlasSprite[] texs) {
		return JSTUtils.makeCube(texs, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@SideOnly(Side.CLIENT)
	public static List<BakedQuad> makeCube(TextureAtlasSprite[] texs, float c0, float c1, float c2, float c3, float c4, float c5) {
		List<BakedQuad> quads = new ArrayList(6);
		float[] col = { 1.0F, 1.0F, 1.0F, 1.0F };
	    Vector3f[] vertices = { new Vector3f(c0, c1, c2), new Vector3f(c0, c1, c5), new Vector3f(c3, c1, c5), new Vector3f(c3, c1, c2) };
	    quads.add(JSTUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.DOWN, texs[0], new double[] { c0 * 16, 16 - c2 * 16, c3 * 16, 16 - c5 * 16 }, col, true));
	    vertices = new Vector3f[] { new Vector3f(c0, c4, c2), new Vector3f(c0, c4, c5), new Vector3f(c3, c4, c5), new Vector3f(c3, c4, c2) };
	    quads.add(JSTUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.UP, texs[1], new double[] { c0 * 16, c2 * 16, c3 * 16, c5 * 16 }, col, false));
	    
	    vertices = new Vector3f[] { new Vector3f(c3, c1, c2), new Vector3f(c3, c4, c2), new Vector3f(c0, c4, c2), new Vector3f(c0, c1, c2) };
	    quads.add(JSTUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.NORTH, texs[2], new double[] { 16 - c3 * 16, 16 - c1 * 16, 16 - c0 * 16, 16 - c4 * 16 }, col, true));
	    vertices = new Vector3f[] { new Vector3f(c3, c1, c5), new Vector3f(c3, c4, c5), new Vector3f(c0, c4, c5), new Vector3f(c0, c1, c5) };
	    quads.add(JSTUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.SOUTH, texs[3], new double[] { c3 * 16, 16 - c1 * 16, c0 * 16, 16 - c4 * 16 }, col, false));
	    
	    vertices = new Vector3f[] { new Vector3f(c0, c1, c2), new Vector3f(c0, c4, c2), new Vector3f(c0, c4, c5), new Vector3f(c0, c1, c5) };
	    quads.add(JSTUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.WEST, texs[4], new double[] { c2 * 16, 16 - c1 * 16, c5 * 16, 16 - c4 * 16 }, col, true));
	    vertices = new Vector3f[] { new Vector3f(c3, c1, c2), new Vector3f(c3, c4, c2), new Vector3f(c3, c4, c5), new Vector3f(c3, c1, c5) };
	    quads.add(JSTUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.EAST, texs[5], new double[] { 16 - c2 * 16, 16 - c1 * 16, 16 - c5 * 16, 16 - c4 * 16 }, col, false));
		return quads;
	}

	@Nullable
	public static EnumFacing getFacingFromNum(byte n) {
		if (n < 0 || n >= EnumFacing.values().length)
			return null;
		return EnumFacing.values()[n];
	}
	
	@Nullable
	public static EnumFacing getOppositeFacing(byte n) {
		EnumFacing ret = getFacingFromNum(n);
		return ret == null ? null : ret.getOpposite();
	}
	
	@Nullable
	public static EnumFacing getOppositeFacing(@Nullable EnumFacing f) {
		if (f == null) return null;
		return f.getOpposite();
	}
	
	@Nonnull
	public static BlockPos getOffset(@Nonnull BlockPos p, @Nullable EnumFacing f, int num) {
		if (f == null || num == 0) return p;
		return p.offset(f, num);
	}
	
	public static byte getNumFromFacing(EnumFacing facing) {
		if (facing == null)
			return -1;
		return (byte)(facing.ordinal());
	}

	public static EnumFacing getClosestSide(BlockPos p, EntityLivingBase elb, ItemStack st, boolean nswe) {
		if (elb != null) {
			int n = MathHelper.floor(elb.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
	        int n2 = Math.round(elb.rotationPitch);
	        if (n2 >= 65 && !nswe) {
	        	return EnumFacing.UP;
	        } else if (n2 <= -65 && !nswe) {
	        	return EnumFacing.DOWN;
	        } else {
	        	switch (n) {
	        	case 0: 
	        		return EnumFacing.NORTH;
	        	case 1: 
	        		return EnumFacing.EAST;
	        	case 2: 
	        		return EnumFacing.SOUTH;
	        	case 3: 
	        		return EnumFacing.WEST;
	        	}
	        }
		}
		return nswe ? EnumFacing.NORTH : EnumFacing.UP;
	}

	public static int convLongToInt(long input) {
		return (int)Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, input));
	}

	/** Multiplies two long integers without over/underflow */
	public static long safeMultiplyLong(long a, long b) {
		long max = Long.signum(a) == Long.signum(b) ? Long.MAX_VALUE : Long.MIN_VALUE;
		if (a != 0 && (b > 0 && b > max / a || b < 0 && b < max / a)) {
			return max;
		}
		return a * b;
	}
	
	/** Adds two long integers without overflow */
	public static long safeAddLong(long a, long b) {
		long max = Long.signum(a) == Long.signum(b) ? Long.MAX_VALUE : Long.MIN_VALUE;
		if (a != 0 && (b > 0 && b > max - a || b < 0 && b < max - a))
			return max;
		return a + b;
	}

	public static void tryToFall(World w, BlockPos p) {
		if ((w.isAirBlock(p.down()) || BlockFalling.canFallThrough(w.getBlockState(p.down()))) && p.getY() >= 0) {
            if (!BlockFalling.fallInstantly && w.isAreaLoaded(p.add(-32, -32, -32), p.add(32, 32, 32))) {
                if (!w.isRemote) {
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(w, (double)p.getX() + 0.5D, (double)p.getY(), (double)p.getZ() + 0.5D, w.getBlockState(p));
                    w.spawnEntity(entityfallingblock);
                }
            } else {
                IBlockState state = w.getBlockState(p);
                w.setBlockToAir(p);
                BlockPos p2;
                for (p2 = p.down(); (w.isAirBlock(p2) || BlockFalling.canFallThrough(w.getBlockState(p2))) && p2.getY() > 0; p2 = p2.down()) {
                    ;
                }

                if (p2.getY() > 0) {
                    w.setBlockState(p2.up(), state);
                }
            }
		}
	}
	
	public static int fillTank (World w, BlockPos p, EnumFacing f, FluidStack fs) {
	    TileEntity te = w.getTileEntity(p.offset(f));
	    if (te == null || fs == null) return 0;
	    
	    if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite())) {
	    	net.minecraftforge.fluids.capability.IFluidHandler fh = (net.minecraftforge.fluids.capability.IFluidHandler)te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite());
	        if (fh != null)
	        	return fh.fill(fs, true);
	    }
	    return 0;
	}

	public static boolean isClient() {
		return FMLCommonHandler.instance().getEffectiveSide().isClient();
	}

	public static boolean oreValid(String name) {
		return !OreDictionary.getOres(name).isEmpty();
	}
	
	public static boolean oreValid(OreDictStack in) {
		return in != null && in.count > 0 && oreValid(in.name);
	}

	public static EnumHand getOppositeHand(@Nonnull EnumHand h) {
		return h == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
	}

	public static void sendMessage(EntityPlayer pl, String key, Object... obj) {
		if (pl != null) pl.sendMessage(new TextComponentTranslation(key, obj));
	}
	
	public static void sendSimpleMessage(EntityPlayer pl, String msg) {
		if (pl != null) pl.sendMessage(new TextComponentString(msg));
	}
    
    /** Used for getting Object from array without Exception. */
    @Nullable
    public static <T> T getObjFromArray(T[] array, int idx) {
    	if (array == null || idx < 0 || idx >= array.length) return (T)null;
		return array[idx];
    }
    
    //Item Related
	@Nonnull
	public static NBTTagCompound getOrCreateNBT(ItemStack st) {
		NBTTagCompound tag = st.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			st.setTagCompound(tag);
		}
		return tag;
	}
	
	public static void dropAll(World w, BlockPos p, List<ItemStack> in) {
		for (ItemStack st : in)
			dropEntityItemInPos(w, p, st);
	}

	public static boolean dropEntityItemInPos (World w, BlockPos p, ItemStack st) {
		double rng = 0.7D;
		double dx = w.rand.nextFloat() * rng + (1.0D - rng) * 0.5D;
		double dy = w.rand.nextFloat() * rng + (1.0D - rng) * 0.5D;
		double dz = w.rand.nextFloat() * rng + (1.0D - rng) * 0.5D;
		return dropEntityItemInCoord(w, p.getX() + dx, p.getY() + dy, p.getZ() + dz, st, true);
	}
	
	public static boolean dropEntityItemInCoord (World w, double x, double y, double z, ItemStack st, boolean delay) {
		if (st == null || st.isEmpty()) return false;
		EntityItem ei = new EntityItem(w, x, y, z, st.copy());
		if (delay) ei.setDefaultPickupDelay();
		w.spawnEntity(ei);
		return true;
	}
	
	@Nonnull
	public static Block getModBlock(String id) {
		if (id == null || id.equals("")) return Blocks.AIR;
		return Block.REGISTRY.getObject(new ResourceLocation(id));
	}
	
	@Nullable
	public static Item getModItem(String id) {
		if (id == null || id.equals("")) return null;
		return Item.REGISTRY.getObject(new ResourceLocation(id));
	}
	
	@Nonnull
	public static ItemStack getModItemStack(String id, int num, int meta, @Nullable NBTTagCompound tag) {
		Item it = getModItem(id);
		if (it != null) {
			ItemStack ret = new ItemStack(it, num, meta);
			if (tag != null) ret.setTagCompound(tag);
			return ret;
		}
		return ItemStack.EMPTY;
	}
	
	@Nonnull
	public static ItemStack getModItemStack(String id, int num, int meta) {
		return getModItemStack(id, num, meta, null);
	}
	
	@Nonnull
	public static ItemStack getModItemStack(String id, int amount) {
		return getModItemStack(id, amount, 0, null);
	}
	
	@Nonnull
	public static ItemStack getModItemStack(String id) {
		return getModItemStack(id, 1, 0, null);
	}
	
	@Nonnull
	public static ItemStack getFirstItem(String ore, int sz) {
		List<ItemStack> st = OreDictionary.getOres(ore);
		if (st == null || st.size() <= 0) return ItemStack.EMPTY;
		ItemStack ret = st.get(0).copy();
		ret.setCount(sz);
		return ret;
	}
	
	@Nonnull
	public static ItemStack getFirstItem(String ore) {
		return getFirstItem(ore, 1);
	}

	public static ItemStack getValidOne(String... ores) {
		for (String s : ores) {
			ItemStack ret = getFirstItem(s, 1);
			if (!ret.isEmpty())
				return ret;
		}
		return ItemStack.EMPTY;
	}
	
	public static ItemStack getFirstOrSecond(String ore, int cnt, Object obj) {
		ItemStack ret = getFirstItem(ore, cnt);
		if (ret.isEmpty()) {
			if (obj instanceof ItemStack)
				return (ItemStack) obj;
			if (obj instanceof String)
				return getFirstItem((String) obj, cnt);
			if (obj instanceof OreDictStack)
				return getFirstItem(((OreDictStack)obj).name, ((OreDictStack)obj).count);
		}
		return ret;
	}
	
	@Nonnull
	public static String getItemID(Item i) {
		if (i == null) return "";
		Object rl = Item.REGISTRY.getNameForObject(i);
		return rl == null ? "" : rl .toString();
	}

	public static long chargeItem(ItemStack st, long eu, int tier, boolean itl, boolean sim) {
		if (st == null || st.isEmpty()) return 0;
		int fe = JSTUtils.convLongToInt(eu * JSTCfg.RFPerEU);
		Item i = st.getItem();
		if (i instanceof IItemJEU)
			return ((IItemJEU)i).injectEU(st, eu, tier, itl, sim);
		if (st.hasCapability(CapabilityEnergy.ENERGY, null))
			return st.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(fe, sim) / JSTCfg.RFPerEU;
	    try {
			if (JSTCfg.ic2Loaded && (i instanceof ISpecialElectricItem || i instanceof IElectricItem || ElectricItem.getBackupManager(st) != null))
				return (long) ElectricItem.manager.charge(st, eu, tier, false, sim);
		} catch (Throwable t) {}
		return 0;
	}
	
	public static long dischargeItem(ItemStack st, long eu, int tier, boolean itl, boolean sim) {
		if (st == null || st.isEmpty()) return 0;
		int fe = JSTUtils.convLongToInt(eu * JSTCfg.RFPerEU);
		Item i = st.getItem();
		if (i instanceof IItemJEU)
			return ((IItemJEU)i).extractEU(st, eu, tier, itl, sim);
		if (st.hasCapability(CapabilityEnergy.ENERGY, null))
			return st.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(fe, sim) / JSTCfg.RFPerEU;
		try {
			if (JSTCfg.ic2Loaded && (i instanceof ISpecialElectricItem || i instanceof IElectricItem || ElectricItem.getBackupManager(st) != null))
				return (long) ElectricItem.manager.discharge(st, eu, tier, itl, true, sim);
		} catch (Throwable t) {}
		return 0;
	}
	
	public static long getEUInItem(ItemStack st) {
		if (st == null || st.isEmpty()) return 0;
		Item i = st.getItem();
		if (i instanceof IItemJEU)
			return ((IItemJEU)i).getEU(st);
		if (st.hasCapability(CapabilityEnergy.ENERGY, null))
			return ((net.minecraftforge.energy.IEnergyStorage)st.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored() / JSTCfg.RFPerEU;
		try {
			if (JSTCfg.ic2Loaded && (i instanceof ISpecialElectricItem || i instanceof IElectricItem || ElectricItem.getBackupManager(st) != null))
				return (long) ElectricItem.manager.getCharge(st);
		} catch (Throwable t) {}
		return 0;
	}
	
	public static long getMaxEUInItem(ItemStack st) {
		if (st == null || st.isEmpty()) return 0;
		Item i = st.getItem();
		if (i instanceof IItemJEU)
			return ((IItemJEU)i).getMaxEU(st);
		if (st.hasCapability(CapabilityEnergy.ENERGY, null))
			return ((net.minecraftforge.energy.IEnergyStorage)st.getCapability(CapabilityEnergy.ENERGY, null)).getMaxEnergyStored() / JSTCfg.RFPerEU;
		try {
			if (JSTCfg.ic2Loaded && (i instanceof ISpecialElectricItem || i instanceof IElectricItem || ElectricItem.getBackupManager(st) != null))
				return (long) ElectricItem.manager.getMaxCharge(st);
		} catch (Throwable t) {}
		return 0;
	}

	public static long sendEnergy(World w, BlockPos p, EnumFacing f, long eu, boolean sim) {
		return sendEnergy(w.getTileEntity(p), f, eu, sim);
	}

	public static long sendEnergy(TileEntity te, EnumFacing f, long eu, boolean sim) {
	    if (eu <= 0 || te == null || f == null) return 0;
	    if (te instanceof TileEntityMeta && ((TileEntityMeta)te).hasValidMTE())
	    	return ((TileEntityMeta)te).mte.injectEnergy(f, eu, sim);
	    int rf = JSTUtils.convLongToInt(eu * JSTCfg.RFPerEU);
	    if (te.hasCapability(CapabilityEnergy.ENERGY, f)) {
	    	long ret = ((net.minecraftforge.energy.IEnergyStorage)te.getCapability(CapabilityEnergy.ENERGY, f)).receiveEnergy(rf, sim);
	    	if (ret % JSTCfg.RFPerEU > 0) ret++;
	    	return ret / JSTCfg.RFPerEU;
	    }
	    if (JSTCfg.ic2Loaded) {
			try {
				IEnergyTile et = EnergyNet.instance.getSubTile(te.getWorld(), te.getPos());
				if (et instanceof IEnergySink && ((IEnergySink)et).acceptsEnergyFrom(null, f)) {
					long e = Math.max(0, Math.min((long)((IEnergySink)et).getDemandedEnergy(), Math.min(JSTUtils.getVoltFromTier(((IEnergySink)et).getSinkTier(), true), eu)));
					if (!sim) ((IEnergySink)et).injectEnergy(f, e, e);
					return e;
				}
			} catch (Throwable t) {}
		}
	    return 0L;
	}
	
    /** Will return null if failed */
    @Nullable
	public static ItemStack drainFluidItem(IFluidHandler fh, int amt, ItemStack in, ItemStack out, @Nullable EntityPlayer pl) {
		if (in == null || in.isEmpty()) return null;
		if (in.hasTagCompound() && in.getTagCompound().hasNoTags()) in.setTagCompound(null);
		FluidActionResult far = FluidUtil.tryEmptyContainer(in, fh, amt, pl, false);
		if (far.isSuccess()) {
			ItemStack ret = far.getResult();
			if (out.isEmpty() || canCombine(out, ret)) {
				if (!out.isEmpty() && out.getCount() + ret.getCount() > out.getMaxStackSize()) return null;
				far = FluidUtil.tryEmptyContainer(in, fh, amt, pl, true);
				if (far.isSuccess()) return far.getResult();
			}
		}
		return null;
	}
    
    /** Will return null if failed */
    @Nullable
	public static ItemStack fillFluidItem(IFluidHandler fh, int amt, ItemStack in, ItemStack out, @Nullable EntityPlayer pl) {
		if (in == null || in.isEmpty()) return null;
		FluidStack fs = fh.drain(amt, false);
		if (fs == null) return null;
		if (in.hasTagCompound() && in.getTagCompound().hasNoTags()) in.setTagCompound(null);
		FluidActionResult result = FluidUtil.tryFillContainer(in, fh, amt, pl, false);
		if (result.isSuccess()) {
			ItemStack full = result.getResult();
			if (out.isEmpty() || canCombine(out, full)) {
				if (!out.isEmpty() && (out.getCount() + full.getCount() > out.getMaxStackSize())) return null;
				result = FluidUtil.tryFillContainer(in, fh, fs.amount, pl, true);
				if (result.isSuccess()) return result.getResult();
			}
		}
		return null;
	}
	
	public static boolean drainFluidItemInv(IFluidHandler fh, int amt, IInventory inv, int in, int out) {
		ItemStack st = drainFluidItem(fh, amt, inv.getStackInSlot(in), inv.getStackInSlot(out), null);
		if (st != null) {
			if (!st.isEmpty()) {
				if (!(inv.getStackInSlot(out)).isEmpty() && canCombine(inv.getStackInSlot(out), st)) {
					(inv.getStackInSlot(out)).grow(st.getCount());
				} else if (inv.getStackInSlot(out).isEmpty()) {
					inv.setInventorySlotContents(out, st.copy());
				}
			}
			(inv.getStackInSlot(in)).shrink(1);
			if ((inv.getStackInSlot(in)).getCount() <= 0) inv.setInventorySlotContents(in, ItemStack.EMPTY);
			return true;
	    }
		return false;
	}
	
	public static boolean fillFluidItemInv(IFluidHandler fh, int amt, IInventory inv, int in, int out) {
		ItemStack st = fillFluidItem(fh, amt, inv.getStackInSlot(in), inv.getStackInSlot(out), null);
		if (st != null) {
			if (!st.isEmpty()) {
				if (!(inv.getStackInSlot(out)).isEmpty() && canCombine(inv.getStackInSlot(out), st)) {
					(inv.getStackInSlot(out)).grow(st.getCount());
				} else if (inv.getStackInSlot(out).isEmpty()) {
					inv.setInventorySlotContents(out, st.copy());
				}
			}
			(inv.getStackInSlot(in)).shrink(1);
			if ((inv.getStackInSlot(in)).getCount() <= 0) inv.setInventorySlotContents(in, ItemStack.EMPTY);
			return true;
	    }
		return false;
	}
	
	public static boolean checkInventoryFull(IInventory in, EnumFacing side) {
		if (in instanceof ISidedInventory) {
			ISidedInventory sinv = (ISidedInventory) in;
			int[] a = sinv.getSlotsForFace(side);
			for (int k : a) {
				ItemStack st2 = sinv.getStackInSlot(k);
				if (st2.isEmpty() || st2.getCount() != st2.getMaxStackSize()) return false;
			}
		} else {
			int i = in.getSizeInventory();
			for (int j = 0; j < i; ++j) {
				ItemStack st = in.getStackInSlot(j);
				if (st.isEmpty() || st.getCount() != st.getMaxStackSize()) return false;
			}
		}
		return true;
	}
	
	/**
	 * Can insert the specified item from the specified slot on the specified side?
	 */
	public static boolean canInsertItemInSlot(IInventory inv, ItemStack st, int idx, EnumFacing s) {
		return !inv.isItemValidForSlot(idx, st) ? false : !(inv instanceof ISidedInventory) || ((ISidedInventory) inv).canInsertItem(idx, st, s);
	}

	/**
	 * Can extract the specified item from the specified slot on the specified side?
	 */
	public static boolean canExtractItemFromSlot(IInventory in, ItemStack st, int idx, EnumFacing s) {
		return !(in instanceof ISidedInventory) || ((ISidedInventory) in).canExtractItem(idx, st, s);
	}
	
	/**
	 * Insert the specified stack to the specified inventory and return any leftover
	 * items
	 */
	public static ItemStack insertStack(IInventory tg, ItemStack st, int idx, EnumFacing f) {
		ItemStack st2 = tg.getStackInSlot(idx);

		if (canInsertItemInSlot(tg, st, idx, f)) {
			boolean flag = false;

			if (st2.isEmpty()) {
				tg.setInventorySlotContents(idx, st);
				st = ItemStack.EMPTY;
				flag = true;
			} else if (canCombine(st2, st)) {
				int i = st.getMaxStackSize() - st2.getCount();
				int j = Math.min(st.getCount(), i);
				st.shrink(j);
				st2.grow(j);
				flag = j > 0;
			}

			if (flag) tg.markDirty();
		}

		return st;
	}
	
    public static boolean canCombine(ItemStack st1, ItemStack st2) {
        return st1.getItem() != st2.getItem() ? false : (st1.getMetadata() != st2.getMetadata() ? false : (st1.getCount() > st1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(st1, st2)));
    }
    
	public static ItemStack sendStackToInv(IInventory tg, ItemStack st, @Nullable EnumFacing f) {
		if (tg instanceof ISidedInventory && f != null) {
			ISidedInventory sinv = (ISidedInventory) tg;
			int[] arr = sinv.getSlotsForFace(f);
			for (int n = 0; n < arr.length && !st.isEmpty(); ++n)
				st = insertStack(tg, st, arr[n], f);
		} else {
			int o = tg.getSizeInventory();
			for (int p = 0; p < o && !st.isEmpty(); ++p)
				st = insertStack(tg, st, p, f);
		}
		return st;
	}

	public static void setEnchant(@Nullable Enchantment en, int lvl, ItemStack st) {
		HashMap<Enchantment, Integer> enchant = new HashMap();
		if (en != null && lvl > 0) enchant.put(en, Integer.valueOf(lvl));
		EnchantmentHelper.setEnchantments(enchant, st);
	}
	
	public static boolean oreMatches(ItemStack st, String id) {
	      for (int i : OreDictionary.getOreIDs(st)) if (OreDictionary.getOreName(i).equals(id)) return true;
	      return false;
	}
	
	public static boolean oreMatches(Block bl, String id) {
	      return oreMatches(new ItemStack(bl), id);
	}

	public static int getMultiplier(int tier, int use) {
		return JSTUtils.getVoltFromTier(tier) / JSTUtils.getNearestVolt(Math.max(32, use));
	}

    public static RayTraceResult rayTracePlayer(World w, EntityPlayer pl, boolean liq) {
        float rp = pl.rotationPitch;
        float ry = pl.rotationYaw;
        double px = pl.posX;
        double py = pl.posY + (double)pl.getEyeHeight();
        double pz = pl.posZ;
        Vec3d v3d = new Vec3d(px, py, pz);
        float f2 = MathHelper.cos(-ry * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-ry * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-rp * 0.017453292F);
        float f5 = MathHelper.sin(-rp * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        if (pl instanceof EntityPlayerMP) d3 = ((EntityPlayerMP)pl).interactionManager.getBlockReachDistance();
        Vec3d v3d2 = v3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return w.rayTraceBlocks(v3d, v3d2, liq, !liq, false);
    }

	public static boolean checkSun(World w, BlockPos p) {
		return !w.provider.isNether() && w.getLightFor(EnumSkyBlock.SKY, p.up()) > 7 && w.isDaytime() && (!w.isRaining() || w.getBiome(p).getRainfall() <= 0.0F) && w.canBlockSeeSky(p.up());
	}
	
	public static boolean clearAdvancement(EntityPlayer pl, String dom, String id) {
		if (pl instanceof EntityPlayerMP) {
			try {
				Advancement adv = ((WorldServer)pl.getEntityWorld()).getAdvancementManager().getAdvancement(new ResourceLocation(dom, id));
				if (adv == null) return false;
				AdvancementProgress prg = ((EntityPlayerMP)pl).getAdvancements().getProgress(adv);
				if (!prg.isDone()) {
					for (String s : prg.getRemaningCriteria())
						((EntityPlayerMP)pl).getAdvancements().grantCriterion(adv, s);
					return true;
				}
			} catch (Exception e) {}
		}
		return false;
	}

	public static String getUnlocalizedFluidName(Fluid f) {
		return f.canBePlacedInWorld() ? f.getBlock().getUnlocalizedName() + ".name" : f.getUnlocalizedName();
	}

	public static void printToBook(ItemStack st, String str) {
		if (st == null || st.isEmpty() || st.getItem() != Items.WRITABLE_BOOK || str == null || str.isEmpty()) return;
		NBTTagCompound tag = JSTUtils.getOrCreateNBT(st);
		NBTTagList tag2 = tag.getTagList("pages", Constants.NBT.TAG_STRING);
		if (tag2.hasNoTags()) {
			tag2.appendTag(new NBTTagString(str));
		} else {
			int n = tag2.tagCount() - 1;
			String str2 = tag2.getStringTagAt(n);
			if (str2.length() + str.length() < 32767 && calcLines(str) + calcLines(str2) <= 14)
				tag2.set(n, new NBTTagString((str2.isEmpty() ? "" : str2 + "\n") + str));
			else
				tag2.appendTag(new NBTTagString(str));
		}
		tag.setTag("pages", tag2);
	}
	
	private static int calcLines(String str) {
		int ret = 0; String[] arr = str.split("\n");
		for (String s : arr) ret += Math.max(1, s.length() / 20 + (s.length() % 20 == 0 ? 0 : 1));
		return ret;
	}

	public static boolean hasType(Biome biome, BiomeDictionary.Type... type) {
		for (BiomeDictionary.Type t : type)
			if (BiomeDictionary.hasType(biome, t)) return true;
		return false;
	}

	public static List<ItemStack> getBlockDrops(World w, BlockPos p, IBlockState bs, int ftne, float cnc, boolean sit, EntityPlayer pl, boolean rTool) {
		Block b = bs.getBlock();
		NonNullList<ItemStack> ls = NonNullList.<ItemStack>create();
		b.getDrops(ls, w, p, bs, ftne);
		if (ls.isEmpty()) return ls;
		if (!rTool || !CompatBWM.isHCPileAffected(bs))
			cnc = ForgeEventFactory.fireBlockHarvesting(ls, w, p, bs, ftne, cnc, sit, pl);
		List<ItemStack> ret = new ArrayList();
		for (ItemStack st : ls) ret.add(w.rand.nextFloat() <= cnc ? st : ItemStack.EMPTY);
		return ret;
	}

	public static ItemStack modStack(ItemStack s, int c, int m) {
		if (s.isEmpty()) return ItemStack.EMPTY;
		s = s.copy();
		if (c > 0) s.setCount(c);
		if (m > 0) s.setItemDamage(m);
		return s.isEmpty() ? ItemStack.EMPTY : s;
	}

	public static int getFluidAmount(FluidStack fs) {
		return fs == null ? 0 : fs.amount;
	}

	public static void setBiome(World w, int x, int z, Biome b) {
		if (b == null) return;
		Chunk c = w.getChunkFromChunkCoords(x >> 4, z >> 4);
		byte[] a = c.getBiomeArray();
		a[((z & 0xF) << 4 | x & 0xF)] = ((byte) (Biome.getIdForBiome(b) & 0xFF));
		c.setBiomeArray(a);
		JSTPacketHandler.sendBiomeChange(w, x, z, b);
	}

	public static FluidStack modFStack(FluidStack fs, int a) {
		fs = fs.copy();
		if (a > 0)
			fs.amount = a;
		return fs;
	}

	public static void giveItem(@Nonnull EntityPlayer pl, ItemStack st) {
		if (!st.isEmpty() && !pl.inventory.addItemStackToInventory(st))
			JSTUtils.dropEntityItemInCoord(pl.world, pl.posX, pl.posY, pl.posZ, st, true);
	}

	@Nonnull
	public static String getRegName(Object iob, boolean ed) {
		Object ret = null;
		if (iob instanceof ItemStack) {
			if (!((ItemStack)iob).isEmpty())
				ret = Item.REGISTRY.getNameForObject(((ItemStack)iob).getItem());
		} else if (iob instanceof Item) {
			ret = Item.REGISTRY.getNameForObject((Item)iob);
		} else if (iob instanceof IBlockState) {
			ret = Block.REGISTRY.getNameForObject(((IBlockState)iob).getBlock());
		} else if (iob instanceof Block) {
			ret = Block.REGISTRY.getNameForObject((Block)iob);
		} else if (iob instanceof FluidStack) {
			ret = ((FluidStack)iob).getFluid().getName();
		} else if (iob instanceof Fluid) {
			ret = ((Fluid)iob).getName();
		} else if (iob instanceof Entity) {
			for (Entry<ResourceLocation, EntityEntry> ee : ForgeRegistries.ENTITIES.getEntries())
				if (iob.getClass() == ee.getValue().getEntityClass()) {
					ret = ee.getKey();
					break;
				}
		}
		if (ed && ret instanceof ResourceLocation)
			return ((ResourceLocation)ret).getResourcePath();
		return ret == null ? "" : ret.toString();
	}

	@Nonnull
	public static String getRegName(Object iob) {
		return getRegName(iob, false);
	}

	public static void sendChatTrsl(@Nonnull EntityPlayer pl, String ul, Object... obj) {
		pl.sendMessage(new TextComponentTranslation(ul, obj));
	}

	public static void sendStatTrsl(@Nonnull EntityPlayer pl, boolean ab, String ul, Object... obj) {
		pl.sendStatusMessage(new TextComponentTranslation(ul, obj), ab);
	}
}
