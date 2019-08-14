package dohyun22.jst3.tiles.device;

import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.items.behaviours.IB_Flamethrower;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_FlameTrap extends MetaTileBase {
	private EntityLivingBase target;
	private FluidTank tank;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_FlameTrap ret = new MT_FlameTrap();
		ret.tank = new FuelTank(ret);
		return ret;
	}

	@Override
	public void onPostTick() {
		if (isClient()) return;
		boolean flag = baseTile.getTimer() % 20 == 0;
		FluidStack fs = tank.getFluid();
		int c = IB_Flamethrower.getFuelVal(fs == null ? null : fs.getFluid());
		EnumFacing f = baseTile.facing;
		if (c <= 0 || baseTile.isActive() || f == null) {
			target = null;
			return;
		}
		if (target == null) {
			if (flag) {
				List<EntityLivingBase> ls = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, getArea());
				for (EntityLivingBase e : ls) {
					if (canAttack(e)) {
						target = e;
						getWorld().playEvent(1018, getPos(), 0);
						break;
					}
				}
			}
		} else {
			if (flag) {
				if (!canAttack(target)) {
					target = null;
					return;
				}
				tank.drainInternal(c * 2, true);
			}
			IB_Flamethrower.throwFlame(getWorld(), baseTile, new Vec3d(f.getFrontOffsetX(), f.getFrontOffsetY(), f.getFrontOffsetZ()), 4, 10);
		}
	}

	private boolean canAttack(EntityLivingBase e) {
		return e instanceof IMob && !e.isDead && e.getDistanceSqToCenter(getPos()) <= 100 && !e.isImmuneToFire() && getArea().intersects(e.getEntityBoundingBox());
	}

	private AxisAlignedBB getArea() {
		BlockPos p = getPos(); int r = 10; EnumFacing f = baseTile.facing;
		for (int n = 1; n <= r; n++) {
			IBlockState bs = getWorld().getBlockState(p.offset(f, n));
			if (bs.getMaterial().blocksMovement() || bs.getMaterial().isLiquid()) {
				r = n - 1;
				break;
			}
		}
		return new AxisAlignedBB(p).grow(0.5).expand(f.getFrontOffsetX() * r, f.getFrontOffsetY() * r, f.getFrontOffsetZ() * r);
	}

	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) tank;
		return super.getCapability(c, f);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		tank.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tank.writeToNBT(tag);
	}

	@Override
	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		return doSetFacing(f, false);
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile == null) return;
		baseTile.facing = JSTUtils.getClosestSide(p, elb, st, false);
		onBlockUpdate();
	}

	@Override
	public void onBlockUpdate() {
		if (!isClient()) baseTile.setActive(isRSPowered());
	}

	@Override
	public boolean canConnectRedstone(EnumFacing f) {
		return true;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!isClient()) {
			if (!heldItem.isEmpty() && pl.getHeldItem(EnumHand.MAIN_HAND) == heldItem && FluidUtil.interactWithFluidHandler(pl, EnumHand.MAIN_HAND, tank))
				return true;
			JSTUtils.sendChatTrsl(pl, "jst.msg.com.tanksimple", tank.getFluidAmount(), tank.getCapacity());
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = getTETex("basic_side");
		return new TextureAtlasSprite[] {s, getTETex("vent"), s, s, s, s};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = getSingleTETex("basic_side");
		for (byte n = 0; n < ret.length; n++) {
			EnumFacing f = JSTUtils.getFacingFromNum(n);
			if (baseTile.facing == f) {ret[n] = getTETex("vent"); break;}
		}
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.flamethrower"));
	}

	private static class FuelTank extends MTETank {

	    public FuelTank(MetaTileBase mte) {
	        super(16000, false, true, mte, -1);
	    }

	    @Override
	    public boolean canFillFluidType(FluidStack fs) {
	    	return fs != null && IB_Flamethrower.getFuelVal(fs.getFluid()) > 0;
	    }
	}
}
