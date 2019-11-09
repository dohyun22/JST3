package dohyun22.jst3.tiles.multiblock;

import java.util.ArrayList;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIMulti;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MT_StirlingGen;
import dohyun22.jst3.tiles.interfaces.IMultiBlockPart;
import dohyun22.jst3.tiles.multiblock.MT_FluidPort;
import dohyun22.jst3.tiles.multiblock.MT_Multiblock;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_LargeBoiler extends MT_Multiblock {
	private double fuelLeft;
	private int /* 20•ÏB */water, capacity, counter;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_LargeBoiler();
	}

	@Override
	protected boolean checkStructure() {
		capacity = 0;
		EnumFacing f = getFacing();
		if (f == null || f.getAxis() == EnumFacing.Axis.Y)
			return false;
		World w = getWorld();
		BlockPos p;
		for (int x = -1; x <= 1; x++) {
			for (int z = 0; z <= 2; z++) {
				p = getRelativePos(x, 0, z);
				if (p.equals(getPos())) continue;
				if (MetaTileBase.getMTEId(w, p) != 5001 && !getAndAddPort(p, 20, "heatres"))
					return false;
			}
		}
		int cl = 0;
		label1: for (int y = 1; y <= 4; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = 0; z <= 2; z++) {
					if (x == 0 && z == 1) continue;
					p = getRelativePos(x, y, z);
					if (!isValid(p, w)) break label1;
				}
			}
			cl++;
		}
		if (cl < 1 || !isValid(getRelativePos(0, cl, 1), w)) return false;
		if (fluidInput.size() == 1 && fluidOutput.size() == 1 && itemInput.size() > 0 && itemInput.size() <= 2) {
			MT_FluidPort fp = getFluidPort(0, true);
			if (fp != null) {
				fp.setCapacity(64000);
				fp.setTransfer(64000);
				for (EnumFacing f2 : EnumFacing.VALUES) {
					p = fp.getPos().offset(f2);
					int n = MetaTileBase.getMTEId(w, p);
					if (n == 5002) {fp.setTexture("csg_b"); break;}
					if (n == 5003) {fp.setTexture("csg_r"); break;}
					if (n == 5083) {fp.setTexture("csg_a"); break;}
				}
			}
			return true;
		}
		capacity = 0;
		return false;
	}

	@Override
	protected int getCheckTimer() {
		return 100;
	}

	@Override
	protected boolean checkCanWork() {
		if ((int)fuelLeft > 0) return true;
		MT_FluidPort p = getFluidPort(0, false);
		if (p == null || p.tank.getFluid() == null || p.tank.getFluid().getFluid() != FluidRegistry.WATER) return false;
		getFuel();
		return (int)fuelLeft > 0;
	}

	@Override
	protected void doWork() {
		MT_FluidPort p = getFluidPort(0, false), p2 = getFluidPort(0, true);
		if (p != null && p2 != null) {
			if (fuelLeft < capacity && counter <= 0) {
				counter = 10;
				getFuel();
			}
			if (counter > 0) counter--;
			double use = Math.min(fuelLeft, capacity);
			FluidStack fs = p.tank.getFluid(), fs2;
			if (fs != null && fs.getFluid() == FluidRegistry.WATER) {
				boolean flag = capacity > 4096;
				int i = (int)(flag ? use / 4 : use);
				if (i <= 0) {
					stopWorking(false);
					return;
				}
				fs = new FluidStack(flag ? JSTFluids.hotsteam : JSTFluids.steam, i);
				fs2 = p2.tank.getFluid();
				if (fs2 != null && fs2.getFluid() != fs.getFluid())
					p2.tank.drainInternal(Integer.MAX_VALUE, true);
				use = p2.tank.fillInternal(fs, true);
				use /= 12.5D;
				if (use > 0) {
					water += use;
					int amt = water / 50;
					if (amt > 0) {
						p.tank.drainInternal(amt, true);
						water -= amt * 50;
					}
				}
			} else
				use = 0.0D;
			fuelLeft -= use;
		}
		if ((int)fuelLeft <= 0 && !checkCanWork()) stopWorking(false);
	}

	@Override
	protected void finishWork() {
		updateLight();
	}

	@Override
	protected void onStartWork() {
		updateLight();
	}

	@Override
	protected void stopWorking(boolean interrupt) {
		super.stopWorking(interrupt);
		updateLight();
	}

	@Override
	public boolean canAcceptEnergy() {
		return false;
	}
	
	@Override
	public boolean isEnergyInput(EnumFacing side) {
		return false;
	}

	@Override
	protected boolean needEnergy() {
		return false;
	}

	@Override
	public int getLightValue() {
		return baseTile.isActive() ? 15 : 0;
	}

	@Override
	public int getDust() {
		if (baseTile == null) return 800;
		return MathHelper.clamp(capacity / 8, 0, 800);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		water = tag.getInteger("water");
		capacity = tag.getInteger("capacity");
		fuelLeft = tag.getDouble("fuel");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("water", water);
		tag.setInteger("cap", capacity);
		tag.setDouble("fuel", fuelLeft);
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}

	@Override
	public int getData() {
		return capacity;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerMulti(inv, te);
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new GUIMulti(new ContainerMulti(inv, te), new byte[][][]{
			{{3,3,3},
			{3,3,3},
			{3,1,3}},
			{{4,4,4},
			{4,0,4},
			{4,4,4}},
			{{4,4,4},
			{4,0,4},
			{4,4,4}},
			{{4,4,4},
			{4,0,4},
			{4,4,4}},
			{{4,4,4},
			{4,4,4},
			{4,4,4}}
		});
	}

	@Override
	protected void updateClient() {
		if (baseTile.isActive()) {
			World w = getWorld();
			if (w.rand.nextInt(8) == 0)
				w.playSound(getPos().getX() + 0.5D, getPos().getY(), getPos().getZ() + 0.5D, w.rand.nextBoolean() ? SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE : SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 2.0F, 1.2F, false);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("heatres");
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("firebox")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = getTETex(baseTile.facing == JSTUtils.getFacingFromNum(n) ? "firebox" + (baseTile.isActive() ? "" : "_off") : "heatres");
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void doDisplay(byte state, int data, FontRenderer fr) {
		if (state != 0)
			fr.drawString(I18n.format("jst.msg.boiler.capacity", data), 14, 84, 0x00FF00);
		else
			super.doDisplay(state, data, fr);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.largeboiler"));
	}

	private void getFuel() {
		double need = capacity;
		for (ItemStack st : getAllInputSlots()) {
			int fv = MT_StirlingGen.getFuelValue(st);
			if (fv > 0 && need > 0) {
				int c = Math.min(st.getCount(), MathHelper.clamp(MathHelper.ceil(need / fv), 1, 64));
				st.shrink(c);
				need -= fv * c;
				fuelLeft += fv * c;
			}
		}
	}

	private boolean isValid(BlockPos p, World w) {
		int n = MetaTileBase.getMTEId(w, p);
		if (n == 5002) {capacity += 32; return true;}
		else if (n == 5003) {capacity += 256; return true;}
		else if (n == 5083) {capacity += 2000; return true;}
		return getAndAddPort(p, 32, null);
	}
}
