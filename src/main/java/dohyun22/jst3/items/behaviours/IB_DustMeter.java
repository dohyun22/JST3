package dohyun22.jst3.items.behaviours;

import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class IB_DustMeter extends ItemBehaviour {

	public IB_DustMeter() {
		maxEnergy = 10000;
	}

	@Override
	public int getTier(ItemStack st) {
		return 1;
	}
	
	@Override
	public boolean canCharge(ItemStack st) {
		return true;
	}
	
	@Override
	public boolean canDischarge(ItemStack st) {
		return false;
	}
	
	@Override
	public long transferLimit(ItemStack st) {
		return 32L;
	}

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (!w.isRemote && getEnergy(st) >= 100) {
			if (!pl.capabilities.isCreativeMode)
				setEnergy(st, getEnergy(st) - 100);
			int p = JSTChunkData.getFineDust(w, new ChunkPos(pl.getPosition()));
			float pm = p / 1000.0F;
			w.playSound(null, pl.getPosition(), JSTSounds.SCAN, SoundCategory.PLAYERS, 1.0F, 1.0F);
			TextFormatting col = pm < 0 ? TextFormatting.WHITE : pm < 30 ? TextFormatting.BLUE : pm < 80 ? TextFormatting.GREEN : pm < 150 ? TextFormatting.YELLOW : pm < 300 ? TextFormatting.RED : TextFormatting.DARK_RED;
			JSTUtils.sendSimpleMessage(pl, col.toString() + (pm > 0.0F ? pm : 0.0F) + " \u338D/\u33A5\u00A7r");
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, maxEnergy);
		sub.add(st);
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
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		addEnergyTip(st, ls);
	}
}
