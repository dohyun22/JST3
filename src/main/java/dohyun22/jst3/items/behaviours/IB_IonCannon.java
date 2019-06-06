package dohyun22.jst3.items.behaviours;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_IonCannon extends ItemBehaviour {

	public IB_IonCannon() {
		maxEnergy = 1000000;
	}

	@Override
	public int getTier(ItemStack st) {
		return 3;
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
		return 8192L;
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		long eu = this.getEnergy(st);
		if (eu >= 2500) {
			pl.world.playSound(null, pl.posX, pl.posY, pl.posZ, JSTSounds.SHOCK2, SoundCategory.PLAYERS, 0.8F, 1.0F);
			if (!pl.capabilities.isCreativeMode)
				setEnergy(st, eu - 2000);
			Vec3d d = pl.getLookVec();
			double sz = 2.4D;
			MutableBlockPos p = new MutableBlockPos();
			Predicate pr = new Predicate() {@Override public boolean apply(Object in) {return in != pl;}};
			for (int n = 2; n <= 50; n++) {
				double px = pl.posX + d.x * n;
				double py = pl.posY + pl.getEyeHeight() + d.y * n;
				double pz = pl.posZ + d.z * n;
				p.setPos(px, py, pz);
				IBlockState bs = w.getBlockState(p);
				if (bs.getMaterial().blocksMovement()) {
					JSTPacketHandler.playCustomEffect(w, p, 1, -10);
					break;
				}
				if (n % 2 == 1) continue;
				List<EntityLivingBase> ls = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(px - sz, py - sz, pz - sz, px + sz, py + sz, pz + sz), pr);
				if (!ls.isEmpty()) {
					Entity e = ls.get(w.rand.nextInt(ls.size()));
					boolean flag = false;
					ls = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(e.posX - 3.5F, e.posY - 3.5F, e.posZ - 3.5F, e.posX + 3.5F, e.posY + 3.5F, e.posZ + 3.5F), pr);
					for (EntityLivingBase e2 : ls)
						if (e2 instanceof EntityLivingBase && e2 != pl.getRidingEntity() && ((EntityLivingBase)e2).attackEntityFrom(JSTDamageSource.causeEntityDamage("ion", pl), 7)) {
							JSTPacketHandler.playCustomEffect(w, ((EntityLivingBase)e2).getPosition(), 1, -10);
							flag = true;
						}
					if (flag) break;
				}
			}
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, maxEnergy);
		sub.add(st);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		long e = getEnergy(st);
		List<String> ret = new ArrayList();
		ret.add(I18n.format("jst.tooltip.energy.eu", e, maxEnergy));
		ret.add(I18n.format("jst.tooltip.energy.rf", e * JSTCfg.RFPerEU, maxEnergy * JSTCfg.RFPerEU));
		return ret;
	}
}
