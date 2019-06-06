package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerItemPort extends ContainerMTE {
	
	public ContainerItemPort(IInventory inv, TileEntityMeta te) {
		super(te);
		for (int y = 0; y < 3; ++y) for (int x = 0; x < 3; ++x) this.addSlotToContainer(new Slot(te, x + y * 3, 62 + x * 18, 17 + y * 18));
		addPlayerInventorySlots(inv, 8, 84);
	}

}
