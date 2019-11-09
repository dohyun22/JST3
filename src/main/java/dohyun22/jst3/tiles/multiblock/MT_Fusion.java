package dohyun22.jst3.tiles.multiblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.client.gui.GUIFusion;
import dohyun22.jst3.container.ContainerFusion;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Fusion extends MT_Multiblock {
	public static final long ES = 100000000L;
	private final byte tier;
	public boolean displayRF;
	public boolean neut;
	public int production, boost;
	/** 2D Structure Data of Fusion Reactor.
	 * 0 = EMPTY, 1 = Casing, 2 = IO Port, 3 = Power OUT*/
	private static final byte[][] STRUCTURE = {
			{0, 2, 1, 1, 0, 1, 1, 2, 0}, 
			{2, 1, 1, 0, 0, 0, 1, 1, 2},
			{1, 1, 3, 0, 0, 0, 3, 1, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 1, 3, 0, 0, 0, 3, 1, 1},
			{2, 1, 1, 0, 0, 0, 1, 1, 2},
			{0, 2, 1, 1, 1, 1, 1, 2, 0}
			};

	public MT_Fusion(byte t) {
		if (t <= 0) throw new IllegalArgumentException("Invalid Tier");
		tier = t;
	}

	@Override
	protected boolean checkStructure() {
		EnumFacing f = getFacing();
		if (f == null || f.getAxis() == EnumFacing.Axis.Y) return false;
		World w = getWorld();
		BlockPos p = getPos();
		for (int x = 0; x < 9; x++)
			for (int z = 0; z < 9; z++)
				if (!isValid(STRUCTURE[z][x], w, getPosFromCoord(p, x - 4, 0, z, f.getOpposite()))) return false;

		if (energyOutput.size() <= 0 || fluidOutput.size() != 1 || fluidInput.size() != 2) return false;
		updateEnergyPort(20000000, 20000000, true);
		return true;
	}
	
	private boolean isValid(byte b, World w, BlockPos p) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		switch (b) {
		case 0:
			return true;
		case 1:
			return MetaTileBase.getMTEId(w, p) == 5074 + tier;
		case 2:
			getAndAddPort(p, 48, "fr_par");
			return true;
		case 3:
			getAndAddPort(p, 2, "fr_par");
			return true;
		}
		return false;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Fusion(tier);
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}
	
	@Override
	public long getMaxEnergy() {
		return ES * tier * 2;
	}
	
	@Override
	public int maxEUTransfer() {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerFusion(inv, te);
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new GUIFusion(new ContainerFusion(inv, te));
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hX, float hY, float hZ) {
		if (baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	protected boolean checkCanWork() {
		MT_FluidPort fp1 = getFluidPort(0, false), fp2 = getFluidPort(1, false);
		if (fluidInput.size() != 2 || fp1 == null || fp2 == null)
			return false;
		FluidTank[] tanks = new FluidTank[] {fp1.tank, fp2.tank};
		RecipeContainer rc = MRecipes.getRecipe(MRecipes.FusionRecipes, null, tanks, Integer.MAX_VALUE, false, true);
		if (rc != null) {
			Object[] obj = rc.getObj();
			if (obj == null || obj.length != 3 || !(obj[0] instanceof Integer) || !(obj[1] instanceof Integer) || !(obj[2] instanceof Boolean) || (!baseTile.isActive() && tier < MathHelper.ceil(((Integer)obj[0]).intValue() / (double)ES))) return false;
			if (boost > 0) {
				try {
					int fa = boost + 1;
					FluidStack f1 = rc.getInputFluids()[0], f2 = rc.getInputFluids()[1];
					FluidStack[] mi = new FluidStack[] {JSTUtils.modFStack(f1, f1.amount * fa), JSTUtils.modFStack(f2, f2.amount * fa)};
					f1 = rc.getOutputFluids()[0];
					FluidStack[] mo = new FluidStack[] {JSTUtils.modFStack(f1, f1.amount * fa)};
					int ml = (Integer)obj[1] * fa;
					if (!(Boolean)obj[2]) ml = (int)(ml * 1.2D);
					Object[] obj2 = new Object[] {obj[0], ml, obj[2]};
					obj = obj2;
					RecipeContainer rc2 = RecipeContainer.newContainer(null, mi, null, mo, rc.getEnergyPerTick() * fa, rc.getDuration(), obj2);
					if (rc2.process(null, tanks, Integer.MAX_VALUE, false, true, false))
						rc = rc2;
					else
						return false;
				} catch (Exception e) {}
			}
			if (!baseTile.isActive()) {
				baseTile.energy -= (Integer)obj[0];
				getWorld().playSound((EntityPlayer) null, getPos(), JSTSounds.FORCEFIELD, SoundCategory.BLOCKS, 1.0F, 1.6F);
			}
			production = (Integer)obj[1];
			neut = (Boolean)obj[2];

			if (energyUse == 0 || mxprogress == 0) {
				energyUse = rc.getEnergyPerTick();
				mxprogress = rc.getDuration();
			} else if (energyUse != rc.getEnergyPerTick() || mxprogress != rc.getDuration()) {
				return false;
			}
			rc.process(null, tanks, Integer.MAX_VALUE, false, true, true);
			fluidOut = rc.getOutputFluids();
			return true;
		}
		return false;
	}
	
	@Override
	protected void doWork() {
		super.doWork();
		if (energyOutput.size() <= 0) return;
		for (MT_EnergyPort eop : getEnergyPorts(true)) {
			long pr = production / energyOutput.size();
			if (eop.baseTile.energy <= eop.getMaxEnergy() + pr)
				eop.baseTile.energy += pr;
		}
		if (neut && baseTile.getTimer() % Math.max(4, 400 / Math.max(1, boost + 1)) == 0 && !inv.get(0).isEmpty()) {
			RecipeContainer rc = MRecipes.getRecipe(MRecipes.FusionBreederRecipes, new ItemStack[] {inv.get(0)}, null, 0, false, false);
			if (rc != null) {
				ItemStack[] outputs = rc.getOutputItems();
				if (outputs != null && outputs.length == 1) {
					boolean flag = false;
					ItemStack ro = outputs[0];
					ItemStack out = (ItemStack) inv.get(1);
					if (out.isEmpty()) {
						inv.set(1, ro.copy());
						flag = true;
					} else if (JSTUtils.canCombine(out, ro)) {
						int cnt = out.getCount() + ro.getCount();
						if (cnt <= baseTile.getInventoryStackLimit() && cnt <= out.getMaxStackSize()) {
							out.setCount(cnt);
							flag = true;
						}
					}
					if (flag)
						rc.process(new ItemStack[] {inv.get(0)}, null, 0, false, false, true);
				}
			}
		}
	}

	@Override
	protected void finishWork() {
		MT_FluidPort p = getFluidPort(0, true);
		if (!fluidOutput.isEmpty() && p != null && fluidOut != null && fluidOut.length == 1)
			p.tank.fillInternal(fluidOut[0], true);
		super.finishWork();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		displayRF = tag.getBoolean("dispRF");
		neut = tag.getBoolean("neut");
		production = tag.getInteger("output");
		boost = tag.getByte("boost");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (displayRF) tag.setBoolean("dispRF", displayRF);
		if (neut) tag.setBoolean("neut", neut);
		if (production > 0) tag.setInteger("output", production);
		if (boost > 0) tag.setByte("boost", (byte)boost);
	}
	
	@Override
	protected void stopWorking(boolean interrupt) {
	    super.stopWorking(interrupt);
	    neut = false;
	    production = 0;
	}
	
	@Override
	public int getInvSize() {
		return 6;
	}
	
	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		return sl == 0;
	}
	
	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return sl == 1;
	}

	@Override
	@Nonnull
	public void getDrops(ArrayList<ItemStack> ls) {
		super.getDrops(ls);
		if (boost > 0) ls.add(new ItemStack(JSTItems.item1, Math.min(boost, 64), 13003));
	}

	@Override
	public boolean tryUpgrade(String id) {
		if (boost < (tier * 8) && id.equals("jst_fusion")) {
			if (boost < 0) boost = 0;
			boost++;
			return true;
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("fr" + tier);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("screen1")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = getTETex(baseTile.facing == JSTUtils.getFacingFromNum(n) ? "screen1" : "fr" + tier);
		return ret;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.fusion"));
	}
	
	public byte getState() {
		return (byte)(baseTile.isActive() ? neut ? 2 : 1 : 0);
	}
}
