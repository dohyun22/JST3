package dohyun22.jst3.tiles.multiblock;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.client.gui.GUIMulti;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.noupdate.MetaTileCasing;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_CokeOven extends MT_Multiblock {
	private byte coilTier;

	public MT_CokeOven() {
		circuitTier = 2;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_CokeOven();
	}

	@Override
	protected boolean checkStructure() {
		EnumFacing f = getFacing();
		if (f == null || f.getAxis() == EnumFacing.Axis.Y)
			return false;
		
		for (int x = -1; x <= 1; x++) {
			for (int y = 0; y < 3; y += 2) {
				for (int z = 0; z <= 2; z++) {
					BlockPos p = getRelativePos(x, y, z);
					if (p.equals(getPos())) continue;
					if (MetaTileBase.getMTEId(getWorld(), p) != 5001 && !getAndAddPort(p, 45, "heatres"))
						return false;
				}
			}
		}
		int coilType = -1;
		for (int x = -1; x <= 1; x++) {
			for (int z = 0; z <= 2; z++) {
				BlockPos p = getRelativePos(x, 1, z);
				if (x == 0 && z == 1) {
					if (!getWorld().isAirBlock(p))
						return false;
					continue;
				}
				int n = MetaTileBase.getMTEId(getWorld(), p);
				if ((coilType != -1 && coilType != n) || n < 5067 || n > 5073)
					return false;
				else
					coilType = n;
			}
		}
		
		if (energyInput.size() <= 0 || energyInput.size() > 2 || itemInput.size() <= 0 || itemInput.size() > 2 || itemOutput.size() != 1 || fluidOutput.size() != 1) return false;
		coilTier = (byte) (coilType - 5065);
		int e = JSTUtils.getVoltFromTier(getTier());
		updateEnergyPort(e, e * 8, false);
		for (MT_FluidPort fp : getFluidPorts(true))
			fp.setCapacity(64000);
		return true;
	}

	@Override
	protected boolean checkCanWork() {
		return checkRecipe(MRecipes.CokeOvenRecipes);
	}

	@Override
	protected void finishWork() {
		sendItem();
		sendFluid();
		super.finishWork();
		updateLight();
	}

	@Override
	protected void onStartWork() {
		updateLight();
	}

	@Override
	protected void stopWorking(boolean interrupt) {
		super.stopWorking(interrupt);
		updateLight();
	}

	@Override
	protected void updateClient() {
		if (baseTile.isActive()) {
			World w = getWorld();
			if (w.rand.nextInt(8) == 0) {
				BlockPos p = getRelativePos(0, 2, 1);
				for (int i = 0; i < 8; i++) {
					double x = p.getX() + 0.5D + w.rand.nextFloat() * 0.6D - 0.3D;
					double y = p.getY() + 1 + w.rand.nextFloat() * 0.2D;
					double z = p.getZ() + 0.5D + w.rand.nextFloat() * 0.6D - 0.3D;
					w.spawnParticle(w.rand.nextBoolean() ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerMulti(inv, te);
		return null;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1) {
			byte[][][] data = {
					{{4,4,4},
					{4,4,4},
					{4,1,4}},
					{{3,3,3},
					{3,0,3},
					{3,3,3}},
					{{4,4,4},
					{4,4,4},
					{4,4,4}}
			};
			return new GUIMulti(new ContainerMulti(inv, te), data);
		}
		return null;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hX, float hY, float hZ) {
		if (baseTile != null && !isClient() && !tryUpg(pl, st))
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
		return Math.min(circuitTier, coilTier);
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
	public int getLightValue() {
		return baseTile.isActive() ? 8 : 0;
	}

	@Override
	public int getDust() {
		return 150;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		coilTier = tag.getByte("tcoil");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("tcoil", coilTier);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("heatres");
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("firebox")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = getTETex(baseTile.facing == JSTUtils.getFacingFromNum(n) ? "firebox" + (baseTile.isActive() ? "" : "_off") : "heatres");
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
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.coke"));
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.com.upg2", 4));
	}
}
