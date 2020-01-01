package dohyun22.jst3.container;

import java.util.Arrays;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IConfigurable;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerGeneric extends ContainerMTE {
	public int progress, mxprogress, fuel, mxfuel;
	public long energy, mxenergy;
	private int[] guiDatas = new int[32];

	public ContainerGeneric(TileEntityMeta te) {
		super(te);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (JSTUtils.isClient() || !(te.mte instanceof IGenericGUIMTE))
			return;

		IGenericGUIMTE r = (IGenericGUIMTE) te.mte;
		int[] d = r.getGuiData();
		for (IContainerListener icl : listeners) {
			int n = r.getFuel();
			if (fuel != n)
				splitIntAndSend(icl, this, 96, n);

			n = r.getMxFuel();
			if (mxfuel != n)
				splitIntAndSend(icl, this, 98, n);

			n = r.getPrg();
			if (n >= 0 && progress != n)
				splitIntAndSend(icl, this, 100, n);

			n = r.getMxPrg();
			if (n >= 0 && mxprogress != n)
				splitIntAndSend(icl, this, 102, n);

			if (energy != te.mte.baseTile.energy)
				splitLongAndSend(icl, this, 104, te.mte.baseTile.energy);

			if (mxenergy != te.mte.getMaxEnergy())
				splitLongAndSend(icl, this, 108, te.mte.getMaxEnergy());

			if (d != null)
				for (int m = 0; m < Math.min(d.length, guiDatas.length); m++)
					if (d[m] != guiDatas[m])
						splitIntAndSend(icl, this, 200 + m * 2, d[m]);
		}

		fuel = r.getFuel();
		mxfuel = r.getMxFuel();
		progress = r.getPrg();
		mxprogress = r.getMxPrg();
		energy = te.mte.baseTile.energy;
		mxenergy = te.mte.getMaxEnergy();
		if (d != null)
			for (int m = 0; m < Math.min(d.length, guiDatas.length); m++)
				guiDatas[m] = d[m];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		fuel = getIntFromData(fuel, 96, id, data);
		mxfuel = getIntFromData(mxfuel, 98, id, data);
		progress = getIntFromData(progress, 100, id, data);
		mxprogress = getIntFromData(mxprogress, 102, id, data);
		energy = getLongFromData(energy, 104, id, data);
		mxenergy = getLongFromData(mxenergy, 108, id, data);

		if (id >= 200 && id < 200 + guiDatas.length * 2) {
			int idx = (id - 200) / 2;
			guiDatas[idx] = getIntFromData(guiDatas[idx], 200 + idx * 2, id, data);
		}
	}

	public void addSlot(Slot sl) {
		addSlotToContainer(sl);
	}

	public void addPlayerSlots(IInventory inv) {
		addPlayerInventorySlots(inv, 8, 84);
	}

	public void addPlayerSlots(IInventory inv, int x, int y) {
		addPlayerInventorySlots(inv, x, y);
	}

	public int getGuiData(int idx) {
		if (guiDatas == null || idx < 0 || idx >= guiDatas.length) return 0;
		return guiDatas[idx];
	}

	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
		int n = si / 1000;
		boolean flag = false;
		if (te.mte instanceof IGenericGUIMTE && n == 1) {
			((IGenericGUIMTE)te.mte).handleBtn(si % 1000, pl);
			flag = true;
		}
		if (te.mte instanceof IConfigurable) {
			if (n == 2) {
				BlockPos p = te.getPos();
				pl.openGui(JustServerTweak.INSTANCE, si == 2000 ? 998 : 999, pl.world, p.getX(), p.getY(), p.getZ());
				flag = true;
			}
		}
		return flag ? pl.inventory.getItemStack() : super.slotClick(si, mc, ct, pl);
	}
}
