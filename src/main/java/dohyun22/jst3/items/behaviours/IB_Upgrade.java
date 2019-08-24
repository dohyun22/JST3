package dohyun22.jst3.items.behaviours;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IB_Upgrade extends ItemBehaviour {
	private final String id, tip;

	public IB_Upgrade(@Nonnull String id) {
		this(id, null);
	}

	public IB_Upgrade(@Nonnull String id, @Nullable String tip) {
		this.id = id;
		this.tip = tip;
	}

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer ep, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote || id == null) return EnumActionResult.PASS;
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		if (mte != null && mte.tryUpgrade(id)) {
			if (!ep.isCreative()) st.shrink(1);
			w.playSound(null, p, JSTSounds.SWITCH, SoundCategory.BLOCKS, 1.0F, 0.8F);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		if (tip != null) ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.upg." + tip));
	}
}
