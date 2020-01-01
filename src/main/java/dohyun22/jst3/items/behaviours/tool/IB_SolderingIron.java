package dohyun22.jst3.items.behaviours.tool;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.items.behaviours.ItemBehaviour;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class IB_SolderingIron extends ItemBehaviour {

	public IB_SolderingIron() {
		maxEnergy = 20000;
	}

	@Override
	public int getTier(ItemStack st) {
		return 1;
	}

	@Override
	public boolean canCharge(ItemStack st) {
		return true;
	}

	@Override
	public boolean canDischarge(ItemStack st) {
		return false;
	}

	@Override
	public long transferLimit(ItemStack st) {
		return 32;
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		addEnergyTip(st, ls);
		ls.add(I18n.format("jst.tooltip.solderingiron"));
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, this.maxEnergy);
		sub.add(st);
	}
	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}
}
