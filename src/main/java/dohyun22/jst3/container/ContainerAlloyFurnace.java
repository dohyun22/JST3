package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.earlytech.MetaTileAlloyFurnace;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerAlloyFurnace extends ContainerMTE {
    public int burnTime;
    public int currentBurnTime;
    public int cookTime;
    public int totalCookTime;
	
	public ContainerAlloyFurnace(IInventory inv, TileEntityMeta te) {
		super(te);
		
		addSlotToContainer(new Slot(te, 0, 43, 17));
		addSlotToContainer(new Slot(te, 1, 61, 17));
		addSlotToContainer(new JSTSlot(te, 2, 116, 35, false, true, 64, true));
		addSlotToContainer(new SlotFurnaceFuel(te, 3, 52, 53));
		
		addPlayerInventorySlots(inv, 8, 84);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (JSTUtils.isClient()) return;
		
	    if (this.te.getWorld().isRemote ||!(te.mte instanceof MetaTileAlloyFurnace)) {
	        return;
	    }
	    
	    MetaTileAlloyFurnace r = (MetaTileAlloyFurnace)te.mte;
	    
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icl = (IContainerListener)this.listeners.get(i);
            
            if (this.burnTime != r.burnTime) {
            	icl.sendWindowProperty(this, 100, r.burnTime);
            }
            
            if (this.currentBurnTime != r.currentBurnTime) {
            	icl.sendWindowProperty(this, 101, r.currentBurnTime);
            }
            
            if (this.cookTime != r.cookTime) {
            	icl.sendWindowProperty(this, 102, r.cookTime);
            }
            
            if (this.totalCookTime != r.totalCookTime) {
            	icl.sendWindowProperty(this, 103, r.totalCookTime);
            }
        }
	    
        this.burnTime = r.burnTime;
        this.currentBurnTime = r.currentBurnTime;
        this.cookTime = r.cookTime;
        this.totalCookTime = r.totalCookTime;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id == 100) this.burnTime = data;
		if (id == 101) this.currentBurnTime = data;
		if (id == 102) this.cookTime = data;
		if (id == 103) this.totalCookTime = data;
	}
}
