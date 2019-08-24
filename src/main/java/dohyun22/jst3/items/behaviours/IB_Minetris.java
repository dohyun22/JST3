package dohyun22.jst3.items.behaviours;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIMinetris;
import dohyun22.jst3.container.ContainerDummy;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class IB_Minetris extends ItemBehaviour {

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (!JSTUtils.isClient()) {
			pl.openGui(JustServerTweak.INSTANCE, 1000, w, MathHelper.floor(pl.posX), MathHelper.floor(pl.posY), MathHelper.floor(pl.posZ));
			return new ActionResult(EnumActionResult.SUCCESS, st);
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public Object getServerGUI(int id, EntityPlayer pl, World w, BlockPos p) {
		return new ContainerDummy();
	}

	@Override
	public Object getClientGUI(int id, EntityPlayer pl, World w, BlockPos p) {
		return new GUIMinetris(new ContainerDummy());
	}
}
