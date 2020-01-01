package dohyun22.jst3.items.behaviours.tool;

import java.lang.reflect.Method;
import java.util.Set;

import com.mojang.authlib.GameProfile;

import dohyun22.jst3.items.behaviours.IB_Damageable;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class IB_LeafCutter extends IB_Damageable {
	private static Class forLeaves;
	private static Method getLeafDrop;

	static {
		if (Loader.isModLoaded("forestry")) {
			forLeaves = ReflectionUtils.getClassObj("forestry.arboriculture.blocks.BlockAbstractLeaves");
			getLeafDrop = ReflectionUtils.getMethod(forLeaves, "getLeafDrop", NonNullList.class, World.class, GameProfile.class, BlockPos.class, Float.TYPE, Integer.TYPE);
		}
	}

	public IB_LeafCutter() {
		super(250);
	}

	@Override
	public boolean isGrafter(ItemStack st) {
		return true;
	}

	@Override
	public float getDigSpeed(ItemStack st, IBlockState bs) {
		if (bs == null || st == null || st.isEmpty()) return 1.0F;
		return canHarvestBlock(bs, st) ? 5.0F : 1.0F;
	}

	@Override
	public boolean canHarvestBlock(IBlockState bs, ItemStack st) {
		return bs == null ? true : bs.getMaterial() == Material.LEAVES || "grafter".equals(bs.getBlock().getHarvestTool(bs));
	}

	@Override
	public void addToolClasses(ItemStack st, Set<String> list) {
		list.add("grafter");
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public int getEnchantability(ItemStack st) {
		return 8;
	}

	@Override
	public boolean canEnchant(ItemStack st, Enchantment en) {
		return en.type == EnumEnchantmentType.DIGGER;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack st, World w, IBlockState bs, BlockPos p, EntityLivingBase el) {
		if (bs == null || w.isRemote) return true;
		if (bs.getBlockHardness(w, p) != 0.0F) doDamage(st, 1, el);
		Block b = bs.getBlock();
		int m = b.getMetaFromState(bs);
		try {
			if (forLeaves.isInstance(b)) {
				NonNullList<ItemStack> ls = NonNullList.create();
				getLeafDrop.invoke(b, ls, w, (GameProfile)null, p, 100.0F, 0);
				JSTUtils.dropAll(w, p, ls);
				return false;
			}
		} catch (Throwable t) {t.printStackTrace();}
		ItemStack dr = null;
		if (b instanceof BlockLeaves) {
			Item i = b.getItemDropped(bs, w.rand, 0);
			if (i != null && i != Items.AIR) dr = new ItemStack(i, 1, b.damageDropped(bs));
		}
		if (dr != null) {
			JSTUtils.dropEntityItemInPos(w, p, dr);
			w.playSound(null, p, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 0.75F, 1.0F);
		}
		return dr == null;
	}
}
