package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MetaTileGridTieInverter;
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
		if (this.te.getWorld().isRemote || !(te.mte instanceof MetaTileGridTieInverter)) {
			return;
		}

		MetaTileGridTieInverter r = (MetaTileGridTieInverter) te.mte;

		for (int i = 0; i < this.listeners.size(); ++i) {
			IContainerListener icl = (IContainerListener) this.listeners.get(i);

			if (this.err != r.getErrorState())
				icl.sendWindowProperty(this, 50, r.getErrorState());
			
			if (this.output != r.output)
				splitLongAndSend(icl, this, 51, (long)(r.output * 100));
			
			if (this.size != r.size)
				splitIntAndSend(icl, this, 55, r.size);
		}

		this.err = r.getErrorState();
		this.output = (long)(r.output * 100);
		this.size = r.size;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		
		if (id == 50)
			this.err = (byte) data;
		this.output = getLongFromData(this.output, 51, id, data);
		this.size = getIntFromData(this.size, 55, id, data);
	}
}
