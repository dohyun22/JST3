package dohyun22.jst3.container;

import dohyun22.jst3.tiles.device.MT_SuperCompressor;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSuperCompressor extends ContainerMTE {
	public int amt;
	
	public ContainerSuperCompressor(IInventory inv, TileEntityMeta te) {
		super(te);
		
	    for (int r = 0; r < 2; r++) for (int c = 0; c < 5; c++) addSlotToContainer(new Slot(te, c + r * 5, 22 + c * 18, 12 + r * 18));
	    
	    addSlotToContainer(new JSTSlot(te, 10, 138, 21, false, true, 64, true));
		
		addPlayerInventorySlots(inv, 8, 84);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (te.getWorld().isRemote || !(te.mte instanceof MT_SuperCompressor))
			return;

		MT_SuperCompressor r = (MT_SuperCompressor) te.mte;

		for (int i = 0; i < listeners.size(); ++i) {
			IContainerListener icl = (IContainerListener) listeners.get(i);

			if (amt != r.comp)
				splitIntAndSend(icl, this, 50, r.comp);
		}

		amt = r.comp;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		amt = getIntFromData(amt, 50, id, data);
	}
}
