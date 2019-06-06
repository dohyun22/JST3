package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MetaTileNaquadahGen;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerNaquadahGen extends ContainerMTE {
	public long energy;
	public long mxenergy;
	public int output;

	public ContainerNaquadahGen(IInventory inv, TileEntityMeta te) {
		super(te);
		
	    for (int r = 0; r < 2; r++) {
	    	for (int c = 0; c < 4; c++) {
	    		addSlotToContainer(new Slot(te, c + r * 4, 8 + c * 18, 8 + r * 18));
	    	}
	    }
		
		addPlayerInventorySlots(inv, 8, 84);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	    if (this.te.getWorld().isRemote ||!(te.mte instanceof MetaTileNaquadahGen)) {
	        return;
	    }
	    
	    MetaTileNaquadahGen r = (MetaTileNaquadahGen)te.mte;
	    
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icl = (IContainerListener)this.listeners.get(i);

            if (this.energy != r.baseTile.energy) {
            	splitLongAndSend(icl, this, 100, r.baseTile.energy);
            }
            
            if (this.mxenergy != r.getMaxEnergy()) {
            	splitLongAndSend(icl, this, 104, r.getMaxEnergy());
            }
            
            if (this.output != r.getOutput()) {
            	splitIntAndSend(icl, this, 108, r.getOutput());
            }
        }
	    
	    this.energy = r.baseTile.energy;
	    this.mxenergy = r.getMaxEnergy();
	    this.output = r.getOutput();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		this.energy = getLongFromData(this.energy, 100, id, data);
		this.mxenergy = getLongFromData(this.mxenergy, 104, id, data);
		this.output = getIntFromData(this.output, 108, id, data);
	}
}
