package dohyun22.jst3.tiles.multiblock;

import java.util.Arrays;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIMulti;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MT_FluidGen;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_LargeGenerator extends MT_Multiblock {
	public int burningFuel;
	/** Diesel Gas Steam Thermal*/
	private final byte type;
	private static final int OUTPUT = 20000;
	/** Distilled Water in 0.1•ÏB.<BR>Only used in Steam Turbine */
	private int waterAmount;
	private Fluid returnFluid;

	public MT_LargeGenerator(int t) {
		this.type = (byte)t;
	}

	@Override
	protected boolean checkStructure() {
		EnumFacing f = this.getFacing();
		if (f == null || f.getAxis() == EnumFacing.Axis.Y) return false;
		World w = this.getWorld();
		BlockPos p = this.getPos();
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 4; z++) {
					BlockPos p2 = getPosFromCoord(p, x - 1, y - 1, z, f);
					if (y == 0 || y == 2) {
						if (MetaTileBase.getMTEId(w, p2) != 5000)
							return false;
					} else if (y == 1) {
						if (x == 1) {
							if (z == 0) {
								continue;
							} else if (z == 3) {
								if (!getAndAddPort(p2, 2, "gen"))
									return false;
							} else if (!w.isAirBlock(p2)) {
								return false;
							}
						} else if (MetaTileBase.getMTEId(w, p2) != 5000 && !getAndAddPort(p2, type == 2 ? 48 : 16, "gen")) {
							return false;
						}
					}
				}
			}
		}
		
		if (energyOutput.size() != 1 || fluidInput.size() <= 0 || fluidInput.size() > 2 || (type == 2 && fluidOutput.size() > 1)) return false;
		updateEnergyPort(OUTPUT, OUTPUT * 8, true);
		for (MT_FluidPort fp : getFluidPorts(false)) {
			fp.setCapacity(64000);
			fp.setTransfer(64000);
		}
		return true;
	}

	@Override
	protected boolean checkCanWork() {
		if (burningFuel > 0) return true;
		for (MT_FluidPort fp : getFluidPorts(false)) {
			int fv = (int)(MT_FluidGen.getFuelValue(this.type, fp.tank.getFluid()) * 1.2F);
			if (fv > 0) {
				FluidStack fs = fp.tank.drainInternal(Math.max((int) Math.ceil(OUTPUT / ((double)fv)), 1), true);
				if (type == 2 && fluidOutput.size() > 0) {
					waterAmount += fs == null ? 0 : fs.amount;
					returnFluid = getReturnFluid(fs.getFluid());
				}
				burningFuel += fv * (fs == null ? 0 : fs.amount);
				return burningFuel > 0;
			}
		}
		return false;
	}
	
	@Override
	protected void doWork() {
		if (energyOutput.isEmpty())
			return;
		int use = Math.min(burningFuel, OUTPUT);
		burningFuel -= use;
		MT_EnergyPort p = getEnergyPort(0, true);
		if (p != null && p.baseTile.energy + use <= p.getMaxEnergy())
			p.baseTile.energy += use;
		if (burningFuel <= 0) {
			if (type == 2 && fluidOutput.size() > 0 && waterAmount >= 10000) {
				int amt = waterAmount / 10000;
				if (returnFluid != null) {
					MT_FluidPort fout = getFluidPort(0, true);
					if (fout != null)
						fout.tank.fillInternal(new FluidStack(returnFluid, amt), true);
					waterAmount -= amt * 10000;
				}
			}
			
			if (!checkCanWork())
				stopWorking(false);
		}
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_LargeGenerator(type);
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		burningFuel = tag.getInteger("fuel");
		if (type == 2) {
			waterAmount = tag.getInteger("dwatr");
			returnFluid = FluidRegistry.getFluid(tag.getString("ftype"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("fuel", burningFuel);
		if (type == 2) {
			tag.setInteger("dwatr", waterAmount);
			if (returnFluid != null) {
				String s = FluidRegistry.getFluidName(returnFluid);
				if (s != null)
					tag.setString("ftype", s);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.multigen"));
		if (type == 2) ls.add(I18n.format("jst.tooltip.tile.multigen.steam"));
		ls.add(I18n.format("jst.msg.com.out") + " " + OUTPUT + " EU/t, " + (OUTPUT * JSTCfg.RFPerEU) + " RF/t");
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
		byte[][][] data = {
				{{4,4,4},
				{4,4,4},
				{4,4,4},
				{4,4,4}},
				{{3,2,3},
				{3,0,3},
				{3,0,3},
				{3,1,3}},
				{{4,4,4},
				{4,4,4},
				{4,4,4},
				{4,4,4}}
		};
		return new GUIMulti(new ContainerMulti(inv, te), data);
	}
	
	@Override
	protected void stopWorking(boolean interrupt) {
		super.stopWorking(interrupt);
		returnFluid = null;
	}
	
	@Override
	protected boolean needEnergy() {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("gen");
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("multigen" + (type == 1 || type == 2 ? "_turb" : type == 3 ? "_lava" : ""))};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				boolean flag = type == 1 || type == 2;
				String str = flag ? "_turb" : type == 3 ? "_lava" : "";
				if (!flag)
					str += baseTile.isActive() ? "" : "_off";
				ret[n] = getTETex("multigen" + str);
			} else {
				ret[n] = getTETex("gen");
			}
		}
		return ret;
	}

	@Override
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "jst_lgen" + JSTUtils.getNumFromFacing(baseTile.facing);
	}
	
	private static Fluid getReturnFluid(Fluid in) {
		String s = FluidRegistry.getFluidName(in);
		if (s == null) return FluidRegistry.WATER;
		Fluid f = null;
		switch (s) {
		case "ic2steam":
		case "ic2superheated_steam":
			f = FluidRegistry.getFluid("ic2distilled_water");
		}
		if (f == null)
			f = FluidRegistry.WATER;
		return f;
	}
	
	@Override
	public boolean canAcceptEnergy() {
		return false;
	}
	
	@Override
	public boolean isEnergyInput(EnumFacing f) {
		return false;
	}

	@Override
	public int getDust() {
		switch (type) {
		case 0: return 600;
		case 1: return 400;
		case 3: return 200;
		default: return 0;
		}
	}
}
