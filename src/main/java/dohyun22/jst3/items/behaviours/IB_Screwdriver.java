package dohyun22.jst3.items.behaviours;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.interfaces.IScrewDriver;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

public class IB_Screwdriver extends IB_Damageable {

	public IB_Screwdriver() {
		super(100);
	}

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer ep, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote) return EnumActionResult.PASS;
		boolean flag = false;
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		TileEntity te = w.getTileEntity(p);
		if (mte instanceof IScrewDriver)
			flag = ((IScrewDriver)mte).onScrewDriver(ep, s, hx, hy, hz);
		else if (te instanceof IScrewDriver)
			flag = ((IScrewDriver)te).onScrewDriver(ep, s, hx, hy, hz);
		if (flag) {
			doDamage(st, ep);
			w.playSound(null, p, JSTSounds.WRENCH, SoundCategory.BLOCKS, 1.0F, 1.5F);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
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
		if (sl != EntityEquipmentSlot.MAINHAND)
			return null;
		Multimap<String, AttributeModifier> ret = HashMultimap.create();
        ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DamageUUID, "Weapon modifier", 2.0D, 0));
        ret.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SpeedUUID, "Weapon modifier", -1.6D, 0));
	    return ret;
	}
	
	@Override
	public boolean hasContainerItem(ItemStack st) {
		if (st.isEmpty()) return false;
		return !getContainerItem(st).isEmpty();
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack st) {
		if (st.isEmpty()) return ItemStack.EMPTY;
		st = st.copy();
		doDamage(st, null);
		return st;
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack st, EntityPlayer pl, Entity e) {
		doDamage(st, 2, pl);
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		List<String> ret = super.getInformation(st, w, adv);
		ret.addAll(JSTUtils.getListFromTranslation("jst.tooltip.screwdriver"));
		return ret;
	}
}
