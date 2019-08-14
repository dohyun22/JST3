package dohyun22.jst3.items.behaviours;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class IB_Damageable extends ItemBehaviour {
	public IB_Damageable(int maxdmg) {
		this.maxUse = maxdmg;
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.add(Math.max(maxUse - (st.hasTagCompound() ? st.getTagCompound().getInteger("damage") : 0), 0) + " / " + maxUse);
	}
}