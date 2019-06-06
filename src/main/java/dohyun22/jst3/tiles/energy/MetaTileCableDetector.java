package dohyun22.jst3.tiles.energy;

import java.util.ArrayList;
import java.util.List;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class MetaTileCableDetector extends MetaTileCable {
	private boolean on = false;
	
	public MetaTileCableDetector() {
		super("cable_dt", 8192, (byte)2, (byte)4, 5, (byte)5);
	}
	
	@Override
	public void onPostTick() {
		if (isClient()) return;
		if (!on) {
			if (volt > 0) {
				on = true;
				update();
			}
		} else if (baseTile.getTimer() % 20 == 0 && volt <= 0) {
			on = false;
			update();
		}
	}
	
	private void update() {
		getWorld().notifyNeighborsOfStateChange(this.getPos(), getWorld().getBlockState(getPos()).getBlock(), true);
	}
	
	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileCableDetector();
	}
	
	@Override
	public boolean canConnectRedstone(EnumFacing f) {
		return true;
	}
	
	@Override
	public int getWeakRSOutput(EnumFacing f) {
		int ret = on ? 15 : 0;
		return ret;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("on", this.on);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.on = tag.getBoolean("on");
	}
}
