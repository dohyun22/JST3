package dohyun22.jst3.items.behaviours.tool;

import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dohyun22.jst3.items.behaviours.IB_Damageable;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IB_Athame extends IB_Damageable {

	public IB_Athame() {
		super(100);
	}

	@Override
	public boolean hitEntity(ItemStack st, EntityLivingBase e1, EntityLivingBase e2) {
		if (e1 == null || e2 == null)
			return false;

		String name = e1.getClass().getName();
		//Checks HitEntity is WWolf and bypasses damage protection code by using setHealth.
		if (name != null && (name.toLowerCase().contains("wolfman") || name.toLowerCase().contains("werewolf"))) {
			float var1 = e1.getHealth() - Math.min(10.0F, (e1.getHealth()));
			e1.setHealth(var1);
			this.doDamage(st, e2);
			if (e2 instanceof EntityPlayer)
				((EntityPlayer)e2).spawnSweepParticles();
			return false;
		}

		boolean boost = e1 instanceof EntityEnderman || e1 instanceof EntityDragon || e1 instanceof EntityShulker;
		if (e2 instanceof EntityPlayer && boost) {
			((EntityPlayer)e2).spawnSweepParticles();
			e1.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)e2), 25.0F);
		}
		this.doDamage(st, e2);
		return false;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack st, World w, IBlockState bs, BlockPos p, EntityLivingBase el) {
		if ((double) bs.getBlockHardness(w, p) != 0.0D) {
			this.doDamage(st, 2, el);
		}
		return true;
	}
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot sl, ItemStack st) {
		if (sl != EntityEquipmentSlot.MAINHAND) return null;
		Multimap<String, AttributeModifier> ret = HashMultimap.create();
        ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DamageUUID, "Weapon modifier", 2.0D, 0));
        ret.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SpeedUUID, "Weapon modifier", -1.0D, 0));
	    return ret;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
	
	@Override
	public int getEnchantability(ItemStack st) {
		return 24;
	}

	@Override
	public boolean canEnchant(ItemStack st, Enchantment en) {
		return en.type == EnumEnchantmentType.WEAPON;
	}
}
