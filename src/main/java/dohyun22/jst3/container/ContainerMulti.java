package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.multiblock.MT_Multiblock;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerMulti extends ContainerMTE {
	/** 0 = incomplete, 1 = standby, 2 = working */
	public byte state;
	public int data;

	public ContainerMulti(IInventory inv, TileEntityMeta te) {
		super(te);
		addPlayerInventorySlots(inv, 8, 104);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	    if (this.te.getWorld().isRemote ||!(te.mte instanceof MT_Multiblock))
	        return;

	    MT_Multiblock r = (MT_Multiblock)te.mte;
	    
	    for (IContainerListener icl : this.listeners) {
            if (this.state != r.getMode())
            	icl.sendWindowProperty(this, 100, r.getMode());
            if (this.data != r.getData())
            	splitIntAndSend(icl, this, 101, r.getData());
	    }
	    
	    this.state = r.getMode();
	    this.data = r.getData();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int dat) {
		super.updateProgressBar(id, dat);
		if (id == 100) state = (byte) dat;
		data = this.getIntFromData(data, 101, id, dat);
	}
}
