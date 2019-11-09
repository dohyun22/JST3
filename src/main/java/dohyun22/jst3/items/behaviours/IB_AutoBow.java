package dohyun22.jst3.items.behaviours;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class IB_AutoBow extends ItemBehaviour {
	
	public IB_AutoBow() {
		maxEnergy = 12000;
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
		return 32;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		addEnergyTip(st, ls);
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, maxEnergy);
		sub.add(st);
	}

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (!w.isRemote && (pl.isCreative() || useEnergy(st, 100, pl, false))) {
			boolean inf = pl.isCreative() || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, st) > 0;
			ItemStack a = findAmmo(pl);
			int c = 20;
			c = ForgeEventFactory.onArrowLoose(st, w, pl, c, !a.isEmpty() || inf);
			if (c >= 0 && (!a.isEmpty() || inf)) {
				if (a.isEmpty()) a = new ItemStack(Items.ARROW);
				float v = c / 20.0F;
				v = (v * v + v * 2.0F) / 3.0F;
				if (v > 1.2F) v = 1.2F;
				if (v >= 0.1D) {
					boolean inf2 = pl.isCreative() || (a.getItem() instanceof ItemArrow && ((ItemArrow)a.getItem()).isInfinite(a, st, pl));
					ItemArrow ia = (ItemArrow)((a.getItem() instanceof ItemArrow) ? a.getItem() : Items.ARROW);
					EntityArrow ea = ia.createArrow(w, a, pl);
					ea.shoot(pl, pl.rotationPitch, pl.rotationYaw, 0.0F, v * 3.0F, 1.0F);
					if (v >= 1.0F) ea.setIsCritical(true);
					int e = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, st);
					if (e > 0) ea.setDamage(ea.getDamage() + e * 0.5D + 0.5D);
					e = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, st);
					if (e > 0) ea.setKnockbackStrength(e);
					if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, st) > 0) ea.setFire(100);
					st.damageItem(1, pl);
					if (inf2 || (pl.isCreative() && (a.getItem() == Items.SPECTRAL_ARROW || a.getItem() == Items.TIPPED_ARROW)))
						ea.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
					w.spawnEntity(ea);
					w.playSound(null, pl.posX, pl.posY, pl.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (w.rand.nextFloat() * 0.4F + 1.2F) + v * 0.5F);
					if (!inf2 && !pl.isCreative()) {
						a.shrink(1);
						if (a.isEmpty()) pl.inventory.deleteStack(a);
					}
				}
			}
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
	public int getEnchantability(ItemStack st) {
		return 4;
	}

	@Override
	public boolean canEnchant(ItemStack st, Enchantment en) {
		return en != Enchantments.UNBREAKING && en.type == EnumEnchantmentType.BOW;
	}

	private static ItemStack findAmmo(EntityPlayer pl) {
		if (pl.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemArrow)
			return pl.getHeldItem(EnumHand.OFF_HAND);
		if (pl.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemArrow)
			return pl.getHeldItem(EnumHand.MAIN_HAND);
		for (int i = 0; i < pl.inventory.getSizeInventory(); i++) {
			ItemStack st = pl.inventory.getStackInSlot(i);
			if (st.getItem() instanceof ItemArrow) return st;
		}
		return ItemStack.EMPTY;
	}
}
