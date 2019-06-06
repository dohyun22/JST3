package dohyun22.jst3.compat.tic;

import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;

public class ModAspectElec extends ModifierAspect {

	@Override
	public boolean canApply(ItemStack st, ItemStack original) throws TinkerGuiException {
		if (!st.hasTagCompound())
			return false;
		if (JSTUtils.getMaxEUInItem(st) <= 0)
			throw new TinkerGuiException(JSTUtils.translate("jst.compat.tic.err.eleconly"));
	    return true;
	}

	@Override
	public void updateNBT(NBTTagCompound nbt, NBTTagCompound mod) {}
}
