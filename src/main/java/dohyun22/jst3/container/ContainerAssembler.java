package dohyun22.jst3.container;

import dohyun22.jst3.tiles.MultiTankHandler;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.machine.MT_Assembler;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerAssembler extends ContainerGeneric {
	public ContainerAssembler(IInventory inv, TileEntityMeta te) {
		super(inv, te);
		
	    for (int r = 0; r < 3; r++)
	    	for (int c = 0; c < 3; c++)
	    		addSlotToContainer(new Slot(te, c + r * 3, 44 + c * 18, 7 + r * 18));
		
		addSlotToContainer(new JSTSlot(te, 9, 130, 25, false, true, 64, true));
		addSlotToContainer(new JSTSlot(te, 10, 148, 25, false, true, 64, true));
		
		addSlotToContainer(new JSTSlot(te, 11, 62, 63, false, true, 64, false));
		
		addSlotToContainer(new BatterySlot(te, 12, 8, 53, false, true));
		
		addPlayerInventorySlots(inv, 8, 84);
	}
	
	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
	    if (!(te.mte instanceof MT_Assembler))
	        return ItemStack.EMPTY;
	    
		if (si == 100) {
			((MT_Assembler)te.mte).forceWork();
			return pl.inventory.getItemStack();
		}
		
		return super.slotClick(si, mc, ct, pl);
	}
}
