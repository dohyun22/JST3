package dohyun22.jst3.tiles.device;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Heater extends MetaTileEnergyInput {
	private static Class smeltery;
	private static Field need;
	private static Field fuel;
	private static Field temp;
	private static Field cfuel;
	
	static {
		try {
			smeltery = Class.forName("slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructureFuelTank");
		} catch (ClassNotFoundException e) {}
		
		if (smeltery != null) {
			try {
				Class clz = Class.forName("slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructure");
				need = ReflectionUtils.getField(clz, "needsFuel");
				fuel = ReflectionUtils.getField(clz, "fuel");
				temp = ReflectionUtils.getField(clz, "temperature");
				cfuel = ReflectionUtils.getField(smeltery, "currentFuel");
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Heater();
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient() || smeltery == null || baseTile.getTimer() % 20 != 0 || baseTile.energy < 600) return;
		for (EnumFacing f : EnumFacing.VALUES) {
			BlockPos p = getPos().offset(f);
			ResourceLocation rl = getWorld().getBlockState(p).getBlock().getRegistryName();
			TileEntity te = getWorld().getTileEntity(p);
			if ("tconstruct:smeltery_controller".equals(rl == null ? null : rl.toString()) && smeltery.isInstance(te)) {
				try {
					if ((boolean) need.get(te) || (int)fuel.get(te) <= 0) {
						fuel.set(te, ((int)fuel.get(te)) + 10);
						temp.set(te, 1500);
						need.set(te, false);
						cfuel.set(te, new FluidStack(FluidRegistry.LAVA, 1));
						baseTile.energy -= 600;
					}
				} catch (Throwable t) {
					baseTile.errorCode = 2;
					t.printStackTrace();
				}
			}
		}
	}

	@Override
	public long getMaxEnergy() {
		return 4096;
	}
	
	@Override
	public int maxEUTransfer() {
		return 128;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.heater"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return this.getSingleTETex("heater");
	}
	
	@Override
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "jst_heater";
	}
}
