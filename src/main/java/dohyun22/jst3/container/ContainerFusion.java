package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.multiblock.MT_Fusion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerFusion extends ContainerMTE {
	public long energy;
	public long mxenergy;
	public boolean complete;
	public boolean rfDisp;
	public byte state;

	public ContainerFusion(IInventory inv, TileEntityMeta te) {
		super(te);
		
		addSlotToContainer(new Slot(te, 0, 88, 94));
		addSlotToContainer(new JSTSlot(te, 1, 130, 94, false, true, 64, true));
		addSlotToContainer(new Slot(te, 2, 8, 94));
		addSlotToContainer(new Slot(te, 3, 26, 94));
		addSlotToContainer(new Slot(te, 4, 44, 94));
		addSlotToContainer(new Slot(te, 5, 62, 94));
		
		addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, 0, 152, 26, false, false, 1, false));
		
		addPlayerInventorySlots(inv, 8, 116);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (te.getWorld().isRemote || !(te.mte instanceof MT_Fusion))
			return;

		MT_Fusion r = (MT_Fusion) te.mte;

		for (int i = 0; i < listeners.size(); ++i) {
			IContainerListener icl = (IContainerListener) listeners.get(i);
			
			if (energy != r.baseTile.energy)
				splitLongAndSend(icl, this, 1, r.baseTile.energy);
			
			if (mxenergy != r.getMaxEnergy())
				splitLongAndSend(icl, this, 2, r.getMaxEnergy());
			
			if (complete != r.isComplete())
				icl.sendWindowProperty(this, 58, r.isComplete() ? 1 : 0);
			
			if (rfDisp != r.displayRF)
				icl.sendWindowProperty(this, 59, r.displayRF ? 1 : 0);
			
			if (state != r.getState())
				icl.sendWindowProperty(this, 60, r.getState());
		}

		energy = r.baseTile.energy;
		mxenergy = r.getMaxEnergy();
		complete = r.isComplete();
		rfDisp = r.displayRF;
		state = r.getState();
	}
	
	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
		if (!pl.world.isRemote && si == 6 && ct == ClickType.PICKUP) {
			((MT_Fusion)te.mte).displayRF = !((MT_Fusion)te.mte).displayRF;
		}
		return super.slotClick(si, mc, ct, pl);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		energy = getLongFromData(energy, 50, id, data);
		mxenergy = getLongFromData(mxenergy, 54, id, data);
		if (id == 58) complete = data != 0;
		if (id == 59) rfDisp = data == 1;
		if (id == 60) state = (byte)data;
	}
}
