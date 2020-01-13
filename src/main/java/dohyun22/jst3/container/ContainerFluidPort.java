package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerFluidPort extends ContainerMTE {
	
	public ContainerFluidPort(IInventory inv, TileEntityMeta te) {
		super(te);
		
		addSlotToContainer(new Slot(te, 0, 107, 22));
		addSlotToContainer(JSTSlot.out(te, 1, 107, 48));
		addSlotToContainer(JSTSlot.fl(te, 2, 53, 35));
		addPlayerInventorySlots(inv, 8, 84);
	}
}
