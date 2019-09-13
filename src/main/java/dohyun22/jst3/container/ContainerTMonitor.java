package dohyun22.jst3.container;

import dohyun22.jst3.tiles.device.MT_TMonitor;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerTMonitor extends ContainerMTE {
	public int temp;
	public boolean invert;
	
	public ContainerTMonitor(IInventory inv, TileEntityMeta te) {
		super(te);
		addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, 0, 152, 8, false, false, 1, false));
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 4; x++) {
				addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, x + y * 4 + 1, x * 18 + 53, y * 18 + 44, false, false, 1, false));
			}
		}
		
		addPlayerInventorySlots(inv, 8, 84);
	}
	
	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
	    if (!(te.mte instanceof MT_TMonitor)) {
	        return ItemStack.EMPTY;
	    }
	    
	    if (!pl.world.isRemote && ct == ClickType.PICKUP) {
	    	if (si == 0) {
	    		((MT_TMonitor)te.mte).invert();
	    	} else {
	    		int n = 0;
	    		switch (si) {
	    		case 1: n = 1; break;
	    		case 2: n = 10; break;
	    		case 3: n = 100; break;
	    		case 4: n = 1000; break;
	    		case 5: n = -1; break;
	    		case 6: n = -10; break;
	    		case 7: n = -100; break;
	    		case 8: n = -1000; break;
	    		}
	    		if (n != 0) {
	    			((MT_TMonitor)te.mte).temp = MathHelper.clamp(((MT_TMonitor)te.mte).temp + n, 0, 1000000);
	    		}
	    	}
	    }
	    
		return super.slotClick(si, mc, ct, pl);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (JSTUtils.isClient()) return;
		
	    if (this.te.getWorld().isRemote ||!(te.mte instanceof MT_TMonitor)) {
	        return;
	    }

	    MT_TMonitor r = (MT_TMonitor)te.mte;
	    
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icl = (IContainerListener)this.listeners.get(i);

            if (this.temp != r.temp)
            	splitIntAndSend(icl, this, 100, r.temp);
            
            if (this.invert != r.invert)
            	icl.sendWindowProperty(this, 102, r.invert ? 1 : 0);
        }
	    
	    this.temp = r.temp;
	    this.invert = r.invert;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		
		this.temp = getIntFromData(this.temp, 100, id, data);
		if (id == 102) this.invert = data != 0;
	}
}
