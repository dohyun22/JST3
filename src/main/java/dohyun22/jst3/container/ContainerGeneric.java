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
	
	public ContainerGeneric(IInventory inv, TileEntityMeta te) {
		super(te);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
	    if (JSTUtils.isClient() ||!(te.mte instanceof IGenericGUIMTE))
	        return;
	    
	    IGenericGUIMTE r = (IGenericGUIMTE)te.mte;
	    
        for (IContainerListener icl : listeners) {
            int n = r.getPrg();
            if (n >= 0 && this.progress != n)
            	splitIntAndSend(icl, this, 100, n);
            
            n = r.getMxPrg();
            if (n >= 0 && this.mxprogress != n)
            	splitIntAndSend(icl, this, 102, n);
            
            if (this.energy != te.mte.baseTile.energy)
            	splitLongAndSend(icl, this, 104, te.mte.baseTile.energy);
            
            if (this.mxenergy != te.mte.getMaxEnergy())
            	splitLongAndSend(icl, this, 108, te.mte.getMaxEnergy());
        }
	    
        this.progress = r.getPrg();
        this.mxprogress = r.getMxPrg();
        this.energy = JSTUtils.convLongToInt(te.mte.baseTile.energy);
        this.mxenergy = JSTUtils.convLongToInt(te.mte.getMaxEnergy());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		this.progress = getIntFromData(this.progress, 100, id, data);
		this.mxprogress = getIntFromData(this.mxprogress, 102, id, data);
		this.energy = getLongFromData(this.energy, 104, id, data);
		this.mxenergy = getLongFromData(this.mxenergy, 108, id, data);
	}
	
	public void addSlot(Slot sl) {
		addSlotToContainer(sl);
	}
	
	public void addPlayerSlots(IInventory inv) {
		addPlayerInventorySlots(inv, 8, 84);
	}
}
