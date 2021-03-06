package dohyun22.jst3.items.behaviours.tool;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_JSTSword extends ItemBehaviour {

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot sl, ItemStack st) {
		if (sl != EntityEquipmentSlot.MAINHAND) return null;
		Multimap<String, AttributeModifier> r = HashMultimap.create();
		r.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DamageUUID, "Weapon modifier", 2019.0D, 0));
		r.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SpeedUUID, "Weapon modifier", -2.4D, 0));
		return r;
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public boolean canDestroyBlockInCreative(World w, BlockPos p, ItemStack st, EntityPlayer pl) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(@Nonnull ItemStack st) {
		return true;
	}
}
