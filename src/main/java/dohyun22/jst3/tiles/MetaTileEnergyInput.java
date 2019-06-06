package dohyun22.jst3.tiles;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.prefab.BasicSink;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.info.ILocatable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;

public abstract class MetaTileEnergyInput extends MetaTileBase {
	private Object ic2EnetObj;
	protected boolean checked;
	
	@Override
	public void onPostTick() {
		if (JSTCfg.ic2Loaded && !checked)
			checkIC2();
	}
	
	@Override
	public void invalidate() {
		if (JSTCfg.ic2Loaded)
			handleEnetObj(false);
	}
	
	@Override
	public void onChunkUnload() {
		if (JSTCfg.ic2Loaded)
			handleEnetObj(false);
	}
	
	@Override
	public void onBlockUpdate() {
		if (!isClient() && canAcceptEnergy() && JSTCfg.ic2Loaded) checked = false;
	}

	@Override
	public boolean canAcceptEnergy() {
		return true;
	}
	
	@Override
	public boolean isEnergyInput(EnumFacing side) {
		return true;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		this.onBlockUpdate();
	}
	
	@Method(modid="ic2")
	protected void checkIC2() {
		if (isClient()) return;
		for (EnumFacing dir : EnumFacing.VALUES) {
			IEnergyTile et = EnergyNet.instance.getSubTile(this.getWorld(), this.getPos().offset(dir));
			if (et instanceof IC2Input) continue;
			if (et instanceof IEnergyEmitter && ((IEnergyEmitter)et).emitsEnergyTo(null, dir.getOpposite())) {
				handleEnetObj(true);
				break;
			}
		}
		checked = true;
	}
	
	@Method(modid="ic2")
	protected void handleEnetObj(boolean load) {
		if (load) {
			if (ic2EnetObj == null) {
				ic2EnetObj = new IC2Input();
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) ic2EnetObj));
			}
		} else if (ic2EnetObj != null) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) ic2EnetObj));
			ic2EnetObj = null;
		}
	}
	
	@Optional.InterfaceList({
	@Optional.Interface(iface="ic2.api.energy.tile.IEnergySink", modid="ic2"),
	@Optional.Interface(iface="ic2.api.info.ILocatable", modid="ic2")
	})
	protected class IC2Input implements IEnergySink, ILocatable {
		@Override
		@Method(modid="ic2")
		public boolean acceptsEnergyFrom(IEnergyEmitter e, EnumFacing s) {
			return canAcceptEnergy() && isEnergyInput(s);
		}

		@Override
		@Method(modid="ic2")
		public double getDemandedEnergy() {
			return maxEUTransfer();
		}

		@Override
		@Method(modid="ic2")
		public int getSinkTier() {
			return Integer.MAX_VALUE;
		}

		@Override
		@Method(modid="ic2")
		public double injectEnergy(EnumFacing s, double amt, double v) {
			return Math.max(0, amt - MetaTileEnergyInput.this.injectEnergy(JSTUtils.getOppositeFacing(s), (long)amt, false));
		}

		@Override
		@Method(modid="ic2")
		public BlockPos getPosition() {
			return getPos();
		}

		@Override
		@Method(modid="ic2")
		public World getWorldObj() {
			return getWorld();
		}
	}
}
