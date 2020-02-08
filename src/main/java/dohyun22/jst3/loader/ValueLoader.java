package dohyun22.jst3.loader;

import javax.annotation.Nullable;

import dohyun22.jst3.api.FineDustCapability;
import dohyun22.jst3.api.IDust;
import dohyun22.jst3.evhandler.DustHandler;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.ReflectionUtils;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ValueLoader extends Loadable {
	public static RegistryNamespaced<ResourceLocation, Class<? extends TileEntity>> TEreg;

	@Override
	public void preInit() {
		CapabilityManager.INSTANCE.register(IDust.class, new FineDustCapability.Impl(), FineDustCapability.Default.class);
		try {
			TEreg = (RegistryNamespaced) ReflectionUtils.getFieldObf(TileEntity.class, "field_190562_f", "REGISTRY").get(null);
			if (TEreg == null) throw new RuntimeException();
		} catch (Throwable t) {
			JSTUtils.LOG.error("Can't get TE registry");
			JSTUtils.LOG.catching(t);
		}
	}

	@Override
	public void postInit() {
		if (JSTCfg.fineDust)
			DustHandler.init();
		JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, new ItemStack(Items.CHAINMAIL_HELMET, 1, 32767));
		JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, new ItemStack(Items.CHAINMAIL_CHESTPLATE, 1, 32767));
		JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, new ItemStack(Items.CHAINMAIL_LEGGINGS, 1, 32767));
		JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, new ItemStack(Items.CHAINMAIL_BOOTS, 1, 32767));
		JSTDamageSource.addHazmat(EnumHazard.CHEMICAL, new ItemStack(JSTItems.mask, 1, 32767));
	}

	@Nullable
	public static Class<? extends TileEntity> getTEClass(String id) {
		if (TEreg != null)
			return TEreg.getObject(new ResourceLocation(id));
		return null;
	}
}
