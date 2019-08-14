package dohyun22.jst3.items.behaviours;

import java.util.List;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import toughasnails.api.TANPotions;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;

public class IB_AirConditioner extends ItemBehaviour {

	public IB_AirConditioner() {
		maxEnergy = 120000;
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
		return 256;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (!JSTUtils.isClient()) {
			NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
			boolean b = !nbt.getBoolean("isON");
			nbt.setBoolean("isON", b);
			JSTUtils.sendMessage(pl, "jst.msg.com." + (b ? "on" : "off"));
			w.playSound(null, pl.posX, pl.posY, pl.posZ, JSTSounds.SWITCH2, SoundCategory.BLOCKS, 0.75F, 1.0F);
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
    public void update(ItemStack st, World w, Entity e, int sl, boolean select) {
		if (!w.isRemote && w.getTotalWorldTime() % 100L == 0 && e instanceof EntityPlayer && getEnergy(st) > 400 && st.getTagCompound().getBoolean("isON")) {
			try {
				setEnergy(st, getEnergy(st) - 400);
				ITemperature td = TemperatureHelper.getTemperatureData((EntityPlayer)e);
				Temperature pt = td.getTemperature();
				int ct = pt.getRawValue();
	            if (ct < 14)
	            	td.setTemperature(new Temperature(Math.min(14, ct + 4)));
	            else if (ct > 14)
	            	td.setTemperature(new Temperature(Math.max(14, ct - 4)));
	            ((EntityPlayer)e).removePotionEffect(TANPotions.hyperthermia);
	            ((EntityPlayer)e).removePotionEffect(TANPotions.hypothermia);
			} catch (Throwable t) {}
		}
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, this.maxEnergy);
		sub.add(st);
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		addEnergyTip(st, ls);
		ls.add(I18n.format("jst.msg.com." + (st.hasTagCompound() && st.getTagCompound().getBoolean("isON") ? "on" : "off")));
	}
}
