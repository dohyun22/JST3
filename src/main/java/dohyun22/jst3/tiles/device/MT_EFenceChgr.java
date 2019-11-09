package dohyun22.jst3.tiles.device;

import java.util.ArrayList;

import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.noupdate.MetaTileEFenceWire;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_EFenceChgr extends MetaTileEnergyInput {
	private int size;
	private byte vLvl;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_EFenceChgr();
	}
	
	@Override
	public long getMaxEnergy() {
		return 100000;
	}
	
	@Override
	public int maxEUTransfer() {
		return 2048;
	}
	
	@Override
	public long injectEnergy(@Nullable EnumFacing dir, long v, boolean sim) {
		vLvl = (byte) JSTUtils.getTierFromVolt(JSTUtils.convLongToInt(v));
		return super.injectEnergy(dir, v, sim);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		vLvl = tag.getByte("vLvl");
		size = tag.getInteger("size");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("vLvl", (byte) vLvl);
		tag.setInteger("size", size);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return this.getSingleTETex("hvsign");
	}
	
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "jst_efence";
	}
	
	public void onPostTick() {
		super.onPostTick();
		if (isClient()) return;
		
		if (baseTile.energy > 0 && baseTile.getTimer() % (20L + size * 2L) == 0) {
			size = 0;
			ArrayList<MetaTileEFenceWire> ls = new ArrayList();
			check(new ArrayList(), ls, this.getPos());
			if (ls.size() <= 0) return;
			long e = Math.max(1, baseTile.energy / ls.size());
			for (int n = 0; n < ls.size() && baseTile.energy > 0; n++)
				baseTile.energy -= ls.get(n).chargeFence(e, JSTUtils.getVoltFromTier(vLvl) * 25);
		}
	}
	
	@Override
	public boolean canConnectRedstone(EnumFacing f) {
		return true;
	}
	
	private void check(ArrayList<BlockPos> ls, ArrayList<MetaTileEFenceWire> ls2, BlockPos pos) {
		if (size > 1024) return;
		ls.add(pos);
		for (EnumFacing f : EnumFacing.VALUES) {
			BlockPos p = pos.offset(f);
			if (!ls.contains(p)) {
				MetaTileBase mte = MetaTileBase.getMTE(getWorld(), p);
				if (mte instanceof MetaTileEFenceWire) {
					size++;
					ls2.add((MetaTileEFenceWire)mte);
					check(ls, ls2, p);
				}
			}
		}
	}
}
