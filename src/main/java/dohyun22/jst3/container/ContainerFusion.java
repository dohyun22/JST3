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
		
		addSlotToContainer(new JSTSlot(new InventoryDummy(), 0, 152, 26, false, false, 1, false));
		
		addPlayerInventorySlots(inv, 8, 116);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (this.te.getWorld().isRemote || !(te.mte instanceof MT_Fusion)) {
			return;
		}

		MT_Fusion r = (MT_Fusion) te.mte;

		for (int i = 0; i < this.listeners.size(); ++i) {
			IContainerListener icl = (IContainerListener) this.listeners.get(i);
			
			if (this.energy != r.baseTile.energy)
				splitLongAndSend(icl, this, 50, r.baseTile.energy);
			
			if (this.mxenergy != r.getMaxEnergy())
				splitLongAndSend(icl, this, 54, r.getMaxEnergy());
			
			if (this.complete != r.isComplete())
				icl.sendWindowProperty(this, 58, r.isComplete() ? 1 : 0);
			
			if (this.rfDisp != r.displayRF)
				icl.sendWindowProperty(this, 59, r.displayRF ? 1 : 0);
			
			if (this.state != r.getState())
				icl.sendWindowProperty(this, 60, r.getState());
		}

		this.energy = r.baseTile.energy;
		this.mxenergy = r.getMaxEnergy();
		this.complete = r.isComplete();
		this.rfDisp = r.displayRF;
		this.state = r.getState();
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
		this.energy = getLongFromData(this.energy, 50, id, data);
		this.mxenergy = getLongFromData(this.mxenergy, 54, id, data);
		if (id == 58) this.complete = data != 0;
		if (id == 59) this.rfDisp = data == 1;
		if (id == 60) this.state = (byte)data;
	}
}
