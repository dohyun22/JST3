package dohyun22.jst3.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.oredict.OreDictionary;

public class JSTDamageSource {
	public static DamageSource ELECTRIC = new DamageSource("electric"),
	DELETE = new DamageSource("delete").setDamageIsAbsolute().setDamageBypassesArmor().setDamageAllowedInCreativeMode(),
	DUST = new DamageSource("finedust").setDamageIsAbsolute().setDamageBypassesArmor(),
	FLAME = new DamageSource("flametrap").setFireDamage(),
	EMP = new DamageSource("emp").setDamageIsAbsolute().setDamageBypassesArmor();
	public static final HashMap<EnumHazard, List<ItemStack>> HAZMATS = new HashMap();

	public static DamageSource causeEntityDamage(String s, @Nullable Entity e) {
		return new EntityDamageSource("jst_" + s, e);
	}
	
	public static enum EnumHazard {
		RADIO, ELECTRIC, CHEMICAL, THERMAL
	}
	
	public static boolean hasFullHazmat(EnumHazard eh, EntityLivingBase elb) {
		Iterable<ItemStack> itr = elb.getArmorInventoryList();
		List<ItemStack> ls = HAZMATS.get(eh);
		if (itr == null || ls == null) return false;
		byte cnt = 0;
		for (ItemStack st : itr) {
			for (ItemStack st2 : ls) {
				if (OreDictionary.itemMatches(st2, st, false)) {
					cnt++;
					break;
				}
			}
		}
		return cnt >= 4;
	}

	public static boolean hasPartialHazmat(EnumHazard eh, EntityLivingBase elb, EntityEquipmentSlot es) {
		ItemStack st = elb.getItemStackFromSlot(es);
		if (!st.isEmpty()) {
			List<ItemStack> ls = HAZMATS.get(eh);
			if (ls != null)
				for (ItemStack st2 : ls)
					if (OreDictionary.itemMatches(st2, st, false))
						return true;
		}
		return false;
	}
	
	public static void addHazmat(EnumHazard eh, ItemStack st) {
		if (eh == null || st == null || st.isEmpty()) return;
		List<ItemStack> ls = HAZMATS.get(eh);
		if (ls == null) {
			ls = new ArrayList<ItemStack>();
			HAZMATS.put(eh, ls);
		}
		ls.add(st);
	}

	public static void addUniversalHazmat(ItemStack st) {
		for (EnumHazard eh : EnumHazard.values()) addHazmat(eh, st);
	}
}
