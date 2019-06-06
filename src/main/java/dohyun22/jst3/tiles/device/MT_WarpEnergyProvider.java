package dohyun22.jst3.tiles.device;

import java.lang.reflect.Method;
import java.util.List;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_WarpEnergyProvider extends MetaTileEnergyInput {
	private static Class tileWarpCore;
	private static Method getSpaceObject;
	private static Method addFuel;

	static {
		tileWarpCore = ReflectionUtils.getClassObj("zmaster587.advancedRocketry.tile.multiblock.TileWarpCore");
		getSpaceObject = ReflectionUtils.getMethod(tileWarpCore, "getSpaceObject");
		addFuel = ReflectionUtils.getMethod("zmaster587.advancedRocketry.stations.SpaceObject", "addFuel", Integer.TYPE);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_WarpEnergyProvider();
	}

	@Override
	public long getMaxEnergy() {
		return 10000000;
	}

	@Override
	public int maxEUTransfer() {
		return 32768;
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient() || tileWarpCore == null || baseTile.energy < 50000 || baseTile.getTimer() % 400 != 0) return;
		try {
			for (EnumFacing f : EnumFacing.VALUES) {
				TileEntity te = getWorld().getTileEntity(getPos().offset(f));
				if (tileWarpCore.isInstance(te)) {
					Object so = getSpaceObject.invoke(te);
					if (so != null) {
						int n = (int) addFuel.invoke(so, MathHelper.clamp((int)baseTile.energy / 5000, 0, 1000));
						if (n > 0) baseTile.energy -= Math.max(n, 10) * 5000;
					}
					break;
				}
			}
		} catch (Throwable t) {
			JSTUtils.LOG.catching(t);
			baseTile.errorCode = 1;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return getSingleTETex("fr_par");
	}
	
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "jst_warp";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.warpenergy"));
	}
}
