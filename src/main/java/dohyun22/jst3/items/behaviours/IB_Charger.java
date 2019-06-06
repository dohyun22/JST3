package dohyun22.jst3.items.behaviours;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
				this.charge(st, JSTUtils.getVoltFromTier(tier), tier, true, false);
			
			if (st.hasTagCompound() && st.getTagCompound().getBoolean("isON")) {
				List<ItemStack> inv = pl.inventory.mainInventory;
				long lim = transferLimit(st);
				int tier = getTier(st);
				for (int n = 0; n < inv.size() && lim > 0; n++) {
					if (n == pl.inventory.currentItem && pl.isHandActive())
						continue;
					ItemStack st2 = (ItemStack) inv.get(n);
					if (!st2.isEmpty() && !OreDictionary.itemMatches(st, st2, false))
						lim -= discharge(st, JSTUtils.chargeItem(st2, lim, tier, false, false), tier, false, false);
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
	@SideOnly(Side.CLIENT)
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		List<String> ret = new ArrayList();
		ret.addAll(JSTUtils.getListFromTranslation("jst.tooltip.charger"));
		if (isSolar) ret.add(I18n.format("jst.tooltip.scharger"));
		ret.addAll(super.getInformation(st, w, adv));
		ret.add(I18n.format("jst.msg.com." + (st.hasTagCompound() && st.getTagCompound().getBoolean("isON") ? "on" : "off")));
		return ret;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
}
