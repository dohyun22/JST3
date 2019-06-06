package dohyun22.jst3.compat.tic;

import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class ModNanoRepair extends ModifierTrait {
	private static final int EU_NEED = 250;

	public ModNanoRepair() {
		super("jst_nano", 0x321B8F);
		addAspects(new ModifierAspect.SingleAspect(this), new ModAspectElec());
	}

	@Override
	public String getLocalizedName() {
		return JSTUtils.translate("jst.compat.tic.mod.nano.name");
	}
	
	@Override
	public String getLocalizedDesc() {
		return JSTUtils.translate("jst.compat.tic.mod.nano.desc");
	}
	
	@Override
	public void onUpdate(ItemStack st, World w, Entity e, int sl, boolean select) {
		if (!w.isRemote && w.getTotalWorldTime() % 20 == 0L && e instanceof EntityLivingBase && st.getItemDamage() > 0 && JSTUtils.dischargeItem(st, EU_NEED, Integer.MAX_VALUE, true, true) >= EU_NEED) {
			ToolHelper.repairTool(st, 1, (EntityLivingBase) e);
			JSTUtils.dischargeItem(st, EU_NEED, Integer.MAX_VALUE, true, false);
		}
	}
}
