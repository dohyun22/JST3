package dohyun22.jst3.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.evhandler.DustHandler;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ic2.api.info.Info;

public class JSTPotions extends Potion {
	private static final String[] validNames = new String[] {"golem", "sentry", "robot", "vehicle", "turret", "machine", "android", "cyborg", "heli", "aircraft", "airplane", "mechanic"};
	private static final ResourceLocation tex = new ResourceLocation(JustServerTweak.MODID, "textures/gui/effects.png");
	public static JSTPotions radioactivity;
	public static JSTPotions finedust;
	public static JSTPotions emp;
	public static Potion radiation;

	public static void init() {
		radioactivity = new JSTPotions("radioactivity", true, 0xFFFF00, 0, 0, 0.25D);
		finedust = new JSTPotions("finedust", true, 0x605028, 1, 0, 0.5D);
		emp = (JSTPotions) new JSTPotions("emp", true, 0xFF0000, 2, 0, 0.5D)
		.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "93EDBC3F-2D42-47BE-BD27-020B6B3F29C3", -1000.0D, 2)
		.registerPotionAttributeModifier(SharedMonsterAttributes.FLYING_SPEED, "D4786DCE-DA71-1EA8-7F12-E728CE812514", -1000.0D, 2)
		.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "D88C95FD-FFC5-4143-9B4B-61A94B156A5A", -1000000.0D, 0)
		.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "1164A748-3DA8-11EA-B77F-2E728CE88125", -1000.0D, 0)
		.registerPotionAttributeModifier(SharedMonsterAttributes.FOLLOW_RANGE, "FDFCC6C5-A6BE-48F1-AF8D-6B7D0F118F7D", -1000.0D, 0);
		radiation = MobEffects.POISON;
		try {
			if (JSTCfg.ic2Loaded)
				radiation = Info.POTION_RADIATION;
		} catch (Throwable t) {}

		registerPotionType(new PotionType(new PotionEffect(JSTPotions.finedust, 2400, 10)), "finedust");
		registerPotionType(new PotionType(new PotionEffect(JSTPotions.finedust, 2400, 20)), "finedust2");
	}

	public JSTPotions(String name, boolean bad, int color, int x, int y, double eff) {
		super(bad, color);
		ForgeRegistries.POTIONS.register(setRegistryName(name));
		setPotionName("jst.potion." + name);
		setIconIndex(x, y);
		setEffectiveness(eff);
	}

	@Override
    public boolean isReady(int d, int a) {
		if (this == finedust) return d % 100 == 0;
		return d % 20 == 0;
    }

	@Override
	public void performEffect(EntityLivingBase elb, int amp) {
		World w = elb.getEntityWorld();
		if (w.isRemote) return;
		Random r = w.rand;
		if (this == radioactivity) {
			int rng = MathHelper.clamp(amp, 3, 10);
			List<EntityLivingBase> el = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(elb.posX - rng, elb.posY - rng, elb.posZ - rng, elb.posX + rng, elb.posY + rng, elb.posZ + rng));
			for (EntityLivingBase e : el) if (e instanceof EntityLivingBase) setRadiation(e, amp * 10, Math.max(amp - 1, 0), false, false);
			if (r.nextInt(4) == 0)
				w.playSound(null, elb.posX, elb.posY, elb.posZ, JSTSounds.GEIGER, SoundCategory.MASTER, 1.0F, 1.0F);
			if (w instanceof WorldServer)
				for (int n = 0; n < 8; n++)
					((WorldServer)w).spawnParticle(EnumParticleTypes.REDSTONE, true, elb.posX + (r.nextFloat() * 4) - 2, elb.posY + (r.nextFloat() * 4) - 2, elb.posZ + (r.nextFloat() * 4) - 2, 0, 0.3D, 1.0D, 0.5D, 1.0D);
		} else if (this == finedust) {
			if (!JSTDamageSource.hasPartialHazmat(EnumHazard.CHEMICAL, elb, EntityEquipmentSlot.HEAD)) {
				if (amp >= 20 || (amp > 0 && elb.getHealth() > Math.max(MathHelper.clamp(1.0F - amp / 20.0F, 0.5F, 0.8F) * elb.getMaxHealth(), 1.0F)))
					elb.attackEntityFrom(JSTDamageSource.DUST, 1.0F);
				if (r.nextInt(Math.max(8, 24 - amp * 2)) == 0 && !(elb instanceof EntityPlayer && ((EntityPlayer)elb).capabilities.isCreativeMode)) {
					Potion eff = MobEffects.SLOWNESS;
					switch (r.nextInt(MathHelper.clamp(8 - amp, 3, 8))) {
					case 1: eff = MobEffects.WEAKNESS; break;
					case 2: eff = MobEffects.MINING_FATIGUE; break;
					case 3: if (amp >= 2) eff = MobEffects.NAUSEA; break;
					case 4: if (amp >= 3) eff = MobEffects.BLINDNESS; break;
					}
					elb.addPotionEffect(new PotionEffect(eff, 250, 0));
				}
			}
			if (amp < 10) {
				int d = JSTChunkData.getFineDust(w, new ChunkPos(MathHelper.floor(elb.posX) >> 4, MathHelper.floor(elb.posZ) >> 4));
				if (d <= 30000) {
					elb.removePotionEffect(finedust);
				} else {
					PotionEffect pe = elb.getActivePotionEffect(finedust);
					if (pe != null) {
						elb.removePotionEffect(finedust);
						elb.addPotionEffect(new PotionEffect(finedust, pe.getDuration(), DustHandler.getEffectLvl(d)));
					}
				}
			}
		} else if (this == emp && !canEMP(elb)) {
			elb.removePotionEffect(emp);
		}
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		if (this == radioactivity || this == finedust || this == emp) return new ArrayList();
		return super.getCurativeItems();
	}

	@Override
	@SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
		return super.getStatusIconIndex();
    }

	@Override
	public boolean shouldRender(PotionEffect effect) {
		return true;
	}

	@Override
	public boolean shouldRenderInvText(PotionEffect effect) {
		return true;
	}

	@Override
	public boolean shouldRenderHUD(PotionEffect effect) {
		return true;
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase elb, AbstractAttributeMap map, int amp) {
		if (this == emp && !canEMP(elb)) return;
		super.applyAttributesModifiersToEntity(elb, map, amp);
	}

	public static boolean canEMP(EntityLivingBase e) {
		if (e == null) return false;
		if (e.hasCapability(CapabilityEnergy.ENERGY, null) && e.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() > 0) return true;
		String s = JSTUtils.getRegName(e, true).toLowerCase();
		for (String reg : validNames)
			if (s.contains(reg))
				return true;
		return false;
	}

	public static void setRadiation(EntityLivingBase el, int du, int pw, boolean ih, boolean add) {
		if (du <= 0 || pw <= 0 || (el instanceof EntityPlayer && ((EntityPlayer)el).capabilities.isCreativeMode))
			return;
		if (JSTCfg.ic2Loaded)
			pw *= 10;
		try {
			if (ih || !JSTDamageSource.hasFullHazmat(EnumHazard.RADIO, el)) {
				PotionEffect eff = el.getActivePotionEffect(radiation);
				if (eff == null)
					el.addPotionEffect(new PotionEffect(radiation, du, pw));
				else if (add)
					el.addPotionEffect(new PotionEffect(radiation, eff.getDuration() + du, pw));
				else if (eff.getDuration() != du)
					el.addPotionEffect(new PotionEffect(radiation, du, pw));
			}
		} catch (Throwable t) {
		}
	}

	public static void registerPotionType(PotionType pt, String n) {
		ForgeRegistries.POTION_TYPES.register(pt.setRegistryName(new ResourceLocation(JustServerTweak.MODID, n)));
	}
}
