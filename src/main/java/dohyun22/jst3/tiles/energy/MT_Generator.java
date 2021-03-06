package dohyun22.jst3.tiles.energy;

import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.FineDustCapability;
import dohyun22.jst3.api.IDust;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.api.IScrewDriver;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.capabilities.Capability;

public abstract class MT_Generator extends MetaTileBase implements IScrewDriver, IDust {
	protected final int tier;
	protected final boolean canRSControl;
	protected boolean rsPowered;
	/** 0: ignore signal, 1: HIGH, 2: LOW */
	protected byte mode;
	
	public MT_Generator(int tier, boolean rsc) {
		this.tier = tier;
		this.canRSControl = rsc;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if (canRSControl) {
			rsPowered = tag.getBoolean("RS");
			mode = tag.getByte("Mode");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		if (canRSControl) {
			if (rsPowered) tag.setBoolean("RS", true);
			if (mode != 0) tag.setByte("Mode", mode);
		}
	}
	
	@Override
	public long getMaxEnergy() {
		return maxEUTransfer() * 400;
	}
	
	@Override
	public void onPostTick() {
		if (isClient()) return;
		
		if (checkRSBehavior()) {
			checkCanGenerate();
			if (baseTile.isActive() && baseTile.energy < getMaxEnergy())
				doGenerate();
		}
		
		if (baseTile.energy > 0) for (EnumFacing f : EnumFacing.VALUES) injectEnergyToSide(f);
	}
	
	@Override
	public void onBlockUpdate() {
		if (!isClient() && canRSControl) {
			boolean b = isRSPowered();
			if (b != rsPowered) {
				rsPowered = b;
				if (mode != 0)
					getWorld().playSound(null, getPos(), JSTSounds.SWITCH, SoundCategory.BLOCKS, 0.75F, getWorld().rand.nextFloat() * 0.2F + 0.9F);
			}
		}
	}
	
	@Override
	public boolean canConnectRedstone(EnumFacing f) {
		return canRSControl;
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.add(I18n.format("jst.tooltip.tile.com.sd.rs"));
		int d = getDust();
		if (d > 0) ls.add(I18n.format("jst.tooltip.tile.com.dust", FineDustCapability.toMicrogram(d * 60)));
	}
	
	@Override
	public int getComparatorInput() {
		return baseTile.isActive() ? 15 : 0;
	}
	
	@Override
	public boolean onScrewDriver(EntityPlayer pl, EnumFacing f, double px, double py, double pz) {
		if (!canRSControl) return false;
		mode++;
		if (mode < 0 || mode > 2)
			mode = 0;
		JSTUtils.sendMessage(pl, "jst.msg.com.rs" + mode);
		baseTile.issueUpdate();
		return true;
	}
	
	protected boolean checkRSBehavior() {
		if (!canRSControl || mode == 0) return true;
		switch (mode) {
		case 1:
			return rsPowered;
		case 2:
			return !rsPowered;
		}
		return false;
	}

	protected void checkCanGenerate() {
	}
	
	/** @return How many energy in EU that generated by this generator */
	protected long getPower() {
		return maxEUTransfer();
	}
	
	protected void doGenerate() {
		baseTile.energy += getPower();
	}

	@Override
	public boolean canGen() {
		return baseTile.isActive();
	}

	@Override
	public int getDust() {
		return 0;
	}

	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		if (c == FineDustCapability.FINEDUST && getDust() > 0)
			return FineDustCapability.FINEDUST.cast(this);
		return null;
	}
}
