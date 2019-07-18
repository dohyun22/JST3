package dohyun22.jst3.utils;

import dohyun22.jst3.JustServerTweak;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class JSTFluids {
	public static Fluid oil;
	public static Fluid fuel;
	public static Fluid nitrofuel;
	public static Fluid naturalGas;
	public static Fluid lng;
	public static Fluid lpg;
	public static Fluid hydrogen;
	public static Fluid deuterium;
	public static Fluid tritium;
	public static Fluid helium3;
	public static Fluid helium;
	public static Fluid lithium;
	public static Fluid carbon;
	public static Fluid nitrogen;
	public static Fluid oxygen;
	public static Fluid sodium;
	public static Fluid silicon;
	public static Fluid chlorine;
	public static Fluid mercury;
	public static Fluid steam;
	public static Fluid acid;
	public static Fluid air;
	public static Fluid heavyfuel;
	
	public static void preInit() {
		oil = createFluid("oil", 0, 500, 300, 5000, false);
		fuel = createFluid("fuel", 0, 300, 300, 1000, false);
		nitrofuel = createFluid("nitrofuel", 0, 400, 300, 1000, false, EnumRarity.UNCOMMON);
		naturalGas = createFluid("gas.natural", 0, -50, 300, 0, true);
		lng = createFluid("liquid_lng", 0, 50, 250, 100, false);
		lpg = createFluid("liquid_lpg", 0, 50, 250, 100, false);
		hydrogen = createFluid("hydrogen", 0, -300, 300, 0, true);
		deuterium = createFluid("deuterium", 0, -200, 300, 0, true);
		tritium = createFluid("tritium", 0, -180, 300, 0, true, EnumRarity.RARE);
		helium3 = createFluid("helium3", 0, -180, 300, 0, true, EnumRarity.RARE);
		helium = createFluid("helium", 0, -100, 300, 0, true);
		lithium = createFluid("lithium", 0, 534, 454, 2000, false);
		carbon = createFluid("carbon", 4, 2260, 3823, 4000, false);
		nitrogen = createFluid("nitrogen", 0, 0, 300, 0, true);
		oxygen = createFluid("oxygen", 0, 0, 300, 0, true);
		sodium = createFluid("sodium", 0, 971, 371, 2000, false);
		chlorine = createFluid("chlorine", 0, 60, 300, 0, true);
		silicon = createFluid("silicon", 4, 2330, 1687, 4000, false);
		mercury = createFluid("mercury", 0, 13593, 300, 2000, false);
		steam = createFluid("steam", 0, -50, 500, 0, true);
		acid = createFluid("acid", 0, 1200, 300, 1000, false);
		air = createFluid("air", 0, 0, 300, 0, true);
		heavyfuel = createFluid("heavyfuel", 0, 1000, 300, 2000, false);

		FluidRegistry.addBucketForFluid(oil);
		FluidRegistry.addBucketForFluid(fuel);
		FluidRegistry.addBucketForFluid(nitrofuel);
		FluidRegistry.addBucketForFluid(lithium);
		FluidRegistry.addBucketForFluid(carbon);
		FluidRegistry.addBucketForFluid(sodium);
		FluidRegistry.addBucketForFluid(silicon);
		FluidRegistry.addBucketForFluid(mercury);
		FluidRegistry.addBucketForFluid(acid);
		FluidRegistry.addBucketForFluid(heavyfuel);
	}

	public static Fluid createFluid(String name, int lumi, int dens, int temp, int visc, boolean gas) {
		return createFluid(name, lumi, dens, temp, visc, gas, EnumRarity.COMMON);
	}
	
	public static Fluid createFluid(String name, int lumi, int dens, int temp, int visc, boolean gas, EnumRarity rarity) {
		ResourceLocation rl = new ResourceLocation(JustServerTweak.MODID + ":blocks/fluids/" + name);
		Fluid ret = FluidRegistry.getFluid(name);
		if (ret == null) {
			ret = new Fluid(name, rl, rl).setLuminosity(lumi).setDensity(dens).setViscosity(visc).setGaseous(gas).setRarity(rarity).setTemperature(temp);
			FluidRegistry.registerFluid(ret);
		}
		return ret;
	}
}
