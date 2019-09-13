package dohyun22.jst3.tiles.multiblock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIMulti;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.Configuration;

public class MT_Rectenna extends MT_Multiblock {
	private boolean check;
	private int out;
	private static Class uet = ReflectionUtils.getClassObj("zmaster587.libVulpes.api.IUniversalEnergyTransmitter");
	private static Method trans = ReflectionUtils.getMethod(uet, "transmitEnergy", EnumFacing.class, Boolean.TYPE);

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Rectenna();
	}

	@Override
	protected void doWork() {
	    if (isClient()) return;
	    if (baseTile.getTimer() % getCheckTimer() == 0 && !checkCanWork()) {
	    	stopWorking(false); return;
	    }
	    if (out > 0) {
	    	MT_EnergyPort p = getEnergyPort(0, true);
			if (p != null && p.baseTile.energy + out <= p.getMaxEnergy())
				p.baseTile.energy += out;
	    	if (baseTile.getTimer() % 100 == 0) {
	    		List<EntityLivingBase> el = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(getPos().getX() - 1, getPos().getY() + 1, getPos().getZ() - 1, getPos().getX() + 2, 256, getPos().getZ() + 2));
	    		for (EntityLivingBase e : el)
	    			e.setFire(10);
	    	}
	    }
	}

	@Override
	protected boolean checkStructure() {
		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++) {
				if ((x | z) == 0) continue;
				BlockPos p = getPos().add(x, 0, z);
				if (MetaTileBase.getMTEId(getWorld(), p) != 5002 && !getAndAddPort(p, 6, "csg_b"))
					return false;
			}
		for (int x = -2; x <= 2; x++)
			for (int z = -2; z <= 2; z++) {
				int u = Math.abs(x), v = Math.abs(z);
				if ((u | v) == 0 || u == 2 && v == 2) continue;
				BlockPos p = getPos().add(x, 1, z);
				if (u < 2 && v < 2 ? !getWorld().isAirBlock(p) : MetaTileBase.getMTEId(getWorld(), p) != 5002)
					return false;
			}
		if (getWorld().getBlockState(getPos().up()).getBlock() != Blocks.IRON_BARS || itemInput.size() <= 0 || energyOutput.size() != 1) return false;
		updateEnergyPort(8192, 100000, true);
		return true;
	}

	@Override
	public void onBlockUpdate() {
		setUpdateTimer();
	}

	@Override
	protected boolean checkCanWork() {
		out = 0;
		if (!getWorld().canBlockSeeSky(getPos().up(2))) return false;
		try {
			for (ItemStack st : getAllInputSlots())
				if (st.hasTagCompound() && "advancedrocketry:satelliteidchip".equals(JSTUtils.getRegName(st))) {
					SatelliteBase s = AdvancedRocketryAPI.dimensionManager.getSatellite(st.getTagCompound().getLong("satelliteId"));
					if (uet.isInstance(s)) {
						int amt = (int) trans.invoke(s, EnumFacing.UP, false);
						if (check) out += amt;
					}
				}
			out *= (int) ((Configuration.microwaveRecieverMulitplier * 1.2F) / (float) JSTCfg.RFPerEU);
			out /= getCheckTimer();
			check = true;
		} catch (Throwable t) {
			JSTUtils.LOG.error("Rectenna Error");
			JSTUtils.LOG.catching(t);
			baseTile.errorCode = 1;
		}
		return out > 0;
	}

	@Override
	protected int getCheckTimer() {
		return 200;
	}

	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), JSTSounds.MICRO, SoundCategory.BLOCKS, 2.0F, 1.0F);
	}

	@Override
	protected void stopWorking(boolean interrupt) {
		super.stopWorking(interrupt);
		out = 0;
	}

	@Override
	protected boolean needEnergy() {
		return false;
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerMulti(inv, te);
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		byte[][][] data = {
				{{0,0,0,0,0},
				{0,3,3,3,0},
				{0,3,1,3,0},
				{0,3,3,3,0},
				{0,0,0,0,0}},
				{{0,4,4,4,0},
				{4,0,0,0,4},
				{4,0,2,0,4},
				{4,0,0,0,4},
				{0,4,4,4,0}}
		};
		return new GUIMulti(new ContainerMulti(inv, te), data);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !getWorld().isRemote)
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		out = tag.getInteger("out");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("out", out);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite a = getTETex("csg_b"), b = getTETex("hvsign");
		return new TextureAtlasSprite[] {a, a, b, b, b, b};
	}

	@Override
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "jst_micro";
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.microwave"));
	}
}
