package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.test.MT_CircuitResearchMachine;
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
		addPlayerInventorySlots(inv, 29, 143);
	}

	public int num = 100;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (JSTUtils.isClient()) return;
		
		MT_CircuitResearchMachine cr = (MT_CircuitResearchMachine) te.mte;
		for (int i = 0; i < listeners.size(); ++i) {
			IContainerListener icl = (IContainerListener) listeners.get(i);
			for (int j = 0; j < listOfGame.length / 2; j++) {
				byte a = cr.listOfGame[j * 2];
				byte b = cr.listOfGame[j * 2 + 1];
				if (listOfGame[j * 2] != a || listOfGame[j * 2 + 1] != b) {
					ContainerMTE.combineBytesAndSend(icl, this, j + num, a, b);
				}
				listOfGame[j * 2] = a;
				listOfGame[j * 2 + 1] = b;
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
			listOfGame[newId] = a;
			listOfGame[newId + 1] = b;
		}
	}

	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
		if (si >= 1000) {
			if ((mc == 0 || mc == 1) && ct == ClickType.QUICK_CRAFT && si < MT_CircuitResearchMachine.row * MT_CircuitResearchMachine.column + 1000) {
				try {
					int dir = si / 1000, id = si % 1000;
					MT_CircuitResearchMachine cr = (MT_CircuitResearchMachine) te.mte;
					if (mc == 0)
						cr.listOfGame[id] = (byte) dir;
					else
						cr.listOfGame[id] = 0;
				} catch (Exception e) {}
			}
			return ItemStack.EMPTY;
		} else
			return super.slotClick(si, mc, ct, pl);
	}
}
