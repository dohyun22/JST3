package dohyun22.jst3.items.behaviours;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class IB_Migrate extends ItemBehaviour {
	private final ItemStack replacement;
	
	public IB_Migrate(ItemStack stack) {
		replacement = stack;
	}

	@Override
    public void update(ItemStack st, World w, Entity e, int sl, boolean select) {
		try {
			if (e instanceof EntityPlayer) {
				ItemStack st2 = replacement.copy();
				NBTTagCompound tag = st.getTagCompound();
				st2.setTagCompound(tag);
				((EntityPlayer)e).inventory.setInventorySlotContents(sl, st2);
			}
		} catch (Exception ex) {}
    }
}
