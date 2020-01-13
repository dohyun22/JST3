package dohyun22.jst3.tiles.device;

import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUITMonitor;
import dohyun22.jst3.container.ContainerTMonitor;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;

public class MT_TMonitor extends MetaTileBase {
	public int temp;
	public boolean invert;
	
	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_TMonitor();
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		this.temp = 1000;
		try {
			for (EnumFacing f : EnumFacing.VALUES) {
				if (baseTile.facing != null) {
					baseTile.facing = null;
					break;
				}
				TileEntity te = getWorld().getTileEntity(getPos().offset(f));
				if (te instanceof IReactor || te instanceof IReactorChamber) {
					baseTile.facing = f;
					break;
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		checkCanStay(elb instanceof EntityPlayer && ((EntityPlayer)elb).capabilities.isCreativeMode);
	}
	
	@Override
	public void onBlockUpdate() {
		if (this.isClient()) return;
		checkCanStay(false);
	}
	
	private void checkCanStay(boolean nodrop) {
		boolean flag = true;
		if (baseTile.facing != null) {
			TileEntity te = getWorld().getTileEntity(JSTUtils.getOffset(getPos(), getFacing(), 1));
			try {
				if (te instanceof IReactor || te instanceof IReactorChamber)
					flag = false;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		if (flag) getWorld().destroyBlock(getPos(), !nodrop);
	}
	
	@Override
	public void onPostTick() {
		if (this.isClient() || baseTile.facing == null || !JSTCfg.ic2Loaded || baseTile.getTimer() % 5 != 0) return;
		try {
			TileEntity te = getWorld().getTileEntity(getPos().offset(baseTile.facing));
			IReactor r = null;
			if (te instanceof IReactor) {
				r = (IReactor)te;
			} else if (te instanceof IReactorChamber) {
				r = ((IReactorChamber)te).getReactorInstance();
			}
			if (r != null) {
				if (this.baseTile.setActive(r.getHeat() >= temp)) {
					getWorld().notifyNeighborsOfStateChange(this.getPos(), getWorld().getBlockState(getPos()).getBlock(), true);
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	@Override
	public boolean canConnectRedstone(EnumFacing f) {
		return true;
	}
	
	@Override
	public int getWeakRSOutput(EnumFacing f) {
		if (!JSTCfg.ic2Loaded) return 0;
		TileEntity te = getWorld().getTileEntity(getPos().offset(f));
		try {
			if (te instanceof IReactor || te instanceof IReactorChamber) return 0;
		} catch (Throwable t) {
			t.printStackTrace();
			return 0;
		}
		int ret = invert != baseTile.isActive() ? 15 : 0;
		return ret;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setByte("rface", (byte) (JSTUtils.getNumFromFacing(baseTile.facing) + 1));
		tag.setInteger("temp", temp);
		tag.setBoolean("invert", invert);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		baseTile.facing = JSTUtils.getFacingFromNum((byte) (tag.getByte("rface") - 1));
		temp = tag.getInteger("temp");
		invert = tag.getBoolean("invert");
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile == null || getWorld().isRemote)
			return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerTMonitor(inv, te);
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUITMonitor(new ContainerTMonitor(inv, te));
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.tmonitor"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("basic_side");
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("screen1")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (baseTile.facing == JSTUtils.getOppositeFacing(n)) {
				ret[n] = getTETex("screen" + (baseTile.isActive() ? "2" : "1"));
			} else {
				ret[n] = getTETex("basic_side");
			}
		}
		return ret;
	}

	public void invert() {
		invert = !invert;
		getWorld().notifyNeighborsOfStateChange(this.getPos(), getWorld().getBlockState(getPos()).getBlock(), true);
	}
}
