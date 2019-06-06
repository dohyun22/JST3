package dohyun22.jst3.compat;

import java.util.HashMap;
import java.util.Map;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class CompatBWM extends Loadable {
	/** Used for bypassing BWM HCPiles feature */
	private static Map<IBlockState, ItemStack> HCPiles;

	@Override
	public String getRequiredMod() {
		return "betterwithmods";
	}
	
	@Override
	public void postInit() {
		try {
			HCPiles = (Map<IBlockState, ItemStack>) ReflectionUtils.getField(Class.forName("betterwithmods.module.hardcore.needs.HCPiles"), "blockStateToPile").get(null);
		} catch (Throwable e) {
			JSTUtils.LOG.error("Error loading HCPile Block lists.");
			e.printStackTrace();
		}
	}
	
	public static boolean isHCPileAffected(IBlockState bs) {
		if (HCPiles == null) return false;
		return HCPiles.containsKey(bs);
	}
}
