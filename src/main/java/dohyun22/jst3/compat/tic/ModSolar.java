package dohyun22.jst3.compat.tic;

import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ModSolar extends ModifierTrait {

	public ModSolar() {
		super("jst_solar", 0x2200FF, 1, 0);
		aspects.clear();
		addAspects(new ModifierAspect.MultiAspect(this, 0xC0CDEF, 1, 32, 1), new ModAspectElec());
	}

	@Override
	public String getLocalizedName() {
		return JSTUtils.translate("jst.compat.tic.mod.solar.name");
	}
	
	@Override
	public String getLocalizedDesc() {
		return JSTUtils.translate("jst.compat.tic.mod.solar.desc");
	}
	
	@Override
	public void onUpdate(ItemStack st, World w, Entity e, int sl, boolean select) {
		if (!w.isRemote && JSTUtils.checkSun(w, e.getPosition()))
			JSTUtils.chargeItem(st, ModifierNBT.readInteger(TinkerUtil.getModifierTag(st, getModifierIdentifier())).current, Integer.MAX_VALUE, true, false);
	}
	
	@Override
	public String getTooltip(NBTTagCompound nbt, boolean detailed) {
	    return getLocalizedName() + " (" + ModifierNBT.readInteger(nbt).current + " / 32 EU)";
	}
}
