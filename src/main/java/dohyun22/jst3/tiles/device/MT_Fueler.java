package dohyun22.jst3.tiles.device;

import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MT_FluidGen;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Fueler extends MetaTileBase {
	private FluidTank tank;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_Fueler ret = new MT_Fueler();
		ret.tank = new MT_FluidGen.FuelTank(16000, ret, -1, 0);
		return ret;
	}

	@Override
	public void onPostTick() {
		if (!isClient() && baseTile.getTimer() % 50 == 0 && tank.getFluid() != null) {
			BlockPos p = getPos();
			List<Entity> el = getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(p).grow(2));
			for (Entity e : el)
				if (!(e instanceof EntityMinecart && !((EntityMinecart)e).isPoweredCart()) && !(e instanceof EntityItem) && Math.abs(e.motionX) + Math.abs(e.motionY) + Math.abs(e.motionZ) < 0.1F && e.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
					FluidStack tr = FluidUtil.tryFluidTransfer(e.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null), tank, tank.getCapacity(), true);
					if (tr != null) {
						SoundEvent ev = tr.getFluid().getEmptySound(tr);
						getWorld().playSound(null, p, ev, SoundCategory.BLOCKS, 0.6F, 1.0F);
					}
				}
		}
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
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
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
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.fueler"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite x = getTieredTex(1);
		return new TextureAtlasSprite[] {x, x, x, x, x, getTETex("fueler")};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = getFacing() == JSTUtils.getFacingFromNum(n) ? getTETex("fueler") : getTieredTex(1);
		return ret;
	}
}
