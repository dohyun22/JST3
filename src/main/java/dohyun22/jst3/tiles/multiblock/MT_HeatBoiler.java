package dohyun22.jst3.tiles.multiblock;

import java.util.Arrays;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIMulti;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.recipe.ILiquidHeatExchangerManager.HeatExchangeProperty;
import ic2.api.recipe.Recipes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_HeatBoiler extends MT_Multiblock {
	private static final int OUTPUT = 16000;
	private int heat;
	/** 0.1•ÏB */
	private int water;
	private BlockPos hotIN;
	private BlockPos coldOUT;
	
	@Override
	protected boolean checkStructure() {
		EnumFacing f = this.getFacing();
		if (f == null || f.getAxis() == EnumFacing.Axis.Y) return false;
		World w = this.getWorld();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = 0; z <= 2; z++) {
					BlockPos p = getPosFromCoord(x, y, z);
					if (y == -1 || y == 1) {
						if (MetaTileBase.getMTEId(w, p) == 5001) continue;
						MetaTileBase mte = MetaTileBase.getMTE(w, p);
						if (mte instanceof MT_FluidPort) {
							((MT_FluidPort)mte).setTexture("heatres");
							if (((MT_FluidPort)mte).isOutput) {
								if (coldOUT != null) return false;
								coldOUT = mte.getPos();
							} else {
								if (hotIN != null) return false;
								hotIN = mte.getPos();
							}
							continue;
						}
						return false;
					} else {
						if ((x | y | z) == 0) continue;
						if (x == 0 && y == 0 && z == 1) {
							if (!w.isAirBlock(p)) return false;
						} else if (MetaTileBase.getMTEId(w, p) != 5001 && !getAndAddPort(p, 48, "heatres")) {
							return false;
						}
					}
				}
			}
		}
		if (fluidInput.size() != 1 || fluidOutput.size() != 1 || hotIN == null || coldOUT == null) return false;
		MT_FluidPort fout = getFluidPort(0, true);
		if (fout != null) {
			fout.setCapacity(64000);
			fout.setTransfer(32000);
		}
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		heat = tag.getInteger("heat");
		water = tag.getInteger("water");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("heat", heat);
		tag.setInteger("water", water);
	}
	
	@Override
	protected boolean checkCanWork() {
		if (heat > 0) return true;
		MT_FluidPort fin = getFluidPort(0, false);
		MT_FluidPort hin = getMTE(MT_FluidPort.class, getWorld(), hotIN);
		MT_FluidPort cout = getMTE(MT_FluidPort.class, getWorld(), coldOUT);
		if (hin == null || cout == null || fin == null || fluidOutput.isEmpty() || getFlag(fin.tank.getFluid()) == 0) return false;
		Object[] obj = getResult(hin.tank.getFluid());
		int hU = (int)obj[0];
		if (hU > 0) {
			int m = Math.max((int) Math.ceil(OUTPUT / (double)hU), 1);
			FluidStack fs = hin.tank.drainInternal(m, true);
			if (fs == null) return false;
			cout.tank.fillInternal(new FluidStack((Fluid)obj[1], fs.amount), true);
			heat += hU * (fs == null ? 0 : fs.amount);
			return true;
		}
		return false;
	}
	
	@Override
	protected void doWork() {
		if (fluidInput.isEmpty() || fluidOutput.isEmpty()) {
			stopWorking(true);
			return;
		}
		MT_FluidPort fp = getFluidPort(0, false);
		if (fp != null) {
			FluidStack fs = fp.tank.getFluid();
			byte flag = getFlag(fs);
			if (flag == 0) {
				stopWorking(true);
				return;
			}
			int use = Math.min(heat, OUTPUT);
			heat -= use;
			water += use;
			Fluid ref = flag == 1 ? JSTFluids.steam : FluidRegistry.getFluid("ic2steam");
			MT_FluidPort fp2 = getFluidPort(0, true);
			if (ref != null)
				fp2.tank.fillInternal(new FluidStack(ref, use), true);
			int amt = water / 10000;
			if (amt > 0) {
				fp.tank.drainInternal(amt, true);
				water -= amt * 10000;
			}
		}
		if (heat <= 0) {
			if (checkCanWork())
				onStartWork();
			else
				stopWorking(false);
		}
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_HeatBoiler();
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile == null || getWorld().isRemote)
			return true;
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
					{{4,4,4},
					{4,4,4},
					{4,4,4}},
					{{3,3,3},
					{3,0,3},
					{3,1,3}},
					{{4,4,4},
					{4,4,4},
					{4,4,4}}
			};
			return new GUIMulti(new ContainerMulti(inv, te), data);
		}
		return null;
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
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (this.baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, st, true);
	}
	
	@Override
	protected void clearPorts() {
		super.clearPorts();
		hotIN = null;
		coldOUT = null;
	}
	
	@Override
	protected boolean needEnergy() {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("heatres");
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("screen3")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (this.baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				ret[n] = getTETex("screen3");
			} else {
				ret[n] = getTETex("heatres");
			}
		}
		return ret;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.heatboiler"));
	}
	
	private static Object[] getResult(FluidStack fs) {
		Fluid f = fs == null ? null : fs.getFluid();
		if (f != null) {
			if (f == FluidRegistry.LAVA)
				return new Object[] {48, FluidRegistry.getFluid("ic2pahoehoe_lava")};
			if (JSTCfg.ic2Loaded)
				try {
					HeatExchangeProperty hep = Recipes.liquidCooldownManager.getHeatExchangeProperty(f);
					if (hep != null) return new Object[] {hep.huPerMB, hep.outputFluid};
				} catch (Throwable t) {}
		}
		return new Object[] {0, null};
	}
	
	private static byte getFlag(FluidStack fs) {
		if (fs != null && fs.getFluid() != null) {
			String name = FluidRegistry.getFluidName(fs);
			if ("water".equals(name)) return 1;
			else if ("ic2distilled_water".equals(name)) return 2;
		}
		return 0;
	}
}
