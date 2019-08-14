package dohyun22.jst3.items.behaviours;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class IB_EntityRemover extends ItemBehaviour {

	@Override
	public boolean onLeftClickEntity(ItemStack st, EntityPlayer pl, Entity e) {
		if (e == null || pl == null || pl.world.isRemote || pl instanceof FakePlayer)
			return false;

		if (!JSTUtils.isOPorSP(pl)) {
			TextComponentTranslation tr = new TextComponentTranslation("commands.generic.permission");
			tr.getStyle().setColor(TextFormatting.RED);
			pl.sendMessage(tr);
			return false;
		}

		e.world.playSound(null, e.posX, e.posY, e.posZ, JSTSounds.LEVEL, SoundCategory.MASTER, 1.0F, 1.0F);
		if (e instanceof EntityLivingBase) {
			((EntityLivingBase) e).setHealth(0);
			if (e instanceof EntityPlayer) {
				if (e instanceof EntityPlayerMP)
					((EntityPlayerMP) e).connection.disconnect(new TextComponentString("You have been instant-deleted by Entity Remover!"));
				return true;
			}
		}
		e.attackEntityFrom(JSTDamageSource.DELETE, Float.POSITIVE_INFINITY);
		e.isDead = true;
		return true;
	}
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot sl, ItemStack st) {
		if (sl != EntityEquipmentSlot.MAINHAND) {
			return null;
		}

		Multimap<String, AttributeModifier> ret = HashMultimap.create();
        ret.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SpeedUUID, "Weapon modifier", 1000.0D, 0));
        ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DamageUUID, "Weapon modifier", Double.POSITIVE_INFINITY, 0));
        return ret;
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.entityremover"));
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
}
