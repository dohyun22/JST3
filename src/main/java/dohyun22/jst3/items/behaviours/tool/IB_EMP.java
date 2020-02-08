package dohyun22.jst3.items.behaviours.tool;

import java.util.List;

import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.utils.EMPBlast;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class IB_EMP extends ItemBehaviour {

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		new EMPBlast(w, pl.getPosition(), 10, true, true).doEMP();
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.emp"));
	}
}
