package dohyun22.jst3.items.behaviours;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_Damageable extends ItemBehaviour {
	public IB_Damageable(int maxdmg) {
		this.maxUse = maxdmg;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		List<String> ret = new ArrayList();
		ret.add(Math.max(this.maxUse - (st.hasTagCompound() ? st.getTagCompound().getInteger("damage") : 0), 0) + " / " + this.maxUse);
		return ret;
	}
}