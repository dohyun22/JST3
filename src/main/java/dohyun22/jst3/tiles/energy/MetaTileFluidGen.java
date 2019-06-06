package dohyun22.jst3.tiles.energy;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIFluidTank;
import dohyun22.jst3.container.ContainerFluidGen;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileFluidGen extends MetaTileGenerator {
	public int burningFuel;
	/** Diesel Gas Steam Thermal*/
	public final byte type;
	private FluidTank tank;

	public MetaTileFluidGen(int tier, int type) {
		super(tier, true);
		this.type = (byte)type;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MetaTileFluidGen ret = new MetaTileFluidGen(tier, type);
		ret.tank = new FuelTank(16000, ret, 2, type);
		return ret;
	}
	
	@Override
	protected void checkCanGenerate() {
		this.baseTile.setActive(burningFuel > 0);
	}
	
	@Override
	protected void doGenerate() {
		int use = Math.min(burningFuel, JSTUtils.getVoltFromTier(tier));
		burningFuel -= use;
		baseTile.energy += use;
	}
	
	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}

	@Override
	public void onPreTick() {
		if (this.isClient() || this.burningFuel > 0) return;
		int fv = getFuelValue(this.type, this.tank.getFluid());
		if (fv > 0) {
			int m = Math.max((int) Math.ceil(maxEUTransfer() / ((double)fv)), 1);
			FluidStack fs = this.tank.drain(m, true);
			this.burningFuel = fv * (fs == null ? 0 : fs.amount);
		}
	}
	
	public static int getFuelValue(byte t, FluidStack fs) {
		if (fs == null) return 0;
		String fn = fs.getFluid().getName();
		Integer ret = null;
		switch (t) {
		case 0:
			ret = MRecipes.DieselGenFuel.get(fn);
			break;
		case 1:
			ret = MRecipes.GasGenFuel.get(fn);
			break;
		case 2:
			ret = MRecipes.AcceptableSteam.get(fn);
			break;
		case 3:
			ret = MRecipes.HeatGenFuel.get(fn); 
		}
		return ret == null ? 0 : ret.intValue();
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient()) {
			World w = getWorld();
			if (baseTile.isActive()) {
				if (type == 0 && baseTile.getTimer() % 50 == 0)
					w.playSound(getPos().getX() + 0.5D, getPos().getY(), getPos().getZ() + 0.5D, JSTSounds.ENGINE, SoundCategory.BLOCKS, 0.8F, 0.8F, false);
				if (w.rand.nextInt(8) == 0) {
					EnumParticleTypes pt = null;
					switch (type) {
					case 0:
						pt = EnumParticleTypes.SMOKE_NORMAL;
						break;
					case 1:
						pt = EnumParticleTypes.FLAME;
						break;
					case 2:
						pt = EnumParticleTypes.EXPLOSION_NORMAL;
						break;
					}
					
					if (pt != null) {
						for (int i = 0; i < 2; i++) {
							double x = this.getPos().getX() + 0.5D + w.rand.nextFloat() * 0.8D - 0.4D;
							double y = this.getPos().getY() + 1 + w.rand.nextFloat() * 0.2D;
							double z = this.getPos().getZ() + 0.5D + w.rand.nextFloat() * 0.8D - 0.4D;
							w.spawnParticle(pt, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
						}
					}
				}
			}
		} else if (!this.inv.get(0).isEmpty()) {
			JSTUtils.drainFluidItemInv(tank, 1000, this.baseTile, 0, 1);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		String str = "error";
		switch (this.type) {
		case 0:
			str= "dieselgen";
			break;
		case 1:
			str= "gasgen";
			break;
		case 2:
			str= "steamgen";
			break;
		case 3:
			str= "lavagen";
			break;
		}
		return new TextureAtlasSprite[] {getTieredTex(tier), getTETex(str), getTieredTex(tier), getTieredTex(tier), getTieredTex(tier), getTieredTex(tier)};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		String str = "error";
		switch (this.type) {
		case 0:
			str= "dieselgen";
			break;
		case 1:
			str= "gasgen";
			break;
		case 2:
			str= "steamgen";
			break;
		case 3:
			str= "lavagen";
			break;
		}
		return new TextureAtlasSprite[] {getTieredTex(tier), getTETex(str + (this.baseTile.isActive() ? "" : "_off")), getTieredTex(tier), getTieredTex(tier), getTieredTex(tier), getTieredTex(tier)};
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile == null || isClient()) {
			return true;
		}
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.burningFuel = tag.getInteger("fuel");
		tank.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("fuel", this.burningFuel);
		tank.writeToNBT(tag);
	}
	
	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) tank;
		return super.getCapability(c, f);
	}
	
	@Override
	public boolean canProvideEnergy() {
		return true;
	}
	
	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return super.isItemValidForSlot(sl, st) && sl == 0 && MetaTileFluidGen.getFuelValue(type, FluidUtil.getFluidContained(st)) > 0;
	}
	
    @Override
    public boolean canExtractItem(int sl, ItemStack st, EnumFacing f) {
    	return sl == 1;
    }
	
	@Override
	public int getInvSize() {
		return 3;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerFluidGen(inv, te);
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		String str = null;
		if (Loader.isModLoaded("jei")) {
			if (type == 0)
				str = "diesel";
			else if (type == 1)
				str = "gas";
			else if (type == 2)
				str = "steam";
			else if (type == 3)
				str = "heat";
		}
		return new GUIFluidTank(new ContainerFluidGen(inv, te), str == null ? null : new String[] {JustServerTweak.MODID + "." + str + "fuel"});
	}
	
	@Override
	public boolean canSlotDrop(int num) {
		return num != 2;
	}
	
	@Override
	public int getComparatorInput() {
		int ta = tank.getFluidAmount();
		int tc = tank.getCapacity();
		if (ta <= 0 || tc <= 0) return 0;
		return ta * 15 / tc;
	}

	@Override
	public int getDust() {
		int r = 0;
		switch (type) {
		case 0: r = 15; break;
		case 1: r = 10; break;
		case 3: r = 5; break;
		}
		r *= JSTUtils.getVoltFromTier(tier) / 32;
		return r;
	}
	
	public static class FuelTank extends MTETank {
		private final byte ty;

	    public FuelTank(int amt, MetaTileBase ret, int sl, int t) {
	        super(amt, true, true, ret, sl);
	        ty = (byte) t;
	    }

	    @Override
	    public boolean canFillFluidType(FluidStack fs) {
	    	return getFuelValue(ty, fs) > 0;
	    }
	}
}
