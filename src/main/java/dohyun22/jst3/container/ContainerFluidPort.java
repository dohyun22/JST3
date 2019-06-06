package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MetaTileFluidGen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerFluidPort extends ContainerMTE {
	
	public ContainerFluidPort(IInventory inv, TileEntityMeta te) {
		super(te);
		
		addSlotToContainer(new Slot(te, 0, 107, 22));
		addSlotToContainer(new JSTSlot(te, 1, 107, 48, false, true, 64, true));
		addSlotToContainer(new JSTSlot(te, 2, 53, 35, false, false, 64, false));
		addPlayerInventorySlots(inv, 8, 84);
	}
}
