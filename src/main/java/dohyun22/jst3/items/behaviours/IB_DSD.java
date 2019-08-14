package dohyun22.jst3.items.behaviours;

import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

public class IB_DSD extends ItemBehaviour {
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		if (!st.hasTagCompound()) return;
		NBTTagList tl = st.getTagCompound().getTagList("DSD_E", 8);
		for (int n = 0; n < tl.tagCount(); n++) {
			String str = tl.getStringTagAt(n);
			ls.add(str.startsWith("<T>") ? I18n.format(str.replaceFirst("<T>", "")) : str);
		}
	}
	
	public static void setData(ItemStack st, @Nullable NBTTagCompound data, String... desc) {
		if (st.getItem() != JSTItems.item1 || !(JSTItems.item1.getBehaviour(st) instanceof IB_DSD))
			return;
		if (st.hasTagCompound())
			st.getTagCompound().removeTag("DSD_D");
		if (data == null)
			return;
		NBTTagCompound tag = JSTUtils.getOrCreateNBT(st);
		if (desc.length > 0) {
			NBTTagList tl = new NBTTagList();
			for (String s : desc)
				tl.appendTag(new NBTTagString(s == null ? "" : s));
			tag.setTag("DSD_E", tl);
		}
		tag.setTag("DSD_D", data);
	}
	
	@Nullable
	public static NBTTagCompound getData(ItemStack st) {
		if (st.getItem() != JSTItems.item1 || !(JSTItems.item1.getBehaviour(st) instanceof IB_DSD))
			return null;
		NBTTagCompound tag = st.getTagCompound();
		if (tag != null) {
			NBTTagCompound ret = tag.getCompoundTag("DSD_D");
			return ret.hasNoTags() ? null : ret;
		}
		return null;
	}
}
