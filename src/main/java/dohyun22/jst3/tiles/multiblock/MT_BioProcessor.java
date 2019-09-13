package dohyun22.jst3.tiles.multiblock;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIMulti;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_BioProcessor extends MT_Multiblock {

	public MT_BioProcessor() {
		circuitTier = 2;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_BioProcessor();
	}

	@Override
	protected boolean checkStructure() {
		EnumFacing f = getFacing();
		if (f == null || f.getAxis() == EnumFacing.Axis.Y) return false;
		World w = this.getWorld();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = 0; z <= 2; z++) {
					BlockPos p = getPosFromCoord(x, y, z);
					if (y == -1 || y == 1) {
						if (MetaTileBase.getMTEId(getWorld(), p) != 5003 && !getAndAddPort(p, 61, "csg_r")) return false;
					} else {
						if ((x | y | z) == 0) continue;
						if (x == 0 && y == 0 && z == 1) {
							if (!w.isAirBlock(p)) return false;
						} else if (MetaTileBase.getMTEId(w, p) != 5003 && !getAndAddPort(p, 61, "csg_r")) return false;
					}
				}
			}
		}
		if (energyInput.size() != 1 || itemInput.size() < 1 || itemInput.size() > 2 || fluidInput.size() != 1 || itemOutput.size() != 1 || fluidOutput.size() != 1) return false;
		int v = JSTUtils.getVoltFromTier(circuitTier);
		updateEnergyPort(v, v * 8, false);
		return true;
	}

	@Override
	protected boolean checkCanWork() {
		return checkRecipe(MRecipes.BioRecipes);
	}

	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 0.8F);
	}

	@Override
	protected void finishWork() {
		sendItem();
		sendFluid();
		super.finishWork();
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerMulti(inv, te);
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		byte[][][] data = {
				{{4,4,4},
				{4,4,4},
				{4,4,4}},
				{{4,4,4},
				{4,0,4},
				{4,1,4}},
				{{4,4,4},
				{4,4,4},
				{4,4,4}}
		};
		return new GUIMulti(new ContainerMulti(inv, te), data);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile == null || isClient() || tryUpg(pl, heldItem))
			return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}

	@Override
	protected int getTier() {
		return circuitTier;
	}

	@Override
	protected boolean isPortPowered() {
		return true;
	}

	@Override
	protected int getEnergyUse() {
		return energyUse * JSTUtils.getMultiplier(getTier(), energyUse);
	}
	
	@Override
	protected int getSpeed() {
		return Math.max(1, JSTUtils.getMultiplier(getTier(), energyUse));
	}

	@Override
	public boolean canAcceptEnergy() {
		return false;
	}

	@Override
	public boolean isEnergyInput(EnumFacing side) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("csg_r");
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("bioprocess")};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = getTETex(baseTile.facing == JSTUtils.getFacingFromNum(n) ? "bioprocess" : "csg_r");
		return ret;
	}

	@Override
	public int getData() {
		if (getMode() != 2) return 0;
		return mxprogress <= 0 ? 0 : progress * 100 / mxprogress;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void doDisplay(byte state, int data, FontRenderer fr) {
		if (state == 2)
			fr.drawString(I18n.format("jst.msg.com.progress", data), 14, 84, 0x00FF00);
		else
			super.doDisplay(state, data, fr);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.bioprocess"));
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.com.upg"));
	}
}
