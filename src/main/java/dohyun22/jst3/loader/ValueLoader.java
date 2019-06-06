package dohyun22.jst3.loader;

import dohyun22.jst3.api.FineDustCapability;
import dohyun22.jst3.api.IDust;
import dohyun22.jst3.evhandler.DustHandler;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ValueLoader extends Loadable {

	@Override
	public String getRequiredMod() {
		return null;
	}

	@Override
	public void preInit() {
	    CapabilityManager.INSTANCE.register(IDust.class, new FineDustCapability.Impl(), FineDustCapability.Default.class);
	}

	@Override
	public void postInit() {
		if (JSTCfg.fineDust)
			DustHandler.initTEs();

	    JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, new ItemStack(Items.CHAINMAIL_HELMET, 1, 32767));
	    JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, new ItemStack(Items.CHAINMAIL_CHESTPLATE, 1, 32767));
	    JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, new ItemStack(Items.CHAINMAIL_LEGGINGS, 1, 32767));
	    JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, new ItemStack(Items.CHAINMAIL_BOOTS, 1, 32767));
	    JSTDamageSource.addHazmat(EnumHazard.CHEMICAL, new ItemStack(JSTItems.mask, 1, 32767));
	}
}
