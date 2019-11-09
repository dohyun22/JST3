package dohyun22.jst3.tiles.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_WaterMaker extends MetaTileEnergyInput {
	private final byte tier;
	private FluidTank tank;

	public MT_WaterMaker(int tier) {
		this.tier = (byte) tier;
	}
	
	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_WaterMaker ret = new MT_WaterMaker(tier);
		ret.tank = new FluidTank(JSTUtils.getVoltFromTier(tier) * 125);
		ret.tank.setCanFill(false);
		return ret;
	}
	
	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		super.getCapability(c, f);
		if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) tank;
		return null;
	}
	
	@Override
	public long getMaxEnergy() {
		return maxEUTransfer() * 100;
	}
	
	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		World w = getWorld();
		if (!isClient()) {
			if (tank.getFluidAmount() > 0 && baseTile.facing != null && !isRSPowered()) {
				int amt = JSTUtils.fillTank(w, getPos(), baseTile.facing, new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), JSTUtils.getVoltFromTier(tier) * 4)));
				if (amt > 0)
					tank.drain(amt, true);
			}
		
			boolean flag = tank.getFluidAmount() < tank.getCapacity();
			baseTile.setActive(flag);
			if (flag) {
				int i = JSTUtils.getVoltFromTier(tier) / 2;
				if (baseTile.energy > i / 2) {
					tank.fillInternal(new FluidStack(FluidRegistry.WATER, i * 2), true);
					baseTile.energy -= i / 2;
				} else {
					tank.fillInternal(new FluidStack(FluidRegistry.WATER, i / 2), true);
				}
			}
		} else if (baseTile.isActive()) {
			if (baseTile.getTimer() % 40 == 0) w.playSound(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, JSTSounds.PUMP, SoundCategory.BLOCKS, 0.25F, 1.0F, false);
			if (w.rand.nextInt(4) == 0) {
	            double x = (double)baseTile.getPos().getX() + 0.5D;
	            double z = (double)baseTile.getPos().getZ() + 0.5D;
	          
	            for (int i = 0; i < 3; i++) {
	    			double y = (double)baseTile.getPos().getY() + (w.rand.nextDouble() * 0.4375 + 0.5D);
	            	double o = w.rand.nextDouble() * 0.6D - 0.3D;
	            	
	            	if (w.isAirBlock(getPos().west()))
	            		w.spawnParticle(EnumParticleTypes.WATER_SPLASH, x - 0.52D, y, z + o, -0.01D, -0.2D, 0.0D, new int[0]);
	            	if (w.isAirBlock(getPos().east()))
	            		w.spawnParticle(EnumParticleTypes.WATER_SPLASH, x + 0.52D, y, z + o, 0.01D, -0.2D, 0.0D, new int[0]);
	            	if (w.isAirBlock(getPos().north()))
	            		w.spawnParticle(EnumParticleTypes.WATER_SPLASH, x + o, y, z - 0.52D, 0.0D, -0.2D, -0.01D, new int[0]);
	            	if (w.isAirBlock(getPos().south()))
	            		w.spawnParticle(EnumParticleTypes.WATER_SPLASH, x + o, y, z + 0.52D, 0.0D, -0.2D, 0.01D, new int[0]);
	            }
			}
		}
	}

	@Override
	public boolean isEnergyInput(EnumFacing side) {
		if (side == null) return true;
		if (baseTile != null) {
			return side != baseTile.facing;
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite ws = getTETex("watergen_side");
		return new TextureAtlasSprite[] {getTieredTex(tier), getTieredTex(tier), ws, ws, ws, ws};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (baseTile.facing == JSTUtils.getFacingFromNum(n))
				ret[n] = getTETex("fl_out");
			else
				ret[n] = n > 1 ? getTETex("watergen_side") : getTieredTex(tier);
		}
		return ret;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.waterpump.desc"));
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, false);
	}
	
	@Override
	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		return doSetFacing(f, false);
	}
}
