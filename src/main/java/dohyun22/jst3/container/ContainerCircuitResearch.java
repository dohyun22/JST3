package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.test.MT_CircuitResearchMachine;
import dohyun22.jst3.utils.JSTChunkData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCircuitResearch extends ContainerMTE {
	public final int row = 9;
	public final int coloum = 12;
	public byte[] listOfGame = new byte[row * coloum];

	public ContainerCircuitResearch(IInventory inv, TileEntityMeta te) {
		super(te);
		for (int r = 0; r < 5; r++)
			addSlotToContainer(new Slot(te, r, 185, 26 + r * 18));
		addPlayerInventorySlots(inv, 29, 141);
	}

	public int num = 10;
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (this.te.getWorld().isRemote || !(te.mte instanceof MT_CircuitResearchMachine)) {
			return;
		}
		MT_CircuitResearchMachine circuitResearch = (MT_CircuitResearchMachine) te.mte;

		for (int i = 0; i < this.listeners.size(); ++i) {
			IContainerListener icl = (IContainerListener) this.listeners.get(i);
			for (int j = 0; j < this.listOfGame.length; j++) {
				byte r = circuitResearch.listOfGame[j];
				if (this.listOfGame[j] != r) {
					icl.sendWindowProperty(this, j + num, (int) r);
					System.out.println((j + num) + ":" + r + ":" + (int)r);
				}
				this.listOfGame[j] = r;
			}
		}
	}

	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
		/*if (!pl.world.isRemote && si == 6 && ct == ClickType.PICKUP) {
			((MT_CircuitResearchMachine)te.mte).displayRF = !((MT_CircuitResearchMachine)te.mte).displayRF;
		}*/
		return super.slotClick(si, mc, ct, pl);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		System.out.println(id);
		if (id - num >= 0 && id - num < row * coloum) {
			System.out.println((byte) data);
			this.listOfGame[id - num] = (byte) data;
		}
	}

}
