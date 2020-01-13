package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MT_GTI;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerGTI extends ContainerMTE {
	public byte err;
	public long output;
	public int size;

	public ContainerGTI(IInventory inv, TileEntityMeta te) {
		super(te);
		addPlayerInventorySlots(inv, 8, 84);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (te.getWorld().isRemote || !(te.mte instanceof MT_GTI))
			return;

		MT_GTI r = (MT_GTI) te.mte;
		for (int i = 0; i < listeners.size(); ++i) {
			IContainerListener icl = (IContainerListener) listeners.get(i);

			if (err != r.getErrorState())
				icl.sendWindowProperty(this, 50, r.getErrorState());
			
			if (output != r.output)
				splitLongAndSend(icl, this, 51, (long)(r.output * 100));
			
			if (size != r.size)
				splitIntAndSend(icl, this, 55, r.size);
		}

		err = r.getErrorState();
		output = (long)(r.output * 100);
		size = r.size;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		
		if (id == 50)
			err = (byte) data;
		output = getLongFromData(output, 51, id, data);
		size = getIntFromData(size, 55, id, data);
	}
}
