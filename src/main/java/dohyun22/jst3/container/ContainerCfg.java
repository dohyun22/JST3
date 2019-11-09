package dohyun22.jst3.container;

import java.util.ArrayList;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IConfigurable;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCfg extends ContainerMTE {
	public final boolean mode;
	public short[] priority = new short[8], cfg = new short[6];
	public EnumFacing front;

	public ContainerCfg(TileEntityMeta te, boolean m) {
		super(te);
		mode = m;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (te.getWorld().isRemote || !(te.mte instanceof IConfigurable)) return;
		IConfigurable r = (IConfigurable)te.mte;
		if (mode) {
			short[] l = r.getCfg();
			if (l != null && l.length == 6) {
				for (int n = 0; n < 6; n++) {
					if (l[n] != cfg[n])
						for (IContainerListener icl : listeners)
							icl.sendWindowProperty(this, n, l[n]);
					cfg[n] = l[n];
				}
			}
			if (front != te.facing) {
				for (IContainerListener icl : listeners)
					icl.sendWindowProperty(this, 200, JSTUtils.getNumFromFacing(te.facing) + 1);
			}
			front = te.facing;
		} else {
			List<Short> l = r.getCfgList();
			if (l != null) {
				for (int n = 0; n < Math.min(l.size(), priority.length); n++) {
					short s = l.get(n) == null ? 0 : l.get(n);
					if (s != priority[n])
						for (IContainerListener icl : listeners)
							icl.sendWindowProperty(this, 100 + n, s);
					priority[n] = s;
				}
			}
		}
	}

	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
		int n = si / 100;
		if (te.mte instanceof IConfigurable) {
			if (n == 0) {
				((IConfigurable)te.mte).changeCfg(si % 100);
			} else if (n == 1) {
				((IConfigurable)te.mte).changeCfgList(si % 100, false);
			} else if (n == 2) {
				((IConfigurable)te.mte).changeCfgList(si % 100, true);
			} else if (n == 3) {
				BlockPos p = te.getPos();
				pl.openGui(JustServerTweak.INSTANCE, 1, te.getWorld(), p.getX(), p.getY(), p.getZ());
			}
		}
	    return ItemStack.EMPTY;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		int n = id % 100;
		if (id == 200) {
			front = JSTUtils.getFacingFromNum((byte)(data - 1));
		} else if (id >= 100) {
			if (n < priority.length) priority[n] = (short)data;
		} else {
			if (id >= 0 && id < cfg.length) cfg[id] = (short)data;
		}
	}
}
