package dohyun22.jst3.items.behaviours.misc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class IB_FluidCan extends ItemBehaviour {
	public static final int startID = 9000;
	@Nonnull
	private final CanType type;

	public IB_FluidCan(@Nonnull CanType type) {
		this.type = type;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, ItemStack st) {
		return cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> cap, ItemStack st) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
			return (T) new CanFluidHandler(st, type);
		return null;
	}

	@Override
	public boolean hasContainerItem(ItemStack st) {
		return type != CanType.NONE;
	}

	@Override
	public ItemStack getContainerItem(ItemStack st) {
		return st.isEmpty() || type == CanType.NONE ? ItemStack.EMPTY : new ItemStack(JSTItems.item1, 1, startID);
	}

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (w.isRemote) return new ActionResult(EnumActionResult.SUCCESS, st);
		RayTraceResult rtr;
		if (type == CanType.NONE) {
			rtr = JSTUtils.rayTraceEntity(pl, true, false, false, 0);
			if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos p = rtr.getBlockPos();
				EnumFacing f = rtr.sideHit;
				if (w.canMineBlockBody(pl, p) && pl.canPlayerEdit(p, rtr.sideHit, pl.getHeldItem(h))) {
					FluidActionResult far = FluidUtil.tryPickUpFluid(st, pl, w, p, f);
					if (far.success) {
						if (!pl.isCreative()) {
							if (st.getCount() == 1) {
								pl.setHeldItem(h, far.result);
							} else {
								st.shrink(1);
								JSTUtils.giveItem(pl, far.result);
							}
							pl.inventoryContainer.detectAndSendChanges();
						}
						return new ActionResult(EnumActionResult.SUCCESS, st);
					}
				}
			}
	    } else {
	    	rtr = JSTUtils.rayTraceEntity(pl, false, true, false, 0);
	    	if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
	    		BlockPos p = rtr.getBlockPos();
				EnumFacing f = rtr.sideHit;
				p = p.offset(f);
				FluidActionResult far = FluidUtil.tryPlaceFluid(pl, w, p, st, type.getFluid());
				if (far != FluidActionResult.FAILURE) {
					if (!pl.isCreative()) {
						ItemStack c = new ItemStack(JSTItems.item1, 1, startID);
						if (st.getCount() > 1) {
							st.shrink(1);
							JSTUtils.giveItem(pl, c);
						} else
							pl.setHeldItem(h, c);
						pl.inventoryContainer.detectAndSendChanges();
					}
					return new ActionResult(EnumActionResult.SUCCESS, st);
				}
	    	}
	    }
		return new ActionResult(EnumActionResult.FAIL, st);
	}

	private static class CanFluidHandler implements IFluidHandlerItem {
		private ItemStack con;
		private CanType type;

		public CanFluidHandler(ItemStack con, CanType type) {
			this.con = con;
			this.type = type;
		}

		public int fill(FluidStack r, boolean d) {
			if (con.getCount() != 1 || r == null || r.amount <= 0 || type.fname != null)
				return 0;
			IB_FluidCan.CanType ct = IB_FluidCan.getCanType(r);
			if (ct != null) {
				int cap = ct.is144mb ? 144 : 1000;
				if (r.amount >= cap) {
					if (d)
						setContainer(ct.ordinal());
					return cap;
				}
			}
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack r, boolean d) {
			if (r == null || !r.isFluidEqual(type.getFluid()))
				return null;
			return drain(r.amount, d);
		}

		@Override
		public FluidStack drain(int a, boolean d) {
			if (con.getCount() != 1 || a < (type.is144mb ? 144 : 1000))
				return null;
			FluidStack fs = type.getFluid();
			if (fs == null || fs.amount <= 0)
				return null;
			if (d)
				setContainer(0);
			return fs;
		}

		@Override
		public ItemStack getContainer() {
			return con;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new IFluidTankProperties[] {new FluidTankProperties(type.getFluid(), type.is144mb ? 144 : 1000)};
		}

		public void setContainer(int id) {
			CanType[] ct = CanType.values();
			try {
				con.setItemDamage(startID + id);
				type = ct[startID + id - startID];
			} catch (Exception e) {
				con.setItemDamage(startID);
				type = CanType.NONE;
			}
		}
	}

	public static enum CanType {
		NONE(null), WATR("water"), LAVA("lava"), OIL("oil"), FUEL("fuel"),
		NTRO("nitrofuel"), NGAS("gas.natural"), LNG("liquid_lng"), LPG("liquid_lpg"), H("hydrogen"),
		H2("deuterium"), H3("tritium"), HE3("helium3"), HE("helium"), LI("lithium", true),
		C("carbon", true), N("nitrogen"), O("oxygen"), NA("sodium", true), CL("chlorine"),
		AIR("air"), ACID("acid"), HG("mercury"), UU("ic2uu_matter"), DSTW("ic2distilled_water"),
		CLNT("ic2coolant"), HCLT("ic2hot_coolant"), BMAS("biomass"), BETH("bio.ethanol"), CREO("creosote"),
		ETHA("ethanol"), BDSL("biodiesel"), POIL("plantoil"), SOIL("seed.oil"), DESL("diesel"),
		GASL("gasoline"), LUBR("lubricant"), HVYF("heavyfuel"), BGAS("ic2biogas"), IBMS("ic2biomass");

		public final String fname;
		public final boolean is144mb;

		private CanType(String fluid, boolean is144) {
			fname = fluid;
			is144mb = is144;
		}

		private CanType(String fluid) {
			this(fluid, false);
		}

		@Nullable
		public FluidStack getFluid() {
			if (fname == null) return null;
			return FluidRegistry.getFluidStack(fname, is144mb ? 144 : 1000);
		}
	}

	@Nullable
	private static CanType getCanType(FluidStack f) {
		if (f == null || f.getFluid() == null) return null;
		String s = f.getFluid().getName();
		for (CanType t : CanType.values()) {
			String n = t.fname;
			if (n != null && n.equals(s)) return t;
		}
		return null;
	}
}
