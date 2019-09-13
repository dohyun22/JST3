package dohyun22.jst3.container;

import dohyun22.jst3.compat.rc.CompatRC;
import dohyun22.jst3.entity.EntityPoweredCart;
import dohyun22.jst3.utils.JSTSounds;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class ContainerECart extends Container {
	private final EntityPlayer player;
	private final EntityMinecart cart;
	
	public ContainerECart(EntityPlayer pl, EntityMinecart ec) {
		player = pl;
		cart = ec;
		for (int n = 0; n < 5; n++)
			addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, n, 8 + 18 * n, 8, false, false, 1, false));
		addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, 5, 102, 8, false, false, 1, false));
		addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, 6, 124, 8, false, false, 1, false));
	}

	@Override
	public boolean canInteractWith(EntityPlayer pl) {
		return pl.getRidingEntity() instanceof EntityMinecart || (cart != null && !cart.isDead);
	}

	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
		if (pl.world.isRemote || !canInteractWith(pl) || ct != ClickType.PICKUP) return super.slotClick(si, mc, ct, pl);
		try {
			byte snd = 0;
			Boolean r = null;
			EntityMinecart ec = pl.getRidingEntity() instanceof EntityMinecart ? (EntityMinecart) pl.getRidingEntity() : cart;
			if (ec == null) return super.slotClick(si, mc, ct, pl);
			for (EntityMinecart emc : CompatRC.getCartsInTrain(ec)) {
				if (emc instanceof EntityPoweredCart) {
					EntityPoweredCart pc = (EntityPoweredCart)emc;
					switch (si) {
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
						pc.setSpeed((byte) si);
						snd = 1;
						break;
					case 5:
						if (r == null) {
							pc.reverse = !pc.reverse;
							r = Boolean.valueOf(pc.reverse);
						} else {
							pc.reverse = r.booleanValue();
						}
						snd = 2;
						break;
					case 6:
						pc.klaxon();
						break;
					}
				}
			}
			if (snd > 0) {
				SoundEvent se = null;
				switch (snd) {
				case 1:
					se = JSTSounds.SWITCH2;
					break;
				case 2:
					se = JSTSounds.SWITCH;
					break;
				}
				if (se != null) pl.world.playSound(null, pl.posX, pl.posY, pl.posZ, se, SoundCategory.MASTER, 1.0F, 1.0F);
			}
		} catch (Throwable t) {}
		return super.slotClick(si, mc, ct, pl);
	}
}
