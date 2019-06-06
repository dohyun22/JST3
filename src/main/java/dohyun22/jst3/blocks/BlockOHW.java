package dohyun22.jst3.blocks;

import java.util.List;
import java.util.Map;
import java.util.Random;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTUtils;
import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@Optional.Interface(iface = "mods.railcraft.api.charge.IChargeBlock",modid="railcraft")
public class BlockOHW extends Block implements IChargeBlock {
	private static Object charge_specs;
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool[] SIDE = {PropertyBool.create("south"), PropertyBool.create("west"), PropertyBool.create("north"), PropertyBool.create("east")};
	public static final PropertyBool INSL = PropertyBool.create("insl");
	private static final AxisAlignedBB[] AABBS = new AxisAlignedBB[] {
			new AxisAlignedBB(0.4375D, 0.0625D, 0.4375D, 0.5625D, 0.75D, 0.5625D),
			new AxisAlignedBB(0.4375D, 0.0D, 0.5625D, 0.5625D, 0.75D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 0.4375D, 0.75D, 0.5625D),
			new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 0.5625D, 0.75D, 0.4375D),
			new AxisAlignedBB(0.5625D, 0.0D, 0.4375D, 1.0D, 0.75D, 0.5625D)};

	public BlockOHW() {
		super(Material.IRON);
		setUnlocalizedName("jst.ohw");
		setRegistryName("ohw");
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		setHardness(1.0F);
		setResistance(6.0F);
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState().withProperty(INSL, false));
		if (JSTCfg.rcLoaded) {
			try {charge_specs = IChargeBlock.ChargeSpec.make(Charge.distribution, IChargeBlock.ConnectType.WIRE, 0.001D);} catch (Throwable t) {}
			setTickRandomly(true);
		}/* else {
			GameRegistry.registerTileEntity(TileOHW.class, new ResourceLocation(JustServerTweak.MODID, "ohw"));
		}*/
	}

	@Override
	public void addCollisionBoxToList(IBlockState bs, World w, BlockPos p, AxisAlignedBB eb, List<AxisAlignedBB> cb, Entity e, boolean as) {
		if (!as) bs = bs.getActualState(w, p);
		addCollisionBoxToList(p, eb, cb, AABBS[0]);
		if (bs.getValue(SIDE[0]))
			addCollisionBoxToList(p, eb, cb, AABBS[1]);
		if (bs.getValue(SIDE[1]))
			addCollisionBoxToList(p, eb, cb, AABBS[2]);
		if (bs.getValue(SIDE[2]))
			addCollisionBoxToList(p, eb, cb, AABBS[3]);
		if (bs.getValue(SIDE[3]))
			addCollisionBoxToList(p, eb, cb, AABBS[4]);
	}

	@Override
	public void updateTick(World w, BlockPos p, IBlockState bs, Random r) {
		if (JSTCfg.rcLoaded) onBlockAdded(w, p, bs);
	}

	@Override
	public void onBlockAdded(World w, BlockPos p, IBlockState bs) {
		if (JSTCfg.rcLoaded) {
			try {
				registerNode(bs, w, p);
			} catch (Throwable t) {}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World w, BlockPos p, IBlockState bs, Entity e) {
		if (!w.isRemote) {
			if (JSTCfg.rcLoaded)
				try {Charge.distribution.network(w).access(p).zap(e, Charge.DamageOrigin.BLOCK, 2.0F);} catch (Throwable t) {}
			/*else if (e instanceof EntityLivingBase) {
				e.attackEntityFrom(JSTDamageSource.ELECTRIC, 2.0F);
				JSTPacketHandler.playCustomEffect(w, p, 1, -10);
			}*/
		}
	}

	@Override
	public void breakBlock(World w, BlockPos p, IBlockState bs) {
		super.breakBlock(w, p, bs);
		if (bs.getValue(INSL)) JSTUtils.dropEntityItemInPos(w, p, new ItemStack(Blocks.STONE_SLAB));
		if (JSTCfg.rcLoaded) {
			try {
				Charge.distribution.network(w).removeNode(p);
			} catch (Throwable t) {}
		}
	}

	@Override
	@Method(modid="railcraft")
	public Map<Charge, IChargeBlock.ChargeSpec> getChargeSpecs(IBlockState bs, IBlockAccess w, BlockPos p) {
		return (Map<Charge, ChargeSpec>) charge_specs;
	}

	@Override
	public IBlockState getActualState(IBlockState bs, IBlockAccess w, BlockPos p) {
		boolean s = connect(w, p, EnumFacing.SOUTH), t = connect(w, p, EnumFacing.WEST), n = connect(w, p, EnumFacing.NORTH), e = connect(w, p, EnumFacing.EAST);
		if (!s && !t && !n && !e) s = t = n = e = true;
		return bs.withProperty(UP, !bs.getValue(INSL) && connect(w, p, EnumFacing.UP)).withProperty(SIDE[0], s).withProperty(SIDE[1], t).withProperty(SIDE[2], n).withProperty(SIDE[3], e);
	}

	private boolean connect(IBlockAccess w, BlockPos p, EnumFacing f) {
		return w.getBlockState(p.offset(f)).getBlock() instanceof BlockOHW;
	}

	@Override
	public IBlockState getStateFromMeta(int m) {
		return getDefaultState().withProperty(INSL, m == 1);
	}

	@Override
	public int getMetaFromState(IBlockState bs) {
		return bs.getValue(INSL) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, UP, SIDE[0], SIDE[1], SIDE[2], SIDE[3], INSL);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState bs) {
    	return false;
	}
   
	@Override
	public boolean isSideSolid(IBlockState bs, IBlockAccess w, BlockPos p, EnumFacing s) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World w, BlockPos p, IBlockState s, EntityPlayer pl, EnumHand h, EnumFacing sd, float hx, float hy, float hz) {
		boolean ret = OreDictionary.itemMatches(new ItemStack(Blocks.STONE_SLAB), pl.getHeldItem(h), false);
		if (!w.isRemote && ret && canAttatch(w, p)) {
			w.setBlockState(p, s.withProperty(INSL, true));
			if (!pl.capabilities.isCreativeMode) pl.getHeldItem(h).shrink(1);
		}
		return ret;
	}

	@Override
	public void neighborChanged(IBlockState bs, World w, BlockPos p, Block b, BlockPos f) {
		if (w instanceof World && !((World)w).isRemote && bs.getValue(INSL) && !canAttatch(w, p)) {
			((World)w).setBlockState(p, bs.withProperty(INSL, false));
			JSTUtils.dropEntityItemInPos((World)w, p, new ItemStack(Blocks.STONE_SLAB));
		}
	}

	@Override
	public EnumPushReaction getMobilityFlag(IBlockState bs) {
		return EnumPushReaction.BLOCK;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState bs, IBlockAccess w, BlockPos p, EnumFacing s) {
		return true;
	}

	private boolean canAttatch(World w, BlockPos p) {
		return !w.isAirBlock(p.up()) && !(w.getBlockState(p.up()).getBlock() instanceof BlockOHW);
	}
}
