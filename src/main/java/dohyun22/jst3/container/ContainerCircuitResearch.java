package dohyun22.jst3.container;

import dohyun22.jst3.container.JSTSlot.ItemMatcher;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.machine.MT_CircuitResearchMachine;
import dohyun22.jst3.tiles.machine.MT_CircuitResearchMachine.IC;
import dohyun22.jst3.tiles.machine.MT_CircuitResearchMachine.MiniGameTile;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCircuitResearch extends ContainerMTE {
	public final int row = 12;
	public final int column = 9;
	public byte[] listOfGame = new byte[row * column];

	public ContainerCircuitResearch(IInventory inv, TileEntityMeta te) {
		super(te);
		addSlotToContainer(new JSTSlot(te, 0, 185, 26).setPredicate(new ItemMatcher(new ItemStack(JSTItems.item1, 1, 190))));
		addSlotToContainer(new JSTSlot(te, 1, 185, 44).setPredicate(new ItemMatcher("paper")));
		addSlotToContainer(new JSTSlot(te, 2, 185, 62).setPredicate(new ItemMatcher("wireSolder")));
		addSlotToContainer(new JSTSlot(te, 3, 185, 80).setPredicate(new ItemMatcher(new ItemStack(JSTItems.item1, 1, 10050))));
		addSlotToContainer(new JSTSlot(te, 4, 185, 98, false, true, 64, true));
		
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
			int id = si % 1000;
			if ((mc == 0 || mc == 1) && ct == ClickType.QUICK_CRAFT && id < MT_CircuitResearchMachine.ROW * MT_CircuitResearchMachine.COLUMN) {
				try {
					MT_CircuitResearchMachine cr = (MT_CircuitResearchMachine) te.mte;
					if (!cr.canRun()) return ItemStack.EMPTY;
					byte g = cr.listOfGame[id];
					if (g >= 0 && g <= 10) {
						if (mc == 0) {
							if (g <= 4) {
								int dir = si / 1000;
								byte n = 0;
								switch (g) {
								case 0:
									n = (byte)dir; break;
								case 1:
									switch (dir) {
									case 2: n = 5; break;
									case 3: n = 7; break;
									case 4: n = 8; break;
									} break;
								case 2:
									switch (dir) {
									case 1: n = 5; break;
									case 3: n = 9; break;
									case 4: n = 10; break;
									} break;
								case 3:
									switch (dir) {
									case 1: n = 7; break;
									case 2: n = 9; break;
									case 4: n = 6; break;
									} break;
								case 4:
									switch (dir) {
									case 1: n = 8; break;
									case 2: n = 10; break;
									case 3: n = 6; break;
									} break;
								}
								if (n > 0) {
									if (cr.solder <= 0) {
										cr.getStackInSlot(2).shrink(1);
										cr.solder = MT_CircuitResearchMachine.SOLDER_PER_WIRE;
									}
									cr.solder--;
									JSTItems.item1.getBehaviour(cr.getStackInSlot(3)).useEnergy(cr.getStackInSlot(3), 100, false);
									cr.listOfGame[id] = n;
									if (g != 0) cr.checkClear();
								}
							}
						} else cr.listOfGame[id] = 0;
					}
				} catch (Exception e) {}
			}
			return ItemStack.EMPTY;
		} else {
			try {((MT_CircuitResearchMachine)te.mte).checkAndLoad();} catch (Exception e) {}
			return super.slotClick(si, mc, ct, pl);
		}
	}
}