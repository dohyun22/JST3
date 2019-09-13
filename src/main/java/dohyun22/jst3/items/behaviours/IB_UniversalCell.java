package dohyun22.jst3.items.behaviours;

import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

public class IB_UniversalCell extends ItemBehaviour {
	private final int capacity;

	public IB_UniversalCell(int cap) {
		capacity = cap;
	}

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		TileEntity te = w.getTileEntity(p);
		IFluidHandlerItem fi = FluidUtil.getFluidHandler(st);
		if (st.getCount() != 1 || fi == null) return EnumActionResult.PASS;
		FluidStack fs = FluidUtil.getFluidContained(st);
		Object[] asp = getAspect(st);
		if (asp == null) {
			int trans = getTransfer(st, false);
			boolean ret = false;
			if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, s)) {
				if (w.isRemote) return EnumActionResult.PASS;
				IFluidHandler fh = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, s);
				FluidStack fs2 = null;
				if (pl.isSneaking()) {
					if (fs != null) {
						fs.amount = Math.min(fs.amount, trans);
						fs2 = fi.drain(fh.fill(fs, true), true);
					}
				} else {
					fs2 = fh.drain(fi.fill(fh.drain(trans, false), true), true);
				}
				if (fs2 != null) {
					boolean gas = fs2.getFluid().isGaseous(fs2);
					w.playSound(null, p, gas ? SoundEvents.BLOCK_LAVA_EXTINGUISH : fs2.getFluid().getFillSound(), SoundCategory.BLOCKS, 1.0F, gas ? 2.0F : 1.0F);
					return EnumActionResult.SUCCESS;
				}
				ret = true;
			}
			if (ret) return EnumActionResult.SUCCESS;
		}

		if (Loader.isModLoaded("thaumcraft")) {
			if (fs == null) {
				try {
					if (te instanceof IAspectContainer && !w.isRemote) {
							int trans = getTransfer(st, true);
							boolean snd = false;
							IAspectContainer ac = ((IAspectContainer)te);
							if (pl.isSneaking()) {
								if (asp != null) {
									int use = Math.min((int)asp[1], trans);
									int left = ac.addToContainer((Aspect)asp[0], use);
									if (left != use) {
										left = (int)asp[1] - use + left;
										if (left > 0) {
											st.getTagCompound().getCompoundTag("TCasp").setInteger("am", left);
										} else {
											st.getTagCompound().removeTag("TCasp");
						    				if (st.getTagCompound().hasNoTags())
						    					st.setTagCompound(null);
										}
										snd = true;
									}
								}
							} else {
								if (asp != null) {
									int sp = (capacity / 125) - (int)asp[1];
									AspectList al = ac.getAspects();
									if (al != null) {
										int amt = Math.min(al.getAmount((Aspect)asp[0]), trans);
										if (amt >= 0 && sp >= amt && ac.takeFromContainer((Aspect)asp[0], amt)) {
											st.getTagCompound().getCompoundTag("TCasp").setInteger("am", (int)asp[1] + amt);
											snd = true;
										}
									}
								} else {
									AspectList al = ac.getAspects();
									for (Entry<Aspect, Integer> as : al.aspects.entrySet()) {
										int am = Math.min(trans, as.getValue());
										if (ac.takeFromContainer(as.getKey(), am)) {
											NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
											NBTTagCompound nbt2 = new NBTTagCompound();
											String str = as.getKey().getTag();
											nbt2.setString("an", str == null ? "" : str);
											nbt2.setInteger("am", am);
											nbt.setTag("TCasp", nbt2);
											snd = true;
											break;
										}
									}
								}
							}
							if (snd) w.playSound(null, p, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.BLOCKS, 1.0F, 0.5F);
							return EnumActionResult.SUCCESS;
					}
				} catch (Throwable t) {
					return EnumActionResult.SUCCESS;
				}
			}
		} else if (st.hasTagCompound() && st.getTagCompound().hasKey("TCasp")) {
			st.getTagCompound().removeTag("TCasp");
			if (st.getTagCompound().hasNoTags())
				st.setTagCompound(null);
		}
		if (!w.isRemote && pl.isSneaking()) {
			w.playSound(null, p, JSTSounds.SWITCH2, SoundCategory.BLOCKS, 0.5F, 1.0F);
			NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
			int tr = nbt.getInteger("_TRNS");
			tr++;
			if ((capacity < 32000 && tr > 4) || tr > 5) {
				nbt.removeTag("_TRNS");
				tr = 0;
			} else {
				nbt.setInteger("_TRNS", tr);
			}
			if (st.getTagCompound().hasNoTags())
				st.setTagCompound(null);
			tr = getTransfer(st, false);
			if (Loader.isModLoaded("thaumcraft"))
				JSTUtils.sendChatTrsl(pl, "jst.msg.unicell.trans.tc", tr, Math.max(1, tr / 125));
			else
				JSTUtils.sendChatTrsl(pl, "jst.msg.unicell.trans", tr);
			return EnumActionResult.SUCCESS;
		}
        return EnumActionResult.PASS;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, ItemStack st) {
		return cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> cap, ItemStack st) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
			return (T) new InternalTank(st, capacity);
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.add(I18n.format("jst.tooltip.unicell"));
		boolean tcl = Loader.isModLoaded("thaumcraft");
		if (tcl)
			ls.add(I18n.format("jst.tooltip.unicell.tc"));
		FluidStack fs = FluidUtil.getFluidContained(st);
		Object[] asp = getAspect(st);
		if (fs != null) {
			ls.add(fs.getLocalizedName());
			ls.add(fs.amount + " / " + capacity + " mB");
		} else if (asp != null) {
			try {
				ls.add(((Aspect)asp[0]).getName());
				ls.add(asp[1] + " / " + (capacity / 125));
			} catch (Throwable t) {}
		} else {
			ls.add(I18n.format("jst.msg.com.empty"));
			ls.add("0 / " + capacity + " mB");
			if (tcl) ls.add("0 / " + (capacity / 125));
		}
		int tr = getTransfer(st, false);
		if (tcl)
			ls.add(I18n.format("jst.tooltip.unicell.trans.tc", tr, Math.max(1, tr / 125)));
		else
			ls.add(I18n.format("jst.msg.unicell.trans", tr));
	}

    private static Object[] getAspect(ItemStack st) {
    	if (Loader.isModLoaded("thaumcraft") && st.hasTagCompound()) {
    		NBTTagCompound nbt = st.getTagCompound();
    		if (nbt.hasKey("TCasp")) {
    			NBTTagCompound nbt2 = nbt.getCompoundTag("TCasp");
				int amt = nbt2.getInteger("am");
    			try {
    				Aspect asp = Aspect.getAspect(nbt2.getString("an"));
    				if (asp != null && amt > 0)
    					return new Object[] {asp, amt};
    			} catch (Throwable t) {}
    		}
    	}
		return null;
	}

    private static int getTransfer(ItemStack st, boolean tc) {
    	if (!st.hasTagCompound()) return 1000;
    	NBTTagCompound tag = st.getTagCompound();
    	int ret = 1000;
		switch (tag.getInteger("_TRNS")) {
		case 1:
			ret = 16; break;
		case 2:
			ret = 144; break;
		case 3:
			ret = 1296; break;
		case 4:
			ret = 8000; break;
		case 5:
			ret = 32000; break;
		}
		if (tc)
			ret = Math.max(1, ret / 125);
		return ret;
    }

	private static class InternalTank extends FluidHandlerItemStack {
		InternalTank(ItemStack c, int cap) {
			super(c, cap);
		}

		@Override
		public FluidStack getFluid() {
			ItemStack st = getContainer();
	    	if (st.hasTagCompound() && getAspect(st) == null) {
	    		NBTTagCompound nbt = st.getTagCompound();
	    		if (nbt.hasKey("fluid")) {
	    			NBTTagCompound ft = nbt.getCompoundTag("fluid");
	    			String s = ft.getString("id");
	    			int am = ft.getInteger("am");
	    			Fluid f = FluidRegistry.getFluid(s);
	    			if (f != null && am > 0)
	    				return new FluidStack(f, am);
	    		}
	    	}
			return null;
		}

		@Override
		public int fill(FluidStack fs, boolean fi) {
			ItemStack st = getContainer();
	    	if (fs == null || st.getCount() != 1 || getAspect(st) != null) return 0;
	    	IFluidHandlerItem fh = FluidUtil.getFluidHandler(st);
	    	FluidStack in = fh == null ? null : fh.getTankProperties()[0].getContents();
	    	if (in != null) {
	    		if (in.isFluidEqual(fs)) {
	    			int sp = capacity - in.amount;
	    			if (sp > 0) {
	    				int re = Math.min(fs.amount, sp);
	    				if (fi) st.getTagCompound().getCompoundTag("fluid").setInteger("am", in.amount + re);
	    				return re;
	    			}
	    		}
	    	} else {
	    		int re = Math.min(fs.amount, capacity);
	    		if (fi) {
		    		NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
		    		NBTTagCompound dat = new NBTTagCompound();
		    		dat.setString("id", FluidRegistry.getFluidName(fs.getFluid()));
		    		dat.setInteger("am", re);
		    		nbt.setTag("fluid", dat);
	    		}
	    		return re;
	    	}
			return 0;
		}

		@Override
		public FluidStack drain(int mx, boolean dr) {
			ItemStack st = getContainer();
	    	if (mx <= 0 || st.getCount() != 1 || getAspect(st) != null) return null;
	    	IFluidHandlerItem fh = FluidUtil.getFluidHandler(st);
	    	FluidStack in = fh == null ? null : fh.getTankProperties()[0].getContents();
	    	if (in != null) {
	    		int am = Math.min(in.amount, mx);
	    		if (dr) {
	    			int amt = in.amount - am;
	    			if (amt <= 0) {
	    				st.getTagCompound().removeTag("fluid");
	    				if (st.getTagCompound().hasNoTags())
	    					st.setTagCompound(null);
	    			} else
	    				st.getTagCompound().getCompoundTag("fluid").setInteger("am", amt);
	    		}
	    		return new FluidStack(in.getFluid(), am);
	    	}
			return null;
		}

		@Override
		protected void setContainerToEmpty() {
			super.setContainerToEmpty();
			if (container.getTagCompound().hasNoTags()) container.setTagCompound(null);
		}
	}
}
