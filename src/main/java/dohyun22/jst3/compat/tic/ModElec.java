package dohyun22.jst3.compat.tic;

import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.Category;

/** Brings back EU modifiers from old versions of TiC. 
 * For the balance reasons, tools with this modifier still have 20% chance of being damaged.
 * (Use Nano-Repair modifier to repair tools with energy)
 * */
public class ModElec extends ModifierTrait {
	private final long energy;
	private final int tier;
	private static final int EU_PER_DMG = 100;
	
	public ModElec(long eu, int t, String id) {
		super("jst_el_" + id, 0x20FFFF);
		energy = eu;
		tier = t;
		addAspects(new ModElecAspect(), new ModifierAspect.SingleAspect(this), new ModifierAspect.CategoryAnyAspect(Category.LAUNCHER, Category.AOE, Category.HARVEST, Category.WEAPON));
	}
	
	@Override
	public String getLocalizedName() {
		return JSTUtils.translate("jst.compat.tic.mod.elec.name");
	}
	
	@Override
	public String getLocalizedDesc() {
		return JSTUtils.translate("jst.compat.tic.mod.elec.desc");
	}

	@Override
	public void applyEffect(NBTTagCompound root, NBTTagCompound mod) {
		super.applyEffect(root, mod);
		if (tier > 0 && energy > 0) {
			root.setLong("JST_EU", 0);
			root.setLong("JST_EU_MAX", energy);
			root.setInteger("JST_EU_LVL", tier);
		}
	}
	
	@Override
	public int onToolDamage(ItemStack tool, int dmg, int ndmg, EntityLivingBase e) {
	    if (e.getEntityWorld().isRemote)
	        return 0;
	    
	    NBTTagCompound tag = tool.getTagCompound();
	    if (tag != null) {
	    	long eu = tag.getLong("JST_EU");
	    	if (dmg * EU_PER_DMG * tier <= eu) {
	    		eu -= dmg * EU_PER_DMG * tier;
	    		tag.setLong("JST_EU", eu);
	    		if (e.getEntityWorld().rand.nextInt(10) != 0)
	    			return 0;
	    	}
	    }
		return ndmg;
	}
	
	@Override
	public void miningSpeed(ItemStack st, PlayerEvent.BreakSpeed ev) {
		if (st.hasTagCompound() && st.getTagCompound().getLong("JST_EU") >= EU_PER_DMG * tier)
			ev.setNewSpeed(ev.getNewSpeed() * (1.0F + tier * 0.5F));
	}
	
	@Override
	public float damage(ItemStack st, EntityLivingBase pl, EntityLivingBase tgt, float dmg, float nDmg, boolean crit) {
		if (st.hasTagCompound())
			nDmg *= 1.0F + tier * 0.5F;
	    return super.damage(st, pl, tgt, dmg, nDmg, crit);
	}
	
	public class ModElecAspect extends ModifierAspect {

		@Override
		public boolean canApply(ItemStack st, ItemStack original) throws TinkerGuiException {
			if (!st.hasTagCompound())
				return false;
			if (JSTUtils.getMaxEUInItem(st) > 0)
				throw new TinkerGuiException(JSTUtils.translate("jst.compat.tic.err.alreadyelec"));
			return true;
		}

		@Override
		public void updateNBT(NBTTagCompound root, NBTTagCompound mod) {
		}
	}
}