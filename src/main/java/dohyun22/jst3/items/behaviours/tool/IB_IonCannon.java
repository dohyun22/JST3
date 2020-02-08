package dohyun22.jst3.items.behaviours.tool;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTPotions;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
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
		maxEnergy = 2400000;
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
		if (useEnergy(st, 2000, pl, pl.isCreative())) {
			pl.world.playSound(null, pl.posX, pl.posY, pl.posZ, JSTSounds.SHOCK2, SoundCategory.PLAYERS, 0.8F, 1.0F);
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
					w.playSound(null, p, JSTSounds.SHOCK, SoundCategory.BLOCKS, 1.5F, 1.0F);
					break;
				}
				if (n % 2 == 1) continue;
				List<EntityLivingBase> ls = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(px - sz, py - sz, pz - sz, px + sz, py + sz, pz + sz), pr);
				if (!ls.isEmpty()) {
					Entity r = pl.getRidingEntity();
					if (r instanceof EntityLivingBase) ls.remove((EntityLivingBase)r);
					EntityLivingBase e = ls.get(w.rand.nextInt(ls.size()));
					boolean flag = false;
					ls = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(e.posX - 4.0F, e.posY - 4.0F, e.posZ - 4.0F, e.posX + 4.0F, e.posY + 4.0F, e.posZ + 4.0F), pr);
					for (EntityLivingBase e2 : ls) {
						double dmg = 8 / Math.max(e.getDistanceSq(e2) * 0.25F, 1);
						if (dmg >= 1 && e2 != r && e2.attackEntityFrom(JSTDamageSource.causeEntityDamage("ion", pl), (int)dmg)) {
							if (w.rand.nextInt(4) == 0 && JSTPotions.canEMP(e2) && e2.getActivePotionEffect(JSTPotions.emp) == null)
								e2.addPotionEffect(new PotionEffect(JSTPotions.emp, 20));
							w.playSound(null, e2.getPosition(), JSTSounds.SHOCK, SoundCategory.BLOCKS, 1.5F, 1.0F);
							JSTPacketHandler.playCustomEffect(w, e2.getPosition(), 1, 10);
							flag = true;
						}
					}
					if (flag) break;
				}
			}
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot sl, ItemStack st) {
		if (sl != EntityEquipmentSlot.MAINHAND) return null;
		Multimap<String, AttributeModifier> ret = HashMultimap.create();
        ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DamageUUID, "Weapon modifier", 1.0D, 0));
	    return ret;
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
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		addEnergyTip(st, ls);
	}
}
