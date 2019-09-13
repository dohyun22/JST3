package dohyun22.jst3.items.behaviours;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class IB_FireExtinguisher extends ItemBehaviour {

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (!w.isRemote) {
			if (!pl.isCreative()) {
				RayTraceResult r = JSTUtils.rayTraceEntity(pl, true, false, false, 0);
				if (r != null && r.typeOfHit == RayTraceResult.Type.BLOCK) {
					FluidActionResult fr = FluidUtil.tryPickUpFluid(st, pl, w, r.getBlockPos(), r.sideHit);
					if (fr.success) {
						st = fr.result;
						return new ActionResult(EnumActionResult.PASS, st);
					}
				}
			}
			if (FluidUtil.getFluidContained(st) != null) {
				if (!w.isRemote && !pl.isHandActive())
					w.playSound(null, pl.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 0.5F);
				pl.setActiveHand(h);
			}
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
    public void onUsingTick(ItemStack st, EntityLivingBase e, int cnt) {
		World w = e.world;
		if (w.isRemote) return;
		RayTraceResult r = JSTUtils.rayTraceEntity(e, true, false, false, 12);
		if (r != null) {
			if (r.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos p = JSTUtils.getOffset(r.getBlockPos(), r.sideHit, 1);
				MutableBlockPos p2 = new MutableBlockPos();
				int n = cnt % 3 == 0 ? 2 : 1;
				for (int x = -n; x <= n; x++)
					for (int y = -n; y <= n; y++)
						for (int z = -n; z <= n; z++) {
							if (w.rand.nextBoolean()) continue;
							p2.setPos(x + p.getX(), y + p.getY(), z + p.getZ());
							IBlockState bs = w.getBlockState(p2);
							if (bs.getMaterial() == Material.FIRE) {
								w.setBlockToAir(p2);
								w.playSound(null, p2, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
							}
						}
			    for (Entity e2 : w.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(p.add(-2, -2, -2), p.add(2, 2, 2)))) {
			        e2.extinguish();
			        if (e2 instanceof EntityLivingBase)
			        	((EntityLivingBase)e2).knockBack(e, 0.3F, MathHelper.sin(e.rotationYaw * 0.017453292F), -MathHelper.cos(e.rotationYaw * 0.017453292F));
			    }
			}
		}
		Vec3d v = e.getLookVec(), sp = e.getPositionVector().addVector(0, e.getEyeHeight(), 0);
		JSTPacketHandler.addParticle(w, EnumParticleTypes.EXPLOSION_NORMAL.ordinal(), 4, 4, sp.x + v.x * 1.5D, sp.y + v.y * 1.5D, sp.z + v.z * 1.5D, v.x, v.y, v.z);
		e.extinguish();
		FluidStack fs = FluidUtil.getFluidContained(st);
		if (!(e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode) && (fs == null || (getMaxItemUseDuration(st) - cnt) * 10 > fs.amount)) {
			e.stopActiveHand();
			return;
		}
	}

	@Override
	public void onStopUsing(ItemStack st, World w, EntityLivingBase e, int t) {
		if (e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode) return;
		IFluidHandlerItem fh = FluidUtil.getFluidHandler(st);
		FluidStack fs = FluidUtil.getFluidContained(st);
		if (fh != null && fs != null)
			fh.drain((getMaxItemUseDuration(st) - t) * 10, true);
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
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
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
			fh.fill(new FluidStack(FluidRegistry.WATER, 4000), true);
			sub.add(st);
		}
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot sl, ItemStack st) {
		if (sl != EntityEquipmentSlot.MAINHAND)
			return null;
		Multimap<String, AttributeModifier> ret = HashMultimap.create();
        ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DamageUUID, "Weapon modifier", 4.0D, 0));
        ret.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SpeedUUID, "Weapon modifier", -1.6D, 0));
	    return ret;
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
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		addFluidTip(st, ls);
	}

	private static class InternalTank extends FluidHandlerItemStack {
		InternalTank(ItemStack c) {
			super(c, 4000);
		}

		@Override
		public int fill(FluidStack fs, boolean fill) {
			if (fs != null) {
				String n = fs.getFluid().getName();
				if ("water".equals(n) || "carbondioxide".equals(n))
					return super.fill(fs, fill);
			}
			return 0;
		}

		@Override
		protected void setContainerToEmpty() {
			super.setContainerToEmpty();
			if (container.getTagCompound().hasNoTags()) container.setTagCompound(null);
		}
	}
}