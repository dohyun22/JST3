package dohyun22.jst3.items.behaviours;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_Flamethrower extends ItemBehaviour {

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		FluidStack fs = FluidUtil.getFluidContained(st);
		if (fs != null && getFuelVal(fs.getFluid()) > 0) {
			if (!w.isRemote && !pl.isHandActive())
				w.playEvent(1018, pl.getPosition(), 0);
			pl.setActiveHand(h);
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
    public void onUsingTick(ItemStack st, EntityLivingBase e, int cnt) {
		World w = e.world;
		if (e.isElytraFlying() && e.posY <= 300) {
			if (!w.isRemote && w.getTotalWorldTime() % 5 == 0)
				w.playSound(null, e.posX, e.posY, e.posZ, JSTSounds.FLAME, SoundCategory.PLAYERS, 1.0F, 1.0F);
			Vec3d d = e.getLookVec();
            e.motionX += d.x * 0.05D + (d.x - e.motionX) * 0.25D;
            e.motionY += d.y * 0.05D + (d.y - e.motionY) * 0.25D;
            e.motionZ += d.z * 0.05D + (d.z - e.motionZ) * 0.25D;
            if (w instanceof WorldServer)
				for (int n = 0; n < 4; n++)
					((WorldServer)w).spawnParticle(EnumParticleTypes.FLAME, false, e.posX, e.posY, e.posZ, 0, -d.x + w.rand.nextFloat() * 0.2F - 0.1F, -d.y + w.rand.nextFloat() * 0.3F - 0.15F, -d.z + w.rand.nextFloat() * 0.2F - 0.1F, 1.0D);
		}
		if (w == null || w.isRemote || !st.hasTagCompound()) return;
		int du = getMaxItemUseDuration(st) - cnt;
		boolean u = st.getTagCompound().getBoolean("UPG");
		FluidStack fs = FluidUtil.getFluidContained(st);
		if (!(e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode) && (fs == null || du * getFuelVal(fs.getFluid()) > fs.amount)) {
			e.stopActiveHand();
			return;
		}
		if (!e.isElytraFlying()) throwFlame(w, e, null, 3, u ? 20 : 13);
	}

	@Override
	public void onStopUsing(ItemStack st, World w, EntityLivingBase e, int t) {
		if (e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode) return;
		IFluidHandlerItem fh = FluidUtil.getFluidHandler(st);
		FluidStack fs = FluidUtil.getFluidContained(st);
		if (fh != null && fs != null) {
			int fv = getFuelVal(fs.getFluid());
			fh.drain((getMaxItemUseDuration(st) - t) * fv, true);
		}
	}

	@Override
    public EnumAction useAction(ItemStack st) {
        return EnumAction.BOW;
    }

	@Override
	public boolean hasCapability(Capability<?> cap, ItemStack st) {
		return cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> cap, ItemStack st) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
			return (T) new InternalTank(st);
		return null;
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack st) {
		return 72000;
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		IFluidHandlerItem fh = FluidUtil.getFluidHandler(st);
		if (fh != null) {
			fh.fill(new FluidStack(JSTFluids.fuel, 4000), true);
			sub.add(st);
			st = st.copy();
			JSTUtils.getOrCreateNBT(st).setBoolean("UPG", true);
			sub.add(st);
		}
	}

	@Override
	public boolean showDurability(ItemStack st) {
		return st.hasTagCompound() ? st.getTagCompound().getCompoundTag("Fluid").getInteger("Amount") > 0 : false;
	}

	@Override
	public double getDisplayDamage(ItemStack st) {
		return st.hasTagCompound() ? 1.0D - (double)st.getTagCompound().getCompoundTag("Fluid").getInteger("Amount") / 4000.0D : 0.0D;
	}

	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot sl, ItemStack st) {
		if (sl != EntityEquipmentSlot.MAINHAND) return null;
		Multimap<String, AttributeModifier> ret = HashMultimap.create();
        ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DamageUUID, "Weapon modifier", 1.0D, 0));
	    return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		ArrayList<String> ret = new ArrayList();
		ret.addAll(JSTUtils.getListFromTranslation("jst.tooltip.flamethrower", st.hasTagCompound() && st.getTagCompound().getBoolean("UPG") ? 20 : 13));
		addFluidTip(st, ret);
		return ret;
	}

	public static void throwFlame(World w, Object o, Vec3d ag, int pwr, int rng) {
		if (w.isRemote) return;
		Vec3d sp = null; Entity e = null;
		if (o instanceof Entity) {
			e = (Entity) o;
			sp = ((Entity)o).getPositionVector().addVector(0, ((Entity)o).getEyeHeight(), 0);
			if (ag == null) ag = e.getLookVec();
		}
		if (o instanceof TileEntity && ag != null)
			sp = new Vec3d(((TileEntity)o).getPos()).addVector(0.5F, 0.5F, 0.5F);

		if (sp != null && ag != null && pwr > 0 && rng > 0) {
			if (w.getTotalWorldTime() % 5 == 0)
				w.playSound(null, sp.x, sp.y, sp.z, JSTSounds.FLAME, SoundCategory.PLAYERS, 1.0F, 1.0F);
			if (w instanceof WorldServer) {
				double ps = Math.max(0.0075D * rng, 0.12D);
				for (int n = 0; n < 4; n++) {
					double vx = ag.x + w.rand.nextFloat() * ps - 0.075F;
					double vy = ag.y + w.rand.nextFloat() * ps - 0.075F;
					double vz = ag.z + w.rand.nextFloat() * ps - 0.075F;
					((WorldServer)w).spawnParticle(EnumParticleTypes.FLAME, true, sp.x + ag.x, sp.y + ag.y, sp.z + ag.z, 0, vx, vy, vz, 1.0D);
				}
			}
			double sz = 1.2D;
			MutableBlockPos p = new MutableBlockPos();
			for (int n = 1; n < rng; n++) {
				double px = sp.x + ag.x * n;
				double py = sp.y + ag.y * n;
				double pz = sp.z + ag.z * n;
				p.setPos(px, py, pz);
				IBlockState bs = w.getBlockState(p);
				if (bs.getMaterial().blocksMovement() || bs.getMaterial().isLiquid()) break;
				List<EntityLivingBase> el =  w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(px - sz, py - sz, pz - sz, px + sz, py + sz, pz + sz));
				for (EntityLivingBase elb : el) {
					if (e != null && (elb == e || elb == e.getRidingEntity())) continue;
					DamageSource ds = e == null ? JSTDamageSource.FLAME : JSTDamageSource.causeEntityDamage("flame", e).setFireDamage();
					if (elb.attackEntityFrom(ds, pwr)) {
						elb.setFire(2 * pwr);
						if (w.rand.nextInt(30) == 0) elb.hurtResistantTime = 0;
					}
				}
			}
		}
	}

	public static int getFuelVal(Fluid f) {
		if (f != null) {
			String s = f.getName();
			if (s != null) {
				switch (s) {
				case "napalm":
				case "nitrofuel": return 1;
				case "rocketfuel":
				case "refined_fuel":
				case "fuel":
				case "gasoline":
				case "liquid_lpg": return 2;
				case "liquid_lng":
				case "refined_oil": return 3;
				case "biofuel":
				case "refined_biofuel":
				case "ethanol":
				case "ic2biogas":
				case "bio.ethanol": return 4;
				case "heavyfuel": return 6;
				case "creosote": return 10;
				case "oil":
				case "crude_oil":
				case "coal": return 15;
				}
			}
		}
		return 0;
	}

	private static class InternalTank extends FluidHandlerItemStack {
		InternalTank(ItemStack c) {
			super(c, 4000);
		}

		@Override
		public int fill(FluidStack fs, boolean fill) {
			if (fs != null && getFuelVal(fs.getFluid()) > 0)
				return super.fill(fs, fill);
			return 0;
		}
		
		@Override
		protected void setContainerToEmpty() {
			super.setContainerToEmpty();
			if (container.getTagCompound().hasNoTags()) container.setTagCompound(null);
		}
	}
}
