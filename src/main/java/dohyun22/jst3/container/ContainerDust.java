package dohyun22.jst3.container;

import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerDust extends Container {
	public static final int mapSz = 11;
	private final InventoryPlayer playerinv;
	private int ssz = MathHelper.ceil((mapSz * mapSz) / 4.0D);
	private boolean loaded;
	private int cx2, cz2, dust2, sp2;
	private ChunkPos start;
	public int cx, cz, dust, sp;
	public byte[] dustmap = new byte[mapSz * mapSz];

	public ContainerDust(InventoryPlayer pi) {
		playerinv = pi;
	}

	@Override
	public boolean canInteractWith(EntityPlayer pl) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (JSTUtils.isClient()) return;
		if (playerinv.player != null && !loaded) {
			start = new ChunkPos(playerinv.player.getPosition());
			cx = start.x;
			cz = start.z;
			sp = (mapSz / 2) + (mapSz / 2) * mapSz;
			dust = JSTChunkData.getFineDust(playerinv.player.world, start);
			for (int x = 0; x < mapSz; x++) {
				for (int z = 0; z < mapSz; z++) {
					byte b = (byte) MathHelper.clamp(JSTChunkData.getFineDust(playerinv.player.world, new ChunkPos(start.x + x - mapSz / 2, start.z + z - mapSz / 2)) / 16000, 0, 15);
					try {
						dustmap[x + z * mapSz] = b;
					} catch (Exception e) {}
				}
			}

			for (int n = 0; n < ssz; n++) {
				short s = 0;
				for (int o = 0; o < 4; o++) {
					int idx = o + n * 4;
					if (idx < dustmap.length)
						s += ((short)dustmap[idx]) << (o * 4);
				}
				for (IContainerListener icl : listeners)
					icl.sendWindowProperty(this, n, s);
			}
			loaded = true;
		}
	    for (IContainerListener icl : listeners) {
            if (cx != cx2)
            	ContainerMTE.splitIntAndSend(icl, this, 200, cx);
            if (cz != cz2)
            	ContainerMTE.splitIntAndSend(icl, this, 202, cz);
            if (dust != dust2)
            	ContainerMTE.splitIntAndSend(icl, this, 204, dust);
            if (sp != sp2)
            	icl.sendWindowProperty(this, 206, sp);
        }
	    cx2 = cx;
	    cz2 = cz;
	    dust2 = dust;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id < ssz) {
			for (int n = 0; n < 4; n++) {
				int idx = n + id * 4;
				if (idx >= 0 && idx < dustmap.length)
					dustmap[idx] = (byte)((data >> (n * 4)) & 15);
			}
		}
		cx = ContainerMTE.getIntFromData(cx, 200, id, data);
		cz = ContainerMTE.getIntFromData(cz, 202, id, data);
		dust = ContainerMTE.getIntFromData(dust, 204, id, data);
		if (id == 206) sp = data;
	}

	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
		if (playerinv.player != null && ct == ClickType.QUICK_CRAFT && si >= 0 && si < mapSz * mapSz) {
			sp = si;
			if (start != null) {
				cx = start.x + (si % mapSz) - (mapSz / 2);
				cz = start.z + (si / mapSz) - (mapSz / 2);
				dust = JSTChunkData.getFineDust(playerinv.player.world, new ChunkPos(cx, cz));
			}
		}
		return ItemStack.EMPTY;
	}
}
