package dohyun22.jst3.items.behaviours.tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cofh.api.block.IDismantleable;
import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.IB_Damageable;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;

public class IB_Wrench extends IB_Damageable {
	/** Block IDs of breakable machine blocks */
	private static final ArrayList<String> allowedMachineList = new ArrayList();

	public IB_Wrench(int maxdmg) {
		super(maxdmg);
		if (allowedMachineList.isEmpty()) {
			allowedMachineList.add("gregtech:gt.blockmachines");
			allowedMachineList.add("ic2:te");
			allowedMachineList.add(JustServerTweak.MODID + ":blockte");
		}
	}

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		return doWrench(st, pl, w, p, s, hx, hy, hz);
	}

	public static EnumActionResult doWrench(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz) {
		if (!w.isRemote && !w.isAirBlock(p)) {
			ItemBehaviour ib = JSTItems.item1.getBehaviour(st);
			s = JSTUtils.determineWrenchingSide(s, hx, hy, hz);
			IBlockState bs = w.getBlockState(p);
			Block lb = bs.getBlock();
			
			TileEntity te = w.getTileEntity(p);
			try {
				if (pl.isSneaking() && ModAPIManager.INSTANCE.hasAPI("cofhapi") && lb instanceof IDismantleable && ((IDismantleable) lb).canDismantle(w, p, bs, pl) && JSTUtils.canPlayerBreakThatBlock(pl, p)) {
					((IDismantleable) lb).dismantleBlock(w, p, bs, pl, false);
					ib.doDamage(st, pl);
					playWrenchSound(pl);
					return EnumActionResult.SUCCESS;
				}
			} catch (Throwable t) {}

			try {
				if (JSTCfg.ic2Loaded && lb instanceof IWrenchable) {
					IWrenchable iw = (IWrenchable) lb;
					if (iw.getFacing(w, p) != s && iw.setFacing(w, p, s, pl)) {
						ib.doDamage(st, pl);
						playWrenchSound(pl);
					} else if (pl.isSneaking() && JSTUtils.canPlayerBreakThatBlock(pl, p) && iw.wrenchCanRemove(w, p, pl)) {
						List<ItemStack> wds = iw.getWrenchDrops(w, p, w.getBlockState(p), te, pl, 0);
						if (wds != null)
							for (ItemStack wd : wds)
								Block.spawnAsEntity(w, p, wd);
						w.setBlockToAir(p);
						if (w.getTileEntity(p) != null)
							w.removeTileEntity(p);
						ib.doDamage(st, pl);
						playWrenchSound(pl);
					}
					return EnumActionResult.SUCCESS;
				}
			} catch (Throwable t) {}

			if (pl.isSneaking() && bs.getPlayerRelativeBlockHardness(pl, w, p) >= 0 && allowedMachineList.contains(Block.REGISTRY.getNameForObject(lb).toString()) && JSTUtils.canPlayerBreakThatBlock(pl, p)) {
				ib.doDamage(st, pl);
				playWrenchSound(pl);
				for (ItemStack dr : lb.getDrops(w, p, bs, 1))
					Block.spawnAsEntity(w, p, dr);
				w.setBlockToAir(p);
				if (te != null)
					w.removeTileEntity(p);
				return EnumActionResult.SUCCESS;
			}

			for (IProperty prop : bs.getPropertyKeys()) {
				if (prop.getName().equals("facing")) {
					try {
						if (prop.getAllowedValues().contains(s)) {
							if (s.equals(bs.getValue(prop))) {
								tryDismantle(pl, p, bs);
							} else {
								ib.doDamage(st, pl);
								playWrenchSound(pl);
								NBTTagCompound nbt = te.writeToNBT(new NBTTagCompound());
								w.setBlockState(p, bs.withProperty(prop, s));
								te = w.getTileEntity(p);
								if (te != null) te.readFromNBT(nbt);
							}
						}
					} catch (Throwable t) {}
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack st, BlockPos p, EntityPlayer pl) {
		World w = pl.getEntityWorld();
		if (w.isRemote || pl.isCreative()) return false;
		if (JSTCfg.ic2Loaded) {
			try {
				IBlockState bs = w.getBlockState(p);
				Block b = bs.getBlock();
				if (b instanceof IWrenchable) {
					if (((IWrenchable)b).wrenchCanRemove(w, p, pl)) {
						List<ItemStack> wds = ((IWrenchable)b).getWrenchDrops(w, p, w.getBlockState(p), w.getTileEntity(p), pl, 0);
						if (wds != null)
							for (ItemStack wd : wds)
								Block.spawnAsEntity(w, p, wd);
						w.setBlockToAir(p);
						if (w.getTileEntity(p) != null)
							w.removeTileEntity(p);
						playWrenchSound(pl);
						return true;
					}
				}
			} catch (Throwable t) {}
		}
		return false;
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack st, EntityPlayer pl, Entity e) {
		this.doDamage(st, 2, pl);
		return super.onLeftClickEntity(st, pl, e);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack st, World w, IBlockState bs, BlockPos p, EntityLivingBase el) {
		if ((double) bs.getBlockHardness(w, p) != 0.0D) {
			doDamage(st, el);
			String tool = bs.getBlock().getHarvestTool(bs);
			if (canHarvestBlock(bs, st))
				playWrenchSound(el);
		}
		return true;
	}

	@Override
	public int harvestLevel(ItemStack st, String tc, @Nullable EntityPlayer pl, @Nullable IBlockState bs) {
		if (bs == null) return -1;
		return canHarvestBlock(bs, st) ? 4 : -1;
	}

	@Override
	public boolean canHarvestBlock(IBlockState bs, ItemStack st) {
		if (bs == null) return true;
		Block b = bs.getBlock();
		String str = b.getHarvestTool(bs);
		return (str != null ? (str.equals("wrench") || str.equals("cutter")) : false) || bs.getMaterial() == Material.PISTON || b == Blocks.HOPPER || b == Blocks.DISPENSER || b == Blocks.DROPPER || b == Blocks.OBSERVER || allowedMachineList.contains(JSTUtils.getRegName(bs));
	}

	@Override
	public float getDigSpeed(ItemStack st, IBlockState bs) {
		return canHarvestBlock(bs, st) ? 10.0F : 1.0F;
	}

	public static void tryDismantle(EntityPlayer ep, BlockPos p, IBlockState bs) {
		if (!JSTUtils.canPlayerBreakThatBlock(ep, p) || !ep.isSneaking())
			return;
		Block lb = bs.getBlock();
		if (lb == Blocks.PUMPKIN || lb == Blocks.LIT_PUMPKIN || lb == Blocks.PISTON || lb == Blocks.STICKY_PISTON
				|| lb == Blocks.DISPENSER || lb == Blocks.DROPPER || lb == Blocks.FURNACE || lb == Blocks.LIT_FURNACE
				|| lb == Blocks.CHEST || lb == Blocks.TRAPPED_CHEST || lb == Blocks.ENDER_CHEST
				|| lb == Blocks.HOPPER) {
			playWrenchSound(ep);
			ep.world.setBlockToAir(p);
			JSTUtils.dropEntityItemInPos(ep.world, p, lb.getItem(ep.world, p, bs));
			if (ep.world.getTileEntity(p) != null)
				ep.world.removeTileEntity(p);
		}
	}

	private static void playWrenchSound(EntityLivingBase el) {
		if (el == null) return;
		el.world.playSound(null, el.posX, el.posY, el.posZ, JSTSounds.WRENCH, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack st, IBlockAccess w, BlockPos p, EntityPlayer pl) {
		return true;
	}

	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}

	@Override
	public boolean hasContainerItem(ItemStack st) {
		if (st.isEmpty()) return false;
		return !getContainerItem(st).isEmpty();
	}

	@Override
	public ItemStack getContainerItem(ItemStack st) {
		if (st.isEmpty()) return ItemStack.EMPTY;
		st = st.copy();
		this.doDamage(st, null);
		return st;
	}

	@Override
	public boolean isWrench(ItemStack st) {
		return true;
	}

	@Override
	public void onWrenchUsed(ItemStack st, EntityLivingBase el) {
		playWrenchSound(el);
		this.doDamage(st, 1, el);
	}

	@Override
	public void addToolClasses(ItemStack st, Set<String> list) {
		list.add("wrench");
	}
}
