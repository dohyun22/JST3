package dohyun22.jst3.items.behaviours;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class IB_Food extends ItemBehaviour {
	private final int amount;
	private final float saturation;
	private final int duration;
	private final boolean alwaysEdible;
	private final boolean isDrink;
	private final PotionEffect[] effects;
	
    public IB_Food(int amt, float sat, int dur, boolean alway, boolean drink, PotionEffect... eff) {
    	this.amount = amt;
    	this.saturation = sat;
    	this.duration = dur;
    	this.alwaysEdible = alway;
    	this.isDrink = drink;
    	this.effects = eff;
    }
	
	@Override
    public ItemStack onUseFinish(ItemStack st, World w, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer)elb;
            pl.getFoodStats().addStats(getAmt(st), getSatMod(st));
            if (!isDrink)
            	w.playSound(null, pl.posX, pl.posY, pl.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, w.rand.nextFloat() * 0.1F + 0.9F);
            onEaten(st, w, pl);
            if (pl instanceof EntityPlayerMP) CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)pl, st);
        }
        st.shrink(1);
        return st;
    }

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (pl.canEat(canAlwaysEat())) {
			pl.setActiveHand(h);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, st);
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, st);
		}
	}
	
	@Override
    public EnumAction useAction(ItemStack st) {
        return isDrink ? EnumAction.DRINK : EnumAction.EAT;
    }
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return duration;
	}

	protected boolean canAlwaysEat() {
		return this.alwaysEdible;
	}

	protected int getAmt(ItemStack st) {
		return this.amount;
	}

	protected float getSatMod(ItemStack st) {
		return this.saturation;
	}
	
	protected void onEaten(ItemStack st, World w, EntityPlayer pl) {
		for (PotionEffect pe : effects)
			pl.addPotionEffect(new PotionEffect(pe.getPotion(), pe.getDuration(), pe.getAmplifier()));
	}
}
