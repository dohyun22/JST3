package dohyun22.jst3.items.behaviours;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.api.IScrewDriver;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class IB_ScrewdriverE extends ItemBehaviour {
	
	public IB_ScrewdriverE() {
		maxEnergy = 10000;
	}
	
	@Override
	public int getTier(ItemStack st) {
		return 1;
	}
	
	@Override
	public boolean canCharge(ItemStack st) {
		return true;
	}
	
	@Override
	public boolean canDischarge(ItemStack st) {
		return false;
	}
	
	@Override
	public long transferLimit(ItemStack st) {
		return 32L;
	}

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer ep, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote || getEnergy(st) < 100 || s == null) return EnumActionResult.PASS;
		boolean flag = false;
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		TileEntity te = w.getTileEntity(p);
		if (mte instanceof IScrewDriver)
			flag = ((IScrewDriver)mte).onScrewDriver(ep, s, hx, hy, hz);
		else if (te instanceof IScrewDriver)
			flag = ((IScrewDriver)te).onScrewDriver(ep, s, hx, hy, hz);
		if (flag) {
			doWork(st, w, p);
			return EnumActionResult.SUCCESS;
		}
		if (JSTCfg.ieLoaded) {
			try {
				if (te instanceof IEBlockInterfaces.IConfigurableSides) {
					int n = ep.isSneaking() ? s.getOpposite().ordinal() : s.ordinal();
					if (((IEBlockInterfaces.IConfigurableSides)te).toggleSide(n, ep)) {
						doWork(st, w, p);
						return EnumActionResult.SUCCESS;
					}
				}
				if (te instanceof IEBlockInterfaces.IDirectionalTile && ((IEBlockInterfaces.IDirectionalTile)te).canHammerRotate(s, hx, hy, hz, ep)) {
					EnumFacing f = ((IEBlockInterfaces.IDirectionalTile)te).getFacing();
					EnumFacing of = f;
					int lim = ((IEBlockInterfaces.IDirectionalTile)te).getFacingLimitation();
					if (lim == 0)
						f = EnumFacing.VALUES[((f.ordinal() + 1) % EnumFacing.VALUES.length)];
					else if (lim == 1)
						f = ep.isSneaking() ? f.rotateAround(s.getAxis()).getOpposite() : f.rotateAround(s.getAxis());
					else if (lim == 2 || lim == 5)
						f = ep.isSneaking() ? f.rotateYCCW() : f.rotateY();
					((IEBlockInterfaces.IDirectionalTile)te).setFacing(f);
					((IEBlockInterfaces.IDirectionalTile)te).afterRotation(of, f);
					te.markDirty();
					IBlockState bs = w.getBlockState(p);
					w.notifyBlockUpdate(p, bs, bs, 3);
					w.addBlockEvent(te.getPos(), te.getBlockType(), 255, 0);
					doWork(st, w, p);
					return EnumActionResult.SUCCESS;
				}
				if (te instanceof IEBlockInterfaces.IHammerInteraction && ((IEBlockInterfaces.IHammerInteraction)te).hammerUseSide(s, ep, hx, hy, hz)) {
					doWork(st, w, p);
					return EnumActionResult.SUCCESS;
				}
			} catch (Throwable t) {}
		}
		return EnumActionResult.PASS;
	}
	
	private void doWork(ItemStack st, World w, BlockPos p) {
		useEnergy(st, 100, false);
		w.playSound(null, p, JSTSounds.WRENCH, SoundCategory.BLOCKS, 1.0F, 1.5F);
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
	
	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}
	
	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, this.maxEnergy);
		sub.add(st);
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		addEnergyTip(st, ls);
		ls.add(I18n.format("jst.tooltip.screwdriver"));
		if (Loader.isModLoaded("immersiveengineering"))
			ls.add(I18n.format("jst.tooltip.screwdriver.ie"));
		if (Loader.isModLoaded("projectred-core"))
			ls.add(I18n.format("jst.tooltip.screwdriver.pr"));
	}

	@Override
	public boolean isScrewdriver(ItemStack st) {
		return getEnergy(st) >= 100;
	}

	@Override
	public void onScrewdriverUsed(ItemStack st, EntityLivingBase el) {
		doWork(st, el.world, el.getPosition());
	}
}
