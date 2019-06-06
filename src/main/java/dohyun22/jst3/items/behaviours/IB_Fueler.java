package dohyun22.jst3.items.behaviours;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_Fueler extends ItemBehaviour {
	
	@Override
	public boolean hasCapability(Capability<?> cap, ItemStack st) {
		return cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> cap, ItemStack st) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
			return (T) new FuelerFluidHandler(st);
		return null;
	}
	
	private class FuelerFluidHandler extends FluidHandlerItemStack {
		public FuelerFluidHandler(ItemStack st) {
			super(st, 8000);
		}
		
		@Override
		public int fill(FluidStack fs, boolean fill) {
			if (fs != null && fs.getFluid() != null && MRecipes.allowedFuelerFuels.contains(fs.getFluid().getName()))
				return super.fill(fs, fill);
			return 0;
		}
		
		@Override
		public FluidStack drain(FluidStack fs, boolean drain) {
			if (fs != null && fs.getFluid() != null && MRecipes.allowedFuelerFuels.contains(fs.getFluid().getName()))
				return super.drain(fs, drain);
			return null;
		}
		
		@Override
		protected void setContainerToEmpty() {
			super.setContainerToEmpty();
			if (container.getTagCompound().hasNoTags()) this.container.setTagCompound(null);
		}
	}
	
	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing f, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote) return EnumActionResult.PASS;
		IFluidHandler th = FluidUtil.getFluidHandler(w, p, f);
		IFluidHandlerItem ih = FluidUtil.getFluidHandler(st);
		if (th != null && ih != null) {
			boolean flag = ih.drain(Integer.MAX_VALUE, false) != null;
			if (FluidUtil.tryFluidTransfer(flag ? th : ih, flag ? ih : th, Integer.MAX_VALUE, true) != null)
				return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
	
	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		ActionResult<ItemStack> ret = new ActionResult(EnumActionResult.PASS, st);
		if (JSTUtils.isClient()) return ret;
		IFluidHandlerItem fh1 = FluidUtil.getFluidHandler(st);
		ItemStack st2 = pl.inventory.armorInventory.get(2);
		IFluidHandlerItem fh2 = FluidUtil.getFluidHandler(st2);
		if ("ic2:jetpack".equals(JSTUtils.getItemID(st2.getItem())) && fh1 != null && fh2 != null)
			fh1.drain(fh2.fill(fh1.drain(Integer.MAX_VALUE, false), true), true);
		return ret;
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
	@SideOnly(Side.CLIENT)
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		ArrayList<String> ret = new ArrayList();
		ret.addAll(JSTUtils.getListFromTranslation("jst.tooltip.fueler"));
		IFluidHandlerItem fh = FluidUtil.getFluidHandler(st);
		if (fh != null) {
			IFluidTankProperties[] tank = fh.getTankProperties();
			if (tank != null && tank.length > 0) {
				FluidStack fs = tank[0].getContents();
				ret.add((fs == null ? 0 : fs.amount) + " / " + tank[0].getCapacity() + "mB " + (fs == null ? "" : fs.getFluid().getLocalizedName(fs)));
			}
		}
		return ret;
	}
}
