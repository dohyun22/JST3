package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerGeneric extends ContainerMTE {
	public int progress;
	public int mxprogress;
	public long energy;
	public long mxenergy;
	public int fuel;
	public int mxfuel;

	public ContainerGeneric(IInventory inv, TileEntityMeta te) {
		super(te);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (JSTUtils.isClient() || !(te.mte instanceof IGenericGUIMTE))
			return;

		IGenericGUIMTE r = (IGenericGUIMTE) te.mte;

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
		}

		fuel = r.getFuel();
		mxfuel = r.getMxFuel();
		progress = r.getPrg();
		mxprogress = r.getMxPrg();
		energy = te.mte.baseTile.energy;
		mxenergy = te.mte.getMaxEnergy();
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
	}

	public void addSlot(Slot sl) {
		addSlotToContainer(sl);
	}

	public void addPlayerSlots(IInventory inv) {
		addPlayerInventorySlots(inv, 8, 84);
	}
}
