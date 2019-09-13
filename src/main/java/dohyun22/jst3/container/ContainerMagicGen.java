package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MetaTileMagicGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerMagicGen extends ContainerMTE {
	public long energy;
	public long mxenergy;
	public int fuelpower;
	public int fuelleft;
	public boolean collectEC;

	public ContainerMagicGen(IInventory inv, TileEntityMeta te) {
		super(te);

		addSlotToContainer(new Slot(te, 0, 15, 11));
		addSlotToContainer(new JSTSlot(te, 1, 15, 54, false, true, 64, true));
		addSlotToContainer(new BatterySlot(te, 2, 63, 11, true, false));
		
		addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, 0, 152, 8, false, false, 1, false));
		
		addPlayerInventorySlots(inv, 8, 84);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	    if (this.te.getWorld().isRemote ||!(te.mte instanceof MetaTileMagicGenerator)) {
	        return;
	    }

	    MetaTileMagicGenerator r = (MetaTileMagicGenerator)te.mte;
	    
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icl = (IContainerListener)this.listeners.get(i);

            if (this.energy != r.baseTile.energy)
            	splitLongAndSend(icl, this, 100, r.baseTile.energy);
            
            if (this.mxenergy != r.getMaxEnergy())
            	splitLongAndSend(icl, this, 104, r.getMaxEnergy());

            if (this.fuelpower != r.fuelValue)
            	splitIntAndSend(icl, this, 108, r.fuelValue);
            
            if (this.fuelleft != r.fuelLeft)
            	splitIntAndSend(icl, this, 110, r.fuelLeft);
            
            if (this.collectEC != r.collectEndCrystal)
            	icl.sendWindowProperty(this, 112, r.collectEndCrystal ? 1 : 0);
        }
	    
	    this.energy = r.baseTile.energy;
	    this.mxenergy = r.getMaxEnergy();
	    this.fuelpower = r.fuelValue;
	    this.fuelleft = r.fuelLeft;
	    this.collectEC = r.collectEndCrystal;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		
		this.energy = getLongFromData(this.energy, 100, id, data);
		this.mxenergy = getLongFromData(this.mxenergy, 104, id, data);
		this.fuelpower = getIntFromData(this.fuelpower, 108, id, data);
		this.fuelleft = getIntFromData(this.fuelleft, 110, id, data);
		if (id == 112) this.collectEC = data == 1;
	}
	
	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
	    if (!(te.mte instanceof MetaTileMagicGenerator))
	        return ItemStack.EMPTY;
	    
	    if (!pl.world.isRemote && ct == ClickType.PICKUP && si == 3)
	    	((MetaTileMagicGenerator)te.mte).collectEndCrystal = !((MetaTileMagicGenerator)te.mte).collectEndCrystal;
	    
		return super.slotClick(si, mc, ct, pl);
	}
}
