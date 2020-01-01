package dohyun22.jst3.items.behaviours.tool;

import java.math.BigDecimal;
import java.math.BigInteger;

import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.Charge.IAccess;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.util.charge.ChargeNetwork;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class IB_Meter extends ItemBehaviour {
	
	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		if (st == null || pl == null || w.isRemote)
			return EnumActionResult.PASS;
		IBlockState bs = w.getBlockState(p);
		if (bs.getBlock() == Blocks.REDSTONE_WIRE) {
			JSTUtils.sendChatTrsl(pl, "jst.msg.meter.rs", bs.getBlock().getMetaFromState(bs));
			playSFX(pl);
			return EnumActionResult.SUCCESS;
		}

		/*if (JSTCfg.rcLoaded) {
			try {
				if (bs.getBlock() instanceof IChargeBlock) {
					IAccess ia = ((IChargeBlock)bs.getBlock()).getMeterAccess(Charge.distribution, bs, w, p);
					if (ia != null) {
						/*String[] str = ia.toString().split("\\|");
						//if (str.length >= 3) {
							JSTUtils.sendChatTrsl(pl, "jst.msg.meter.rc.charge", ia.toString()str[2].substring(13).replaceFirst("\\,.*", ""));
							playSFX(pl);
							return EnumActionResult.SUCCESS;
						//}
					}
				}
			} catch (Throwable e) {e.printStackTrace();}
		}*/

		TileEntity te = w.getTileEntity(p);
		if (te == null) {
			JSTUtils.sendChatTrsl(pl, "jst.msg.meter.rs", w.getStrongPower(p));
			playSFX(pl);
			return EnumActionResult.SUCCESS;
		}

		if (te instanceof TileEntityMeta && ((TileEntityMeta)te).hasValidMTE()) {
			MetaTileBase mte = ((TileEntityMeta)te).mte;
			if (mte.getMaxEnergy() > 0) {
				JSTUtils.sendChatTrsl(pl, "jst.msg.meter.stored", ((TileEntityMeta)te).energy, mte.getMaxEnergy(), "EU");
				playSFX(pl);
				return EnumActionResult.SUCCESS;
			}
			JSTUtils.sendChatTrsl(pl, "jst.msg.meter.rs", w.getStrongPower(p));
			return EnumActionResult.SUCCESS;
		}

		if (te.hasCapability(CapabilityEnergy.ENERGY, s)) {
			IEnergyStorage es = te.getCapability(CapabilityEnergy.ENERGY, s);
			JSTUtils.sendChatTrsl(pl, "jst.msg.meter.stored", es.getEnergyStored(), es.getMaxEnergyStored(), "FE");
			playSFX(pl);
			return EnumActionResult.SUCCESS;
		}

		if (JSTCfg.ic2Loaded) {
			try {
				if (te instanceof ic2.api.tile.IEnergyStorage) {
					JSTUtils.sendChatTrsl(pl, "jst.msg.meter.stored", ((ic2.api.tile.IEnergyStorage)te).getStored(), ((ic2.api.tile.IEnergyStorage)te).getCapacity(), "EU");
					playSFX(pl);
					return EnumActionResult.SUCCESS;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		JSTUtils.sendChatTrsl(pl, "jst.msg.meter.rs", w.getStrongPower(p));
		playSFX(pl);
		return EnumActionResult.SUCCESS;
	}

	private static void playSFX(EntityPlayer pl) {
		pl.world.playSound(null, pl.posX, pl.posY, pl.posZ, JSTSounds.SCAN, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
}
