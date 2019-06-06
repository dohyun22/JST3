package dohyun22.jst3.tiles.machine;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.MultiTankHandler;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

public abstract class MT_Machine extends MetaTileEnergyInput implements IGenericGUIMTE {
	protected final int tier;
	protected int progress;
	protected int mxprogress;
	/** Energy Use Per Tick */
	protected int energyUse;
	private byte cooldown;
	/** If true, The Machine will reset its progress when provided energy is not sufficient */
	protected boolean canIEL;
	protected int batterySlot = -1;
	@Nullable
	protected MultiTankHandler tankHandler = null;

	public MT_Machine(int tier) {
		this.tier = tier;
	}

	@Override
	public void onPostTick() {
	    super.onPostTick();
	    if (isClient()) return;
	    if (this.batterySlot >= 0) {
	    	injectEnergy(null, JSTUtils.dischargeItem(inv.get(batterySlot), Math.min(getMaxEnergy() - baseTile.energy, maxEUTransfer()), tier, false, false), false);
	    }
	    if (isInCooldown()) this.cooldown = (byte)(cooldown - 1);
	    if (baseTile.isActive()){
	    	doWork();
	    } else if (baseTile.getTimer() % 40L == 0L && baseTile.energy > 0L && !isInCooldown() && checkCanWork()) {
	    	onStartWork();
	    	toggleMachine(true);
	    }
	}

	protected abstract boolean checkCanWork();

	protected abstract void finishWork();

	protected boolean isInCooldown() {
		return this.cooldown > 0;
	}

	protected void onStartWork() {}

	protected void stopWorking(boolean interrupt) {
		if (interrupt)
			interrupt(80);
		toggleMachine(false);
		clear(false);
	}

	protected void interrupt(int cd) {
		this.cooldown = ((byte) cd);
		getWorld().playSound(null, getPos(), JSTSounds.INTERRUPT, SoundCategory.BLOCKS, 1.0F, getWorld().rand.nextFloat() * 0.4F + 0.8F);
	}

	protected void doWork() {
		if (energyUse > 0) {
			if (cooldown > 0)
				return;
			int m = JSTUtils.getMultiplier(this.tier, this.energyUse);
			int u = energyUse * m;
			if (baseTile.energy >= u) {
				baseTile.energy -= u;
				if (baseTile.energy < u) {
					if (canIEL)
						stopWorking(true);
					else
						interrupt(80);
					return;
				}
				progress += Math.max(1, m);
				if (progress >= mxprogress) {
					finishWork();
					clear(true);
					if (!checkCanWork())
						toggleMachine(false);
					else
						onStartWork();
				}
			}
		} else {
			stopWorking(true);
		}
	}
	
	/** @return true if machine state has been changed. */
	protected boolean toggleMachine(boolean on) {
		return baseTile.setActive(on);
	}
	
	/** @param cr Will clear Work Result(such as ItemStack/FluidStack) if true */
	protected void clear(boolean cr) {
		energyUse = 0;
		progress = 0;
		mxprogress = 0;
	}

	@Override
	public int getPrg() {
		return Math.min(progress, mxprogress);
	}

	@Override
	public int getMxPrg() {
		return this.mxprogress;
	}

	@Override
	public void setInventorySlotContents(int sl, ItemStack st) {
		ItemStack st2 = (ItemStack) inv.get(sl);
		inv.set(sl, st);
		if (st.getCount() > baseTile.getInventoryStackLimit())
			st.setCount(baseTile.getInventoryStackLimit());
		if (!isClient() && isInputSlot(sl) && baseTile.energy > 0L && !isInCooldown() && !baseTile.isActive() && checkCanWork()) {
			onStartWork();
			toggleMachine(true);
		}
	}

	protected boolean isInputSlot(int sl) {
		return false;
	}

	@Override
	public long getMaxEnergy() {
		return maxEUTransfer() * 100;
	}

	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(this.tier);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energyUse = tag.getInteger("ept");
		progress = tag.getInteger("prg");
		mxprogress = tag.getInteger("mprg");
		cooldown = tag.getByte("cd");
		if (tankHandler != null) {
			tankHandler.readFromNBT(tag.getCompoundTag("fluid"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("ept", energyUse);
		tag.setInteger("prg", progress);
		tag.setInteger("mprg", mxprogress);
		if (cooldown > 0) {
			tag.setByte("cd", cooldown);
		}
		if (this.tankHandler != null) {
			NBTTagCompound tag2 = new NBTTagCompound();
			this.tankHandler.writeToNBT(tag2);
			tag.setTag("fluid", tag2);
		}
	}
	
	@Override
	public int getComparatorInput() {
		if (progress <= 0 || mxprogress <= 0) return 0;
		return progress * 15 / mxprogress;
	}

	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		super.getCapability(c, f);
		if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this.tankHandler != null)
			return (T) this.tankHandler;
		return null;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric ret = new ContainerGeneric(inv, te);
		addSlot(ret, inv, te);
		ret.addPlayerSlots(inv);
		return ret;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric ret = new GUIGeneric((ContainerGeneric) getServerGUI(id, inv, te));
		addComp(ret);
		return ret;
	}
	
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {}

	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {}
}
