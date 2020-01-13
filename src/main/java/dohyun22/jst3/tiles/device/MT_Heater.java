package dohyun22.jst3.tiles.device;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Heater extends MetaTileEnergyInput implements IGenericGUIMTE {
	private static Class ti_smeltery, tc_smelter;
	private static Field[] _1, _2;
	private static Method tc_sfs;
	private static ArrayList<HeatSupport> heatSupp = new ArrayList();

	static {
		try {
			ti_smeltery = Class.forName("slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructureFuelTank");
			Class clz = Class.forName("slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructure");
			_1 = new Field[4];
			_1[0] = ReflectionUtils.getField(clz, "needsFuel");
			_1[1] = ReflectionUtils.getField(clz, "fuel");
			_1[2] = ReflectionUtils.getField(clz, "temperature");
			_1[3] = ReflectionUtils.getField(ti_smeltery, "currentFuel");
		} catch (Throwable t) {
			ti_smeltery = null;
			_1 = null;
		}

		try {
			tc_smelter = Class.forName("thaumcraft.common.tiles.essentia.TileSmelter");
			_2 = new Field[3];
			_2[0] = ReflectionUtils.getField(tc_smelter, "furnaceBurnTime");
			_2[1] = ReflectionUtils.getField(tc_smelter, "currentItemBurnTime");
			_2[2] = ReflectionUtils.getField(tc_smelter, "speedBoost");
			tc_sfs = ReflectionUtils.getMethod("thaumcraft.common.blocks.essentia.BlockSmelter", "setFurnaceState", World.class, BlockPos.class, Boolean.TYPE);
		} catch (Throwable t) {
			tc_smelter = null;
			_2 = null;
		}

		addSupport("ic2.core.block.machine.tileentity.TileEntityIronFurnace", new String[] {"fuel", "totalFuel"}, new Object[] {800, 800}, new int[] {0}, 40, false);
		addSupport("net.xalcon.torchmaster.common.tiles.TileEntityTerrainLighter", new String[] {"burnTime", "totalBurnTime"}, new Object[] {800, 800}, new int[] {0}, 40, false);
		addSupport("moze_intel.projecte.gameObjs.tiles.RMFurnaceTile", new String[] {"furnaceBurnTime", "currentItemBurnTime"}, new Object[] {800, 800}, new int[] {0}, 40, false);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Heater();
	}

	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient()) return;
		injectEnergy(null, JSTUtils.dischargeItem(inv.get(0), Math.min(getMaxEnergy() - baseTile.energy, maxEUTransfer()), 5, false, false), false);
		if (baseTile.getTimer() % 20 == 0) {
			for (EnumFacing f : EnumFacing.VALUES) {
				if (baseTile.energy < 600) break;
				BlockPos p = getPos().offset(f);
				TileEntity te = getWorld().getTileEntity(p);
				if (te == null) continue;
				if (te instanceof TileEntityFurnace) {
					TileEntityFurnace tf = (TileEntityFurnace)te;
					int bt = tf.getField(0);
					if (bt <= 40) {
						tf.setField(0, 800);
						tf.setField(1, 800);
			            if (bt <= 0) {
			                BlockFurnace.setState(true, getWorld(), p);
			                tf.markDirty();
			            }
						baseTile.energy -= 400;
					}
				} else if (ti_smeltery != null && ti_smeltery.isInstance(te)) {
					if (JSTUtils.getRegName(getWorld().getBlockState(p)).equals("tconstruct:smeltery_controller"))
						try {
							if (_1[0].getBoolean(te) || _1[1].getInt(te) <= 0) {
								_1[0].setBoolean(te, false);
								_1[1].setInt(te, (_1[1].getInt(te)) + 10);
								_1[2].setInt(te, 1500);
								_1[3].set(te, new FluidStack(FluidRegistry.LAVA, 1));
								baseTile.energy -= 400;
							}
						} catch (Throwable t) {
							JSTUtils.LOG.error("Can't heat Tcon Smeltery");
							baseTile.errorCode = 2;
							JSTUtils.LOG.catching(t);
						}
				} else if (tc_smelter != null && tc_smelter.isInstance(te)) {
					try {
						if (_2[0].getInt(te) <= 40) {
							_2[0].setInt(te, 800);
							_2[1].setInt(te, 800);
							_2[2].setBoolean(te, true);
							tc_sfs.invoke(null, getWorld(), p, true);
							baseTile.energy -= 400;
						}
					} catch (Throwable t) {
						JSTUtils.LOG.error("Can't heat TC Smelter");
						baseTile.errorCode = 2;
						JSTUtils.LOG.catching(t);
					}
				} else {
					Iterator<HeatSupport> it = heatSupp.iterator();
					while (it.hasNext()) {
						HeatSupport hs = it.next();
						try {
							if (hs.canHeat(te)) {
								hs.heat(te);
								baseTile.energy -= 400;
							}
						} catch (Throwable t) {
							JSTUtils.LOG.error("Can't heat " + hs.te.getSimpleName());
							JSTUtils.LOG.catching(t);
							it.remove();
						}
					}
				}
			}
		}
	}

	@Override
	public long getMaxEnergy() {
		return 4096;
	}
	
	@Override
	public int maxEUTransfer() {
		return 128;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hitX, float hitY, float hitZ) {
		if (!isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric r = new ContainerGeneric(te);
		r.addSlot(new BatterySlot(te, 0, 80, 35, false, true));
		r.addPlayerSlots(inv);
		return r;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric r = new GUIGeneric((ContainerGeneric)getServerGUI(id, inv, te));
		r.addSlot(80, 35, 2);
		r.addPwr(85, 15);
		return r;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.heater"));
		List<String> ls2 = JSTUtils.getListFromTranslation("jst.tooltip.tile.heater.supp");
		try {
			ls.add(ls2.get(0));
			if (JSTCfg.ic2Loaded) ls.add(ls2.get(1));
			if (JSTCfg.ticLoaded) ls.add(ls2.get(2));
			if (JSTCfg.tcLoaded) ls.add(ls2.get(3));
			if (Loader.isModLoaded("torchmaster")) ls.add(ls2.get(4));
			if (Loader.isModLoaded("projecte")) ls.add(ls2.get(5));
		} catch (Exception e) {}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return this.getSingleTETex("heater");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getModelKey() {
		return "jst_heater";
	}

	public static boolean addSupport(Object cl, String[] f, Object[] v, @Nullable int[] c, int th, boolean em) {
		if (f == null || v == null || f.length != v.length) {
			JSTUtils.LOG.error("Invalid Argument");
			return false;
		}
		Class cz = ReflectionUtils.getClassObj(cl);
		if (cz != null) {
			Field[] fa = new Field[f.length];
			for (int n = 0; n < f.length; n++) {
				Field val = ReflectionUtils.getField(cz, f[n]);
				if (val == null)
					return false;
				fa[n] = val;
			}
			heatSupp.add(new HeatSupport(cz, fa, v, c, th, em));
			return true;
		}
		return false;
	}

	private static class HeatSupport {
		Class te;
		Field[] fields;
		Object[] setVals;
		int[] check;
		int trigger;
		boolean exact;

		HeatSupport(Class t, Field[] f, Object[] v, int[] c, int trig, boolean em) {
			te = t;
			fields = f;
			setVals = v;
			check = c;
			trigger = trig;
		}

		boolean canHeat(TileEntity teo) throws Throwable {
			if (teo == null || !(exact ? te == teo.getClass() : te.isInstance(teo))) return false;
			if (check != null) {
				for (int i : check) {
					Object o = fields[i].get(teo);
					if (o == null)
						return false;
					Class c = o.getClass();
					if (o instanceof Boolean) {
						if (!(Boolean)o)
							return false;
					} else if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long) {
						if (Long.valueOf(o.toString()) > trigger)
							return false;
					}
				}
			}
			return true;
		}

		void heat(TileEntity teo) throws Throwable {
			if (fields != null)
				for (int n = 0; n < fields.length; n++)
					fields[n].set(teo, setVals[n]);
		}
	}
}
