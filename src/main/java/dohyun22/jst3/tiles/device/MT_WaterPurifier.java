package dohyun22.jst3.tiles.device;

import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;

public class MT_WaterPurifier extends MetaTileEnergyInput {
	public static final String[] supported = {"water", "ic2distilled_water", "distilled_water", "saltwater", "ice", "dirtywater", "sludge", "sewage", "mud"};
	private MTETank tank;
	private int watr;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_WaterPurifier ret = new MT_WaterPurifier();
		ret.tank = new MTETank(16000, false, true, ret, -1, false, supported);
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
	public void readFromNBT(NBTTagCompound tag) {
		tank.readFromNBT(tag);
		watr = tag.getInteger("watr");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tank.writeToNBT(tag);
		tag.setInteger("watr", watr);
	}

	@Override
	public int maxEUTransfer() {
		return 32;
	}

	@Override
	public long getMaxEnergy() {
		return 1024;
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		World w = getWorld();
		if (w == null || w.isRemote || baseTile.energy < 2)
			return;
		if (tank.getFluidAmount() > 0 && watr + 250 <= 16000) {
			baseTile.energy -= 2;
			if (baseTile.getTimer() % 100 == 0) {
				FluidStack fs = tank.drainInternal(250, true);
				if (fs != null)
					watr += fs.amount;
			}
		}
		if (baseTile.getTimer() % 100 == 0) {
			TileEntity te = w.getTileEntity(getPos().up());
			if (te != null) {
				IFluidHandler fh = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
				if (fh != null) {
					FluidStack fs = fh.drain(Math.min(tank.getCapacity() - tank.getFluidAmount(), 1000), false);
					fh.drain(tank.fill(fs, true), true);
				}
			}
		}
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hX, float hY, float hZ) {
		if (!isClient()) {
			if (!st.isEmpty() && pl.getHeldItem(EnumHand.MAIN_HAND) == st && FluidUtil.interactWithFluidHandler(pl, EnumHand.MAIN_HAND, tank))
				return true;
			if (watr >= 250) {
				if (st.isEmpty()) {
					if (Loader.isModLoaded("toughasnails"))
					try {
						IThirst td = ThirstHelper.getThirstData(pl);
						int ct = td.getThirst();
						if (ct < 20) {
							td.setThirst(ct + 8);
							td.setHydration(10.0f);
							td.setExhaustion(0.0f);
							getWorld().playSound(null, getPos(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.BLOCKS, 0.6F, 1.0F);
							watr -= 250;
						}
					} catch (Throwable t) {}
				} else if (st.getItem() == Items.GLASS_BOTTLE) {
					st.shrink(1);
					ItemStack st2 = JSTUtils.getModItemStack("toughasnails:purified_water_bottle");
					if (st2.isEmpty()) {
						st2 = new ItemStack(Items.POTIONITEM);
						JSTUtils.getOrCreateNBT(st2).setString("Potion", "minecraft:water");
					}
					JSTUtils.giveItem(pl, st2);
					watr -= 250;
					getWorld().playSound(null, getPos(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.6F, 1.0F);
				} else {
					IFluidHandlerItem cap = st.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
					if (cap != null)
						watr -= cap.fill(new FluidStack(FluidRegistry.WATER, watr), true);
				}
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(1);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("waterpurifier")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = baseTile.facing == JSTUtils.getFacingFromNum(n) ? getTETex("waterpurifier") : getTieredTex(1);
		return ret;
	}
}
