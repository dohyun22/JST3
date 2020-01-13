package dohyun22.jst3.container;

import dohyun22.jst3.tiles.energy.MT_EnergyMeter;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerMeter extends ContainerMTE {
	public long sum;
	public double avg;
	private long avgL;
	public boolean displayRF;
	public float amp;
	private int ampI;

	public ContainerMeter(IInventory inv, TileEntityMeta te) {
		super(te);
		addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, 0, 152, 10, false, false, 1, false));
		addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, 1, 152, 32, false, false, 1, false));
		addPlayerInventorySlots(inv, 8, 84);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (JSTUtils.isClient() || !(te.mte instanceof MT_EnergyMeter))
	        return;

		MT_EnergyMeter r = (MT_EnergyMeter)te.mte;
	    
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icl = (IContainerListener)this.listeners.get(i);

            if (this.avgL != r.avg20X100)
            	splitLongAndSend(icl, this, 100, r.avg20X100);
            
            if (this.sum != r.total)
            	splitLongAndSend(icl, this, 104, r.total);
            
            if (this.ampI != r.avgAmp20X100)
            	splitLongAndSend(icl, this, 108, r.avgAmp20X100);
            
            if (this.displayRF != r.baseTile.isActive())
            	icl.sendWindowProperty(this, 112, r.baseTile.isActive() ? 1 : 0);
        }
	    
	    this.avgL = r.avg20X100;
	    this.sum = r.total;
	    this.ampI = r.avgAmp20X100;
	    this.displayRF = r.baseTile.isActive();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		
		this.avgL = getLongFromData(this.avgL, 100, id, data);
		this.avg = avgL / 100.0D;
		this.sum = getLongFromData(this.sum, 104, id, data);
		this.ampI = getIntFromData(this.ampI, 108, id, data);
		this.amp = this.ampI / 100.0F;
		if (id == 112) this.displayRF = data != 0;
	}
	
	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
	    if (!(te.mte instanceof MT_EnergyMeter))
	        return ItemStack.EMPTY;
	    
	    if (!pl.world.isRemote && ct == ClickType.PICKUP) {
	    	MT_EnergyMeter em = ((MT_EnergyMeter)te.mte);
	    	switch (si) {
	    	case 0:
	    		em.avg20X100 = 0;
	    		em.total = 0;
	    		break;
	    	case 1:
	    		em.baseTile.setActive(!em.baseTile.isActive());
	    		break;
	    	default:
	    	}
	    }
	    
		return super.slotClick(si, mc, ct, pl);
	}
}
