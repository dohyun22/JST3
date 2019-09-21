package dohyun22.jst3.tiles.multiblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIMulti;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IMultiBlockPart;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class MT_FluidDrill extends MT_Multiblock {
	private boolean noFluid;
	private byte tier = 2;

	public MT_FluidDrill() {
		circuitTier = 2;
	}

	@Override
	protected boolean checkStructure() {
		EnumFacing f = getFacing();
		if (f == null || f.getAxis() == EnumFacing.Axis.Y) return false;
		
		int pid = -1;
		BlockPos pos = getPos();
		World w = getWorld();
		for (int x = -1; x <= 1; x++) {
			for (int z = 0; z <= 2; z++) {
				BlockPos p = getRelativePos(x, 0, z);
				if (p.equals(pos)) continue;
				if (getAndAddPort(p, 33, null))
					continue;
				int id = MetaTileBase.getMTEId(w, p);
				if (((pid != -1 && pid != id) || id < 3 || id > 9))
					return false;
				pid = id;
			}
		}
		
		for (int n = 1; n < 4; n++) {
			BlockPos center = getRelativePos(0, n, 1);
			int id = MetaTileBase.getMTEId(w, center);
			if ((pid != -1 && pid != id) || id < 3 || id > 9)
				return false;
			pid = id;
			for (EnumFacing f2 : EnumFacing.HORIZONTALS)
				if (MetaTileBase.getMTEId(w, center.offset(f2)) != 5080)
					return false;
			center = center.add(0, 3, 0);
			if (MetaTileBase.getMTEId(w, center) != 5080)
				return false;
		}
		
		if (energyInput.size() <= 0 || energyInput.size() > 2 || fluidOutput.size() != 1)
			return false;
		
		for (int n = pos.getY() - 1; n > 0; n--)
			if (MetaTileBase.getMTE(w, pos.offset(EnumFacing.DOWN, n)) instanceof MT_FluidDrill)
				return false;
		
		tier = (byte) (pid - 1);
		int v = JSTUtils.getVoltFromTier(getTier());
		updateEnergyPort(v, v * 8, false);
		MT_FluidPort fp = getFluidPort(0, true);
		if (fp != null) fp.setCapacity(64000);
		fp.setTexture("t" + tier + "_side");
		for (IMultiBlockPart mbp : getEnergyPorts(false))
			mbp.setTexture("t" + tier + "_side");
		baseTile.issueUpdate();
		return true;
	}

	@Override
	protected boolean checkCanWork() {
		MT_FluidPort fp = getFluidPort(0, true);
		if (noFluid || fp == null) return false;
		FluidTank t = fp.tank;
		if (t.getCapacity() - t.getFluidAmount() < 5000)
			return false;
		energyUse = 100;
		mxprogress = 100;
		return true;
	}
	
	@Override
	protected void finishWork() {
		if (fluidOutput.size() > 0) {
			FluidStack[] afs = JSTChunkData.getOrCreateFluid(getWorld(), new ChunkPos(JSTUtils.getOffset(getPos(), JSTUtils.getOppositeFacing(getFacing()), 1)), false);
			MT_FluidPort fp = this.getFluidPort(0, true);
			if (afs != null && afs[0] != null) {
				FluidTank t = fp.tank;
				if (t.getCapacity() - t.getFluidAmount() < 5000)
					return;
				FluidStack fs2 = afs[0].copy();
				fs2.amount = Math.min(afs[0].amount, 5000);
				afs[0].amount -= fs2.amount;
				if (afs[0].amount <= 0) {
					noFluid = true;
					afs[0] = null;
				}
				t.fillInternal(fs2, true);
				JSTChunkData.putFluidData(getWorld(), new ChunkPos(getPos()), afs[0], false);
			} else {
				noFluid = true;
			}
		}
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_FluidDrill();
	}

	@Override
	protected boolean isPortPowered() {
		return true;
	}

	@Override
	protected void updateClient() {
		if (baseTile.isActive()) {
			World w = getWorld();
			if (w.rand.nextInt(8) == 0) {
				BlockPos p = getRelativePos(0, 3, 1);
				for (int i = 0; i < 8; i++) {
					double x = p.getX() + 0.5D + w.rand.nextFloat() * 0.6D - 0.3D;
					double y = p.getY() + 1 + w.rand.nextFloat() * 0.2D;
					double z = p.getZ() + 0.5D + w.rand.nextFloat() * 0.6D - 0.3D;
					w.spawnParticle(w.rand.nextBoolean() ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hX, float hY, float hZ) {
		if (baseTile != null && !isClient() && !tryUpg(pl, st))
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerMulti(inv, te);
		return null;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1) {
			byte[][][] data = {
					{{2,2,2},
					{2,2,2},
					{2,1,2}},
					{{0,3,0},
					{3,4,3},
					{0,3,0}},
					{{0,3,0},
					{3,4,3},
					{0,3,0}},
					{{0,3,0},
					{3,4,3},
					{0,3,0}},
					{{0,0,0},
					{0,3,0},
					{0,0,0}},
					{{0,0,0},
					{0,3,0},
					{0,0,0}},
					{{0,0,0},
					{0,3,0},
					{0,0,0}}
			};
			return new GUIMulti(new ContainerMulti(inv, te), data);
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.drillingrig"));
	}

	@Override
	public int getDust() {
		return 100;
	}
	
	@Override
	protected int getTier() {
		return Math.min(circuitTier, tier);
	}
	
	@Override
	protected int getSpeed() {
		return Math.max(1, JSTUtils.getMultiplier(getTier(), energyUse));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		noFluid = tag.getBoolean("nofluid");
		tier = tag.getByte("tcase");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("nofluid", noFluid);
		tag.setByte("tcase", tier);
	}
	
	@Override
	public void readSyncableDataFromNBT(NBTTagCompound tag) {
		circuitTier = tag.getByte("tcirc");
	}
	
	@Override
	public void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setByte("tcirc", circuitTier);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(2);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("screen3")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = baseTile.facing == JSTUtils.getFacingFromNum(n) ? getTETex("screen3") : getTieredTex(circuitTier);
		return ret;
	}
	
	@Override
	public int getData() {
		if (getMode() != 2) return 0;
		return mxprogress <= 0 ? 0 : progress * 100 / mxprogress;
	}
	
	@Override
	public byte getMode() {
		return (byte) (isComplete() ? baseTile.isActive() ? 2 : noFluid ? 3 : 1 : 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void doDisplay(byte state, int data, FontRenderer fr) {
		if (state == 3)
			fr.drawString(I18n.format("jst.msg.com.nofluid"), 14, 84, 0xFF0000);
		else if (state == 2)
			fr.drawString(I18n.format("jst.msg.com.progress", data), 14, 84, 0x00FF00);
		else
			super.doDisplay(state, data, fr);
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}
	
	@Override
	public boolean canAcceptEnergy() {
		return false;
	}
	
	@Override
	public boolean isEnergyInput(EnumFacing side) {
		return false;
	}
}
