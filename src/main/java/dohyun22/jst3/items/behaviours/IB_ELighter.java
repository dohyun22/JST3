package dohyun22.jst3.items.behaviours;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_ELighter extends ItemBehaviour {
	
	public IB_ELighter() {
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
    public EnumActionResult onRightClickBlock(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumHand h, EnumFacing f, float hx, float hy, float hz) {
		if (getEnergy(st) < 100 || f == null) return EnumActionResult.PASS;
        p = p.offset(f);
        if (!pl.canPlayerEdit(p, f, st)) {
            return EnumActionResult.PASS;
        } else {
            if (!w.isRemote && w.isAirBlock(p)) {
        		if (!pl.isCreative())
        			setEnergy(st, getEnergy(st) - 100);
                if (pl instanceof EntityPlayerMP)
                    CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)pl, p, st);
                w.playSound(null, p, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                w.setBlockState(p, Blocks.FIRE.getDefaultState(), 11);
            }
            return EnumActionResult.SUCCESS;
        }
    }
    
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
	
	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot sl, ItemStack st) {
		if (sl != EntityEquipmentSlot.MAINHAND || getEnergy(st) < 100) return null;
		Multimap<String, AttributeModifier> ret = HashMultimap.create();
        ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DamageUUID, "Weapon modifier", 5.0D, 0));
        ret.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SpeedUUID, "Weapon modifier", 10.0D, 0));
	    return ret;
	}
	
	@Override
	public boolean hitEntity(ItemStack st, EntityLivingBase e1, EntityLivingBase e2) {
		if (e1 != null && getEnergy(st) >= 100) {
			if (e2 instanceof EntityPlayer && !((EntityPlayer)e2).isCreative())
				setEnergy(st, getEnergy(st) - 100);
			e1.getEntityWorld().playSound(null, e1.posX, e1.posY, e1.posZ, JSTSounds.SHOCK, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
		return false;
	}
	
	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, this.maxEnergy);
		sub.add(st);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		long e = getEnergy(st);
		List<String> ret = new ArrayList();
		ret.add(I18n.format("jst.tooltip.energy.eu", e, maxEnergy));
		ret.add(I18n.format("jst.tooltip.energy.rf", e * JSTCfg.RFPerEU, maxEnergy * JSTCfg.RFPerEU));
		return ret;
	}
}
