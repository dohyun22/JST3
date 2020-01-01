package dohyun22.jst3.compat.ic2;

import java.util.List;

import ic2.api.crops.ICropTile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;

public class CropFishing extends CropJST {

	public CropFishing() {
		super("fishingbush", "dohyun22", 3, 1, 0, new String[] {"Fishing", "Water", "Food"}, 5, 0, 4, 1, 0, 2);
		fin();
	}

	@Override
	public void tick(ICropTile cr) {
		if (cr.getCurrentSize() < 3) return;
		BlockPos p = cr.getPosition();
		World w = cr.getWorldObj();
		double x = p.getX() + 0.5D, z = p.getZ() + 0.5D;
		if (w.rand.nextInt(3) == 0) {
			List<EntityLivingBase> ls = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x - 1.5D, p.getY(), z - 1.5D, x + 1.5D, p.getY() + 2.0D, z + 1.5D));
			if (ls.isEmpty()) return;
			EntityLivingBase e = ls.get(w.rand.nextInt(ls.size()));
			if (e != null) {
			    if (e instanceof EntityPlayerMP)
			        ((EntityPlayerMP)e).setPositionAndUpdate(x, p.getY() + 0.05D, z);
			    else
			    	e.setPositionAndRotation(x, p.getY() + 0.05D, z, e.rotationYaw, e.rotationPitch);
				w.playSound(null, p, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.BLOCKS, 1.0F, 0.4F / (w.rand.nextFloat() * 0.4F + 0.8F));
			}
		}
		if (w instanceof WorldServer && w.rand.nextInt(16) == 0) {
			((WorldServer)w).spawnParticle(EnumParticleTypes.WATER_SPLASH, x, p.getY() + 0.5F, z, 10 + w.rand.nextInt(5), 0.1D, 0.0D, 0.1D, 0.0D);
			w.playSound(null, p, SoundEvents.ENTITY_BOBBER_SPLASH, SoundCategory.BLOCKS, 1.0F, w.rand.nextFloat() * 0.4F + 0.8F);
		}
	}

	@Override
	public ItemStack getGain(ICropTile cr) {
		World w = cr.getWorldObj();
		if (w instanceof WorldServer) {
			List<ItemStack> ls = w.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(w.rand, new LootContext.Builder((WorldServer)w).build());
			if (!ls.isEmpty()) {
				ItemStack st = ls.get(w.rand.nextInt(ls.size()));
				if (st != null && !st.isEmpty()) return st.copy();
			}
		}
		return null;
	}
}
