package dohyun22.jst3.items.behaviours.misc;

import java.util.List;

import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import toughasnails.api.TANPotions;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;

public class IB_Bottle extends ItemBehaviour {
	private final byte mode;

	public IB_Bottle(int m) {
		mode = (byte) m;
	}

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		boolean flag = false;
		if (mode == 0) {
			if (!w.isRemote) {
				RayTraceResult rtr = JSTUtils.rayTraceEntity(pl, true, false, false, 0);
				if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK && w.getBlockState(rtr.getBlockPos()).getMaterial() == Material.WATER) {
					JSTUtils.giveItem(pl, new ItemStack(JSTItems.item1, 1, 10024));
					st.shrink(1);
					w.playSound(null, pl.posX, pl.posY, pl.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
					flag = true;
				}
			}
		} else if (!pl.isCreative()) {
			if (Loader.isModLoaded("toughasnails"))
				try {
					IThirst td = ThirstHelper.getThirstData(pl);
					if (td.getThirst() < 20)
						flag = true;
				} catch (Throwable t) {}
			else
				flag = true;
			if (flag) pl.setActiveHand(h);
		}
		return new ActionResult<ItemStack>(flag ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, st);
	}

	@Override
	public ItemStack onUseFinish(ItemStack st, World w, EntityLivingBase e) {
		if (e instanceof EntityPlayer) {
			if (Loader.isModLoaded("toughasnails")) {
				try {
					IThirst td = ThirstHelper.getThirstData((EntityPlayer) e);
					boolean np = mode == 1;
					td.addStats(np ? 3 : 6, np ? 0.1F : 0.5F);
					if (!w.isRemote && np && w.rand.nextFloat() < 0.75F && SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST))
						e.addPotionEffect(new PotionEffect(TANPotions.thirst, 600));
				} catch (Throwable t) {}
			}
			if (!st.hasTagCompound() || !st.getTagCompound().getBoolean("NoReuse"))
				JSTUtils.giveItem((EntityPlayer) e, new ItemStack(JSTItems.item1, 1, 10023));
		}
		st.shrink(1);
		return st;
	}

	@Override
    public EnumAction useAction(ItemStack st) {
        return EnumAction.DRINK;
    }

	@Override
	public int getMaxItemUseDuration(ItemStack st) {
		return 32;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		if (st.hasTagCompound() && st.getTagCompound().getBoolean("NoReuse"))
			ls.add(I18n.format("jst.msg.com.noreuse"));
	}
}
