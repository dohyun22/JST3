package dohyun22.jst3.tiles.multiblock;

import java.util.Arrays;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.client.gui.GUIFusion;
import dohyun22.jst3.container.ContainerFusion;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
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
	public int production;
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
		this.tier = t;
	}

	@Override
	protected boolean checkStructure() {
		EnumFacing f = this.getFacing();
		if (f == null || f.getAxis() == EnumFacing.Axis.Y) return false;
		World w = this.getWorld();
		BlockPos p = this.getPos();
		for (int x = 0; x < 9; x++)
			for (int z = 0; z < 9; z++)
				if (!isValid(STRUCTURE[z][x], w, getPosFromCoord(p, x - 4, 0, z, f.getOpposite()))) return false;

		if (this.energyOutput.size() <= 0 || this.fluidOutput.size() != 1 || this.fluidInput.size() != 2) return false;
		updateEnergyPort(JSTUtils.getVoltFromTier(7), 20000000, true);
		
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
		if (this.baseTile != null) this.baseTile.facing = JSTUtils.getClosestSide(p, elb, st, true);
	}
	
	@Override
	public long getMaxEnergy() {
		return ES * tier;
	}
	
	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(this.tier + 5) * 2;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerFusion(inv, te);
		return null;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUIFusion(new ContainerFusion(inv, te));
		return null;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile == null || getWorld().isRemote)
			return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}

	@Override
	protected boolean checkCanWork() {
		MT_FluidPort fip1 = this.getFluidPort(0, false);
		MT_FluidPort fip2 = this.getFluidPort(1, false);
		if (this.fluidInput.size() != 2 || fip1 == null || fip2 == null)
			return false;
		RecipeContainer rc = MRecipes.getRecipe(MRecipes.FusionRecipes, null, new FluidTank[] {fip1.tank, fip2.tank}, Integer.MAX_VALUE, false, true);
		if (rc != null) {
			Object[] obj = rc.getObj();
			if (obj == null || obj.length != 3 || !(obj[0] instanceof Integer) || !(obj[1] instanceof Integer) || !(obj[2] instanceof Boolean) || (!this.baseTile.isActive() && this.baseTile.energy < ((Integer)obj[0]).intValue())) return false;
			
			if (!this.baseTile.isActive()) {
				this.baseTile.energy -= ((Integer)obj[0]).intValue();
				getWorld().playSound((EntityPlayer) null, getPos(), JSTSounds.FORCEFIELD, SoundCategory.BLOCKS, 1.0F, 1.6F);
			}
			production = ((Integer)obj[1]).intValue();
			neut = ((Boolean)obj[2]).booleanValue();
			
			if (this.energyUse == 0 || this.mxprogress == 0) {
				this.energyUse = rc.getEnergyPerTick();
				this.mxprogress = rc.getDuration();
			} else if (this.energyUse != rc.getEnergyPerTick() || this.mxprogress != rc.getDuration()) {
				return false;
			}
			rc.process(null, new FluidTank[] {fip1.tank, fip2.tank}, Integer.MAX_VALUE, false, true, true);
			fluidOut = rc.getOutputFluids();
			return true;
		}
		return false;
	}
	
	@Override
	protected void doWork() {
		super.doWork();
		if (this.energyOutput.size() <= 0) return;
		for (MT_EnergyPort eop : getEnergyPorts(true)) {
			long pr = this.production / this.energyOutput.size();
			if (eop.baseTile.energy <= eop.getMaxEnergy() + pr)
				eop.baseTile.energy += pr;
		}
		if (this.neut && this.baseTile.getTimer() % 400 == 0 && !this.inv.get(0).isEmpty()) {
			RecipeContainer rc = MRecipes.getRecipe(MRecipes.FusionBreederRecipes, new ItemStack[] {this.inv.get(0)}, null, 0, false, false);
			if (rc != null) {
				ItemStack[] outputs = rc.getOutputItems();
				if (outputs != null && outputs.length == 1) {
					boolean flag = false;
					ItemStack ro = outputs[0];
					ItemStack out = (ItemStack) this.inv.get(1);
					if (out.isEmpty()) {
						this.inv.set(1, ro.copy());
						flag = true;
					} else if (JSTUtils.canCombine(out, ro)) {
						int cnt = out.getCount() + ro.getCount();
						if (cnt <= this.baseTile.getInventoryStackLimit() && cnt <= out.getMaxStackSize()) {
							out.setCount(cnt);
							flag = true;
						}
					}
					if (flag)
						rc.process(new ItemStack[] {this.inv.get(0)}, null, 0, false, false, true);
				}
			}
		}
	}

	@Override
	protected void finishWork() {
		MT_FluidPort p = getFluidPort(0, true);
		if (!fluidOutput.isEmpty() && p != null && this.fluidOut != null && this.fluidOut.length == 1)
			p.tank.fillInternal(this.fluidOut[0], true);
		super.finishWork();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		displayRF = tag.getBoolean("dispRF");
		neut = tag.getBoolean("neut");
		production = tag.getInteger("output");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (displayRF) tag.setBoolean("dispRF", displayRF);
		if (neut) tag.setBoolean("neut", neut);
		if (production > 0) tag.setInteger("output", production);
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
			ret[n] = getTETex(this.baseTile.facing == JSTUtils.getFacingFromNum(n) ? "screen1" : "fr" + tier);
		return ret;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.fusion"));
	}
	
	public byte getState() {
		return (byte)(this.baseTile.isActive() ? neut ? 2 : 1 : 0);
	}
}
