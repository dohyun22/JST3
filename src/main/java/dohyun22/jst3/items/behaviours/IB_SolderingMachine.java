package dohyun22.jst3.items.behaviours;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_SolderingMachine extends ItemBehaviour{
	public IB_SolderingMachine() {
		maxEnergy = 20000;
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
	@Nullable
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		long e = getEnergy(st);
		List<String> ret = new ArrayList();
		ret.add(I18n.format("jst.tooltip.energy.eu", e, this.maxEnergy));
		BigInteger bi = BigInteger.valueOf(JSTCfg.RFPerEU);
		ret.add(I18n.format("jst.tooltip.energy.rf", BigInteger.valueOf(e).multiply(bi), BigInteger.valueOf(this.maxEnergy).multiply(bi)));
		ret.add(I18n.format("jst.tooltip.solderingmachine"));
		return ret;
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, this.maxEnergy);
		sub.add(st);
	}
	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(@Nonnull ItemStack st) {
        return false;
    }
}
