package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.test.MetaTileCreativeGenerator;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCreativeGenerator extends ContainerMTE{
	public long energy;
	public long mxenergy;

	public ContainerCreativeGenerator(IInventory inv, TileEntityMeta te) {
		super(te);
		
		addSlotToContainer(new SlotFurnaceFuel(te, 0, 39, 52));
		addSlotToContainer(new BatterySlot(te, 1, 39, 17, true, false));
		
		addPlayerInventorySlots(inv, 8, 84);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	    if (this.te.getWorld().isRemote ||!(te.mte instanceof MetaTileCreativeGenerator)) {
	        return;
	    }

	    MetaTileCreativeGenerator r = (MetaTileCreativeGenerator)te.mte;
	    
        for (IContainerListener icl : this.listeners) {
            if (this.energy != r.baseTile.energy)
            	splitLongAndSend(icl, this, 100, r.baseTile.energy);
            
            if (this.mxenergy != r.getMaxEnergy())
            	splitLongAndSend(icl, this, 104, r.getMaxEnergy());
        }
	    
	    this.energy = r.baseTile.energy;
	    this.mxenergy = r.getMaxEnergy();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		
		this.energy = getLongFromData(this.energy, 100, id, data);
		this.mxenergy = getLongFromData(this.mxenergy, 104, id, data);
	}

}
