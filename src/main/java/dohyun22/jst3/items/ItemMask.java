package dohyun22.jst3.items;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.utils.JSTChunkData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

public class ItemMask extends ItemArmor implements ISpecialArmor {

	public ItemMask() {
		super(ArmorMaterial.LEATHER, -1, EntityEquipmentSlot.HEAD);
		setRegistryName(JustServerTweak.MODID, "mask");
		setUnlocalizedName("jst.mask");
		setMaxDamage(200);
		setCreativeTab(JustServerTweak.JSTTab);
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@Override
	public void damageArmor(EntityLivingBase e, ItemStack st, DamageSource src, int dmg, int slot) {}

	@Override
	public ArmorProperties getProperties(EntityLivingBase pl, ItemStack ar, DamageSource src, double dmg, int sl) {
		return new ISpecialArmor.ArmorProperties(0, 0.0D, 0);
	}

	@Override
	public int getArmorDisplay(EntityPlayer pl, ItemStack ar, int sl) {
		return 0;
	}

	@Override
	public String getArmorTexture(ItemStack st, Entity e, EntityEquipmentSlot sl, String ty) {
		return "jst3:textures/armor/mask.png";
	}

	@Override
	public void onArmorTick(World w, EntityPlayer pl, ItemStack st) {
		if (!w.isRemote && pl != null && pl.ticksExisted % 200 == 0) {
			int d = JSTChunkData.getFineDust(w, new ChunkPos(pl.getPosition()));
			if (d >= 80000)
				st.damageItem(MathHelper.clamp(d / 150000, 1, 5), pl);
		}
	}
}
