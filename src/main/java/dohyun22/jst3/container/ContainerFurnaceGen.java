package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MetaTileFurnaceGen;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerFurnaceGen extends ContainerMTE {
	
	public long energy;
	public long mxenergy;
	public int fuelpower;
	public int fuelleft;

	public ContainerFurnaceGen(IInventory inv, TileEntityMeta te) {
		super(te);
		
		addSlotToContainer(new SlotFurnaceFuel(te, 0, 39, 52));
		addSlotToContainer(new BatterySlot(te, 1, 39, 17, true, false));
		
		addPlayerInventorySlots(inv, 8, 84);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	    if (this.te.getWorld().isRemote ||!(te.mte instanceof MetaTileFurnaceGen)) {
	        return;
	    }

	    MetaTileFurnaceGen r = (MetaTileFurnaceGen)te.mte;
	    
        for (IContainerListener icl : this.listeners) {
            if (this.energy != r.baseTile.energy)
            	splitLongAndSend(icl, this, 100, r.baseTile.energy);
            
            if (this.mxenergy != r.getMaxEnergy())
            	splitLongAndSend(icl, this, 104, r.getMaxEnergy());

            if (this.fuelpower != r.fuelValue)
            	icl.sendWindowProperty(this, 108, Math.min(r.fuelValue, Short.MAX_VALUE));
            
            if (this.fuelleft != (int)r.fuelLeft)
            	icl.sendWindowProperty(this, 109, (int)Math.min(r.fuelLeft, Short.MAX_VALUE));
        }
	    
	    this.energy = r.baseTile.energy;
	    this.mxenergy = r.getMaxEnergy();
	    this.fuelpower = r.fuelValue;
	    this.fuelleft = (int)r.fuelLeft;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		
		this.energy = getLongFromData(this.energy, 100, id, data);
		this.mxenergy = getLongFromData(this.mxenergy, 104, id, data);
		
		if (id == 108) this.fuelpower = data;
		if (id == 109) this.fuelleft = data;
	}
}
