package dohyun22.jst3.items.behaviours.misc;

import dohyun22.jst3.items.behaviours.ItemBehaviour;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;

public class IB_AlkaliMetal extends ItemBehaviour {
	
	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		World w = ei.getEntityWorld();
		if (w.isRemote) return false;
		if ((ei.isWet() || ei.isBurning()) && w.rand.nextInt(5) == 0) {
			w.createExplosion(ei, ei.posX, ei.posY, ei.posZ, 1.0F, false);
			ei.setDead();
			return true;
		}
		return false;
	}

}
