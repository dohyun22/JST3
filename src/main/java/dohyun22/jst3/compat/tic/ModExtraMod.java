package dohyun22.jst3.compat.tic;

import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModExtraMod extends Modifier {

	public ModExtraMod(int nid) {
		super("jst_exmod" + nid);
		addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this, 0xFFFF00));
	}

	@Override
	public boolean isHidden() {
		return true;
	}

	@Override
	public void applyEffect(NBTTagCompound root, NBTTagCompound mod) {
		ToolNBT data = TagUtil.getToolStats(root);
		data.modifiers += 1;
		TagUtil.setToolTag(root, data.get());
	}
	
	@Override
	public String getLocalizedName() {
		return JSTUtils.translate("jst.compat.tic.mod.exmod.name");
	}
	
	@Override
	public String getLocalizedDesc() {
		return getLocalizedName();
	}
}
