package dohyun22.jst3.compat;

import javax.annotation.Nullable;

import com.google.common.base.Function;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.FineDustCapability;
import dohyun22.jst3.api.IDust;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;
import ic2.api.tile.IEnergyStorage;
import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeConfigProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.event.FMLInterModComms;

@Optional.Interface(iface="com.google.common.base.Function", modid="theoneprobe")
public class CompatTOP extends Loadable implements Function<ITheOneProbe, Void> {

	@Override
	public String getRequiredMod() {
		return "theoneprobe";
	}

	@Override
	@Method(modid = "theoneprobe")
	public void preInit() {
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", getClass().getName());
	}

	@Override
	@Nullable
	@Method(modid = "theoneprobe")
	public Void apply(@Nullable ITheOneProbe in) {
		if (in != null) {
			EUInfoProvider eu = new EUInfoProvider();
		    in.registerProvider(eu);
		    in.registerProbeConfigProvider(eu);
		    in.registerProvider(new GeneralInfoProvider());
			if (JSTCfg.ic2Loaded && !Loader.isModLoaded("topaddons")) in.registerProvider(new IC2CropInfoProvider());
		}
		return null;
	}

	@Optional.InterfaceList({@Optional.Interface(iface="mcjty.theoneprobe.api.IProbeInfoProvider", modid="theoneprobe"), @Optional.Interface(iface="mcjty.theoneprobe.api.IProbeConfigProvider", modid="theoneprobe")})
	public static class EUInfoProvider implements IProbeInfoProvider, IProbeConfigProvider {
		@Override
		public String getID() {
			return JustServerTweak.MODID + ":jeu";
		}

		@Override
		public void addProbeInfo(ProbeMode pm, IProbeInfo pi, EntityPlayer pl, World w, IBlockState bs, IProbeHitData da) {
			TileEntity te = w.getTileEntity(da.getPos());
			long cur = 0;
			long max = 0;
			if (te instanceof TileEntityMeta && ((TileEntityMeta)te).hasValidMTE()) {
				cur = ((TileEntityMeta)te).energy;
				max = ((TileEntityMeta)te).mte.getMaxEnergy();
			} else if (JSTCfg.ic2Loaded) {
				try {
					if (te instanceof IEnergyStorage) {
						cur = ((IEnergyStorage)te).getStored();
						max = ((IEnergyStorage)te).getCapacity();
					}
				} catch (Throwable t) {}
			}
			if (max > 0)
				pi.progress(Math.max(0, cur), max, pi.defaultProgressStyle().suffix(" EU").filledColor(0xFF0064FF).alternateFilledColor(0xFF0050DC).borderColor(0xFF003264).numberFormat(cur >= 100000000 ? NumberFormat.COMPACT : NumberFormat.FULL));
		}

		@Override
	    public void getProbeConfig(IProbeConfig cf, EntityPlayer pl, World w, Entity e, IProbeHitEntityData da) {}

		@Override
		public void getProbeConfig(IProbeConfig cf, EntityPlayer pl, World w, IBlockState bs, IProbeHitData da) {
			TileEntity te = w.getTileEntity(da.getPos());
			if (te instanceof TileEntityMeta && ((TileEntityMeta) te).hasValidMTE())
				cf.setRFMode(0);
		}
	}

	@Optional.Interface(iface="mcjty.theoneprobe.api.IProbeInfoProvider", modid="theoneprobe")
	public static class IC2CropInfoProvider implements IProbeInfoProvider {
		@Override
		public String getID() {
			return JustServerTweak.MODID + ":ic2crop";
		}

		@Override
		public void addProbeInfo(ProbeMode pm, IProbeInfo pi, EntityPlayer pl, World w, IBlockState bs, IProbeHitData da) {
			if (!JSTCfg.ic2Loaded) return;
			TileEntity te = w.getTileEntity(da.getPos());
			try {
				if (te instanceof ICropTile) {
					ICropTile cr = ((ICropTile)te);
					CropCard cc = cr.getCrop();
					if (cc != null) {
						if (cc == Crops.weed) {
							pi.text(I18n.translateToLocal("jst.msg.scan.ic2.crop.weed"));
						} else {
							int sl = cr.getScanLevel();
							if (sl > 0 || pm == ProbeMode.EXTENDED || pm == ProbeMode.DEBUG)
								pi.text(I18n.translateToLocalFormatted("jst.compat.top.crop.name", I18n.translateToLocal(cc.getUnlocalizedName())));
							else
								pi.text(I18n.translateToLocal("jst.compat.top.crop.nameu"));
							if (sl >= 4 || pm == ProbeMode.DEBUG)
								pi.text(I18n.translateToLocalFormatted("jst.compat.top.crop.stats", cr.getStatGrowth(), cr.getStatGain(), cr.getStatResistance()));
							else
								pi.text(I18n.translateToLocal("jst.compat.top.crop.statsu"));
							if (cc.canBeHarvested(cr))
								pi.text(I18n.translateToLocal("jst.compat.top.crop.harvest" + (cc.getOptimalHarvestSize(cr) == cr.getCurrentSize() ? 2 : 1)));
						}
					}
				}
			} catch (Throwable t) {}
		}
	}

	@Optional.Interface(iface="mcjty.theoneprobe.api.IProbeInfoProvider", modid="theoneprobe")
	public static class GeneralInfoProvider implements IProbeInfoProvider {
		@Override
		public String getID() {
			return JustServerTweak.MODID + ":general";
		}

		@Override
		public void addProbeInfo(ProbeMode pm, IProbeInfo pi, EntityPlayer pl, World w, IBlockState bs, IProbeHitData da) {
			TileEntity te = w.getTileEntity(da.getPos());
			if (te != null) {
				if (pm == ProbeMode.EXTENDED) {
					IDust fd = te.getCapability(FineDustCapability.FINEDUST, EnumFacing.UP);
					if (fd != null)
						pi.text(I18n.translateToLocalFormatted("jst.tooltip.tile.com.dust", FineDustCapability.toMicrogram(fd.getDust() * 60)));
				}
				if (pm == ProbeMode.DEBUG) {
					NBTTagCompound tag = new NBTTagCompound();
					te.writeToNBT(tag);
					pi.text("TE ID: " + tag.getString("id"));
					if (te instanceof TileEntityMeta && ((TileEntityMeta)te).errorCode != 0)
						pi.text("MTE Error Code: " + ((TileEntityMeta)te).errorCode);
				}
			}
		}
	}
}
