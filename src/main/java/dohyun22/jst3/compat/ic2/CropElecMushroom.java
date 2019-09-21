package dohyun22.jst3.compat.ic2;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.ICropTile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CropElecMushroom extends CropJST {
	
	public CropElecMushroom() {
		super("elecmushroom", "dohyun22", 4, 1, 200, null, null, new ItemStack[] {null, new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(Items.REDSTONE), new ItemStack(JSTItems.item1, 1, 27)}, new String[] {"Mushroom", "Electricity", "High Voltage", "Danger"}, 8, 1, 2, 4, 4, 1);
	}

	@Override
	public void tick(ICropTile cr) {
		World w = cr.getWorldObj();
		
		if (cr.getCurrentSize() == 1) return;
		BlockPos p = cr.getPosition();
		for (EnumFacing f : EnumFacing.VALUES) {
			int n = Math.max(1, cr.getCurrentSize() * 24 + cr.getStatGrowth() * 4) * (cr.getCustomData().hasKey("pwr") ? 8 : 1);
			if (JSTUtils.sendEnergy(w, p.offset(f), f.getOpposite(), n, false) > 0) {
				cr.getCustomData().removeTag("pwr");
				break;
			}
		}
		
		double xc = p.getX() + 0.5D, yc = p.getY() + 0.5D, zc = p.getZ() + 0.5D;
		List<EntityLivingBase> list = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(xc - 1.0D, p.getY(), zc - 1.0D, xc + 1.0D, p.getY() + 1.0D + 1.0D, zc + 1.0D));
		if (list == null || list.isEmpty()) return;
		boolean flag = false;
		if (w.rand.nextInt(20) == 0 && w.canBlockSeeSky(p.up())) {
			EntityLivingBase tgt = list.get(0);
			flag = w.addWeatherEffect(new EntityLightningBolt(w, p.getX(), p.getY(), p.getZ(), false));
			if (canGrow(cr)) cr.setGrowthPoints(cr.getGrowthPoints() + 100);
			cr.getCustomData().setBoolean("pwr", true);
		} else {
			for (EntityLivingBase elb : list)
				if (!JSTDamageSource.hasFullHazmat(EnumHazard.ELECTRIC, elb) && elb.attackEntityFrom(JSTDamageSource.ELECTRIC, MathHelper.clamp(cr.getCurrentSize(), 1.0F, 3.0F)))
					flag = true;
		}
		if (flag) {
			JSTPacketHandler.playCustomEffect(w, p, 1, 0);
			w.playSound(null, p, JSTSounds.SHOCK2, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean onEntityCollision(ICropTile cr, Entity e) {
		return false;
	}
}
