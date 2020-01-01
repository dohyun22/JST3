package dohyun22.jst3.items.behaviours.energy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class IB_Charger extends IB_Battery {
	private final boolean isSolar;
	
	public IB_Charger(long storage, int tier, boolean solar) {
		super(storage, tier, 2.0F, 0);
		isSolar = solar;
	}
	
	@Override
    public void update(ItemStack st, World w, Entity e, int sl, boolean select) {
		if (e instanceof EntityPlayerMP && w.getTotalWorldTime() % 4L == 0) {
			EntityPlayer pl = (EntityPlayer) e;
			if (isSolar && JSTUtils.checkSun(w, pl.getPosition()))
				charge(st, JSTUtils.getVoltFromTier(tier), tier, true, false);
			
			if (st.hasTagCompound() && st.getTagCompound().getBoolean("isON")) {
				List<ItemStack> inv = pl.inventory.mainInventory;
				long lim = transferLimit(st);
				for (int n = 0; n < inv.size(); n++) {
					if (lim <= 0) break;
					if (n != pl.inventory.currentItem || !pl.isHandActive()) {
						ItemStack st2 = inv.get(n);
						if (!st2.isEmpty() && st != st2)
							lim -= discharge(st, JSTUtils.chargeItem(st2, discharge(st, lim, tier, false, true), tier, false, false), tier, false, false);
					}
				}
				inv = pl.inventory.armorInventory;
				for (int n = 0; n < inv.size(); n++) {
					if (lim <= 0) break;
					ItemStack st2 = inv.get(n);
					if (!st2.isEmpty() && st != st2 && JSTUtils.dischargeItem(st2, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) <= 0)
						lim -= discharge(st, JSTUtils.chargeItem(st2, discharge(st, lim, tier, false, true), tier, false, false), tier, false, false);
				}
				inv = pl.inventory.offHandInventory;
				for (int n = 0; n < inv.size(); n++) {
					if (lim <= 0) break;
					ItemStack st2 = inv.get(n);
					if (!st2.isEmpty() && st != st2)
						lim -= discharge(st, JSTUtils.chargeItem(st2, discharge(st, lim, tier, false, true), tier, false, false), tier, false, false);
				}
			}
		}
	}
	
	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		ActionResult<ItemStack> ret = new ActionResult(EnumActionResult.PASS, st);
		if (JSTUtils.isClient()) return ret;
		NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
		boolean b = !nbt.getBoolean("isON");
		nbt.setBoolean("isON", b);
		pl.sendMessage(new TextComponentTranslation("jst.msg.com." + (b ? "on" : "off")));
		w.playSound(null, pl.posX, pl.posY, pl.posZ, JSTSounds.SWITCH, SoundCategory.BLOCKS, 0.75F, 1.0F);
		return ret;
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.charger"));
		if (isSolar) ls.add(I18n.format("jst.tooltip.scharger"));
		super.getInformation(st, w, ls, adv);
		ls.add(I18n.format("jst.msg.com." + (st.hasTagCompound() && st.getTagCompound().getBoolean("isON") ? "on" : "off")));
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return !st.hasTagCompound() || !st.getTagCompound().getBoolean("isON");
	}
}
