package dohyun22.jst3.items.behaviours.misc;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.machine.MT_CircuitResearchMachine;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class IB_BluePrint extends ItemBehaviour {	
	public IB_BluePrint() {}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		int s = getSolder(st);
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.blueprint", getCircuitTier(st), s, ItemStack.DECIMALFORMAT.format(s / (double)MT_CircuitResearchMachine.SOLDER_PER_WIRE)));
	}	
	
	public static int getSolder(ItemStack st) {
		return st.hasTagCompound() ? st.getTagCompound().getInteger("consumeSize") : 0;
	}
	
	public static int getCircuitTier(ItemStack st) {
		return st.hasTagCompound() ? st.getTagCompound().getInteger("tier") : 0;
	}
	
	public static void setSizeOfConsumedSolderAndTier(ItemStack st, int s, int t) {
		NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
		nbt.setInteger("consumeSize", s);
		nbt.setInteger("tier", t);
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		setSizeOfConsumedSolderAndTier(st, 16, 1); sub.add(st);
	}
}