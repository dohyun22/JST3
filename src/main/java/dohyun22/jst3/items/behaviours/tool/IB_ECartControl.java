package dohyun22.jst3.items.behaviours.tool;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIECart;
import dohyun22.jst3.container.ContainerECart;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IB_ECartControl extends ItemBehaviour {
	
	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (!w.isRemote && pl.getRidingEntity() instanceof EntityMinecart)
			pl.openGui(JustServerTweak.INSTANCE, 1000, w, 0, 0, 0);
		return new ActionResult(EnumActionResult.PASS, st);
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
	
	@Override
	public Object getServerGUI(int id, EntityPlayer pl, World w, BlockPos p) {
		return new ContainerECart(pl, null);
	}

	@Override
	public Object getClientGUI(int id, EntityPlayer pl, World w, BlockPos p) {
		return new GUIECart(new ContainerECart(pl, null));
	}
}
