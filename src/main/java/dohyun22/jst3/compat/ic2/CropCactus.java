package dohyun22.jst3.compat.ic2;

import ic2.api.crops.ICropTile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class CropCactus extends CropJST {
	
	public CropCactus() {
		super("cactus", "Notch", 4, 1, -150, null, null, new ItemStack[] {new ItemStack(Blocks.CACTUS)}, new String[] {"Cactus", "Green", "Desert"}, 2, 1, 2, 3, 3, 2);
	}

	@Override
	public boolean onEntityCollision(ICropTile cr, Entity e) {
		if (cr.getCurrentSize() >= 2 && e instanceof EntityLivingBase)
			e.attackEntityFrom(DamageSource.CACTUS, 1.0F);
		return false;
	}
}
