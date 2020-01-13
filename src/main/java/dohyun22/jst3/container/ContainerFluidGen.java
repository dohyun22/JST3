package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MT_FluidGen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;

public class ContainerFluidGen extends ContainerMTE {

	public ContainerFluidGen(IInventory inv, TileEntityMeta te) {
		super(te);
		
		addSlotToContainer(new InternalSlot(te, 0, 107, 22, te.mte instanceof MT_FluidGen ? ((MT_FluidGen) te.mte).type : -1));
		addSlotToContainer(new JSTSlot(te, 1, 107, 48, false, true, 64, true));
		
		addSlotToContainer(new JSTSlot(te, 2, 53, 35, false, false, 64, false));
		
		addPlayerInventorySlots(inv, 8, 84);
	}

	private class InternalSlot extends Slot {
		private final byte mode;

		public InternalSlot(IInventory inv, int idx, int x, int y, int mode) {
			super(inv, idx, x, y);
			this.mode = (byte)mode;
		}
		
		@Override
	    public boolean isItemValid(ItemStack st) {
			if (st == null) return false;
	        return MT_FluidGen.getFuelValue(mode, FluidUtil.getFluidContained(st)) > 0;
	    }
	}
}
