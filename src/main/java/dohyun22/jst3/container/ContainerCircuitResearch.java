package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.test.MT_CircuitResearchMachine;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCircuitResearch extends ContainerMTE {
	public final int row = 12;
	public final int column = 9;
	public byte[] listOfGame = new byte[row * column];

	public ContainerCircuitResearch(IInventory inv, TileEntityMeta te) {
		super(te);
		for (int r = 0; r < 5; r++)
			addSlotToContainer(new Slot(te, r, 185, 26 + r * 18));
		addPlayerInventorySlots(inv, 29, 141);
	}

	public int num = 100;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if(JSTUtils.isClient())return;
		
		MT_CircuitResearchMachine circuitResearch = (MT_CircuitResearchMachine) te.mte;

		for (int i = 0; i < this.listeners.size(); ++i) {
			IContainerListener icl = (IContainerListener) this.listeners.get(i);
			for (int j = 0; j < this.listOfGame.length / 2; j++) {
				byte a = circuitResearch.listOfGame[j * 2];
				byte b = circuitResearch.listOfGame[j * 2 + 1];
				if (this.listOfGame[j * 2] != a || this.listOfGame[j * 2 + 1] != b) {
					this.combineByteAndSend(icl, this, a, b, j + num);
				}
				this.listOfGame[j * 2] = a;
				this.listOfGame[j * 2 + 1] = b;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id - num >= 0 && id - num < row * column / 2) {
			int newId = id - num;
			newId *= 2;
			byte a = (byte)(data >> 8);
			byte b = (byte)(data & 0xFF);
			this.listOfGame[newId] = a;
			this.listOfGame[newId + 1] = b;
		}
	}

	public static void combineByteAndSend(IContainerListener icl, Container con, byte a, byte b, int in) {
		short combined = a;
		combined <<= 8;
		combined += b;
		icl.sendWindowProperty(con, in, combined);
	}
	
}
