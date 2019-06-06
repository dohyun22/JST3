package dohyun22.jst3.tiles.energy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileUESU extends MetaTileEnergyInput implements IGenericGUIMTE {
	private final int tier;
	private final long maxenergy;
	
	public MetaTileUESU(int tier, long storage) {
		this.tier = tier;
		this.maxenergy = storage;
	}
	
	@Override
	public int getInvSize() {
		return 2;
	}

	@Override
	public boolean canProvideEnergy() {
		return true;
	}
	
	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(this.tier);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileUESU(this.tier, this.maxenergy);
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile != null && !getWorld().isRemote)
			pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = this.getTieredTex(tier);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex(tier >= 4 ? "e3d" : "e1d")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (this.baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				ret[n] = getTETex(tier >=  4 ? "e3d" : "e1d");
			} else {
				ret[n] = this.getTieredTex(tier);
			}
		}
		return ret;
	}
	
	@Override
	public long getMaxEnergy() {
		return this.maxenergy;
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (getWorld().isRemote)
			return;
		if (this.baseTile.energy > 0)
			injectEnergyToSide(this.baseTile.facing);
		if (!this.inv.get(0).isEmpty() && this.baseTile.energy > 0L)
			this.baseTile.energy -= JSTUtils.chargeItem((ItemStack)this.inv.get(0), Math.min(this.baseTile.energy, maxEUTransfer()), this.tier, false, false);
		if (!this.inv.get(1).isEmpty() && this.baseTile.energy < this.maxenergy)
			this.baseTile.energy += JSTUtils.dischargeItem((ItemStack)this.inv.get(1), Math.min(this.maxenergy - this.baseTile.energy, maxEUTransfer()), this.tier, false, false);
	}

	@Override
	public boolean isEnergyInput(EnumFacing side) {
		if (side == null) return true;
		return side != this.baseTile.facing;
	}

	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		if (side == null) return true;
		return !isEnergyInput(side);
	}
	
	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		return (sl == 0 && JSTUtils.chargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) > 0) || (sl == 1 && JSTUtils.dischargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) > 0);
	}
	
	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return (sl == 0 && JSTUtils.chargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) <= 0) || (sl == 1 && JSTUtils.dischargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) <= 0);
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric ret = new ContainerGeneric(inv, te);
		ret.addSlot(new BatterySlot(te, 0, 52, 17, true, false));
		ret.addSlot(new BatterySlot(te, 1, 52, 53, false, true));
		ret.addPlayerSlots(inv);
		return ret;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric ret = new GUIGeneric((ContainerGeneric) getServerGUI(id, inv, te));
		ret.addSlot(52, 17, 2);
		ret.addSlot(52, 53, 2);
		ret.addPwr2(93, 34);
		return ret;
	}
	
	@Override
	public int getPrg() {
		return -1;
	}

	@Override
	public int getMxPrg() {
		return -1;
	}
	
	@Override
	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		return doSetFacing(f, false);
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (this.baseTile == null) return;
		super.onPlaced(p, bs, elb, st);
		this.baseTile.energy = st.hasTagCompound() ? st.getTagCompound().getLong("Energy") : 0L;
		this.baseTile.facing = JSTUtils.getClosestSide(p, elb, st, false);
	}
	
	@Override
	public ArrayList<ItemStack> getDrops() {
		if (this.baseTile == null) return new ArrayList();
	    ItemStack st = new ItemStack(JSTBlocks.blockTile, 1, this.baseTile.getID());
	    if (!this.isClient() && this.baseTile.energy > 0) {
	    	NBTTagCompound nbt = new NBTTagCompound();
	    	nbt.setLong("Energy", this.baseTile.energy);
	    	st.setTagCompound(nbt);
	    }
	    return new ArrayList(Arrays.asList(new ItemStack[] {st}));
	}
	
	@Override
	public void getSubBlocks(int id, NonNullList<ItemStack> list) {
		ItemStack st = new ItemStack(JSTBlocks.blockTile, 1, id);
		list.add(st);
		st = st.copy();
		NBTTagCompound nbt = new NBTTagCompound();
    	nbt.setLong("Energy", maxenergy);
    	st.setTagCompound(nbt);
		list.add(st);
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		long e = st.hasTagCompound() ? st.getTagCompound().getLong("Energy") : 0;
		ls.add(I18n.format("jst.tooltip.energy.eu", e, this.maxenergy));
		BigInteger bi = BigInteger.valueOf(JSTCfg.RFPerEU);
		ls.add(I18n.format("jst.tooltip.energy.rf", BigInteger.valueOf(e).multiply(bi), BigInteger.valueOf(this.maxenergy).multiply(bi)));
		e = JSTUtils.getVoltFromTier(this.tier);
		ls.add(I18n.format("jst.msg.com.out") + " " + e + " EU/t, " + (e * JSTCfg.RFPerEU) + " RF/t");
	}
	
	@Override
	public boolean isEnergyStorage() {
		return true;
	}
	
	@Override
	public boolean showDurability(ItemStack st) {
		return st.getCount() == 1;
	}

	@Override
	public double getDurability(ItemStack st) {
		if (!st.hasTagCompound()) return 1.0D;
		return 1.0D - (double)st.getTagCompound().getLong("Energy") / (double)this.maxenergy;
	}
	
	@Override
	public int getComparatorInput() {
		if (baseTile.energy <= 0 || maxenergy <= 0) return 0;
		return (int) (JSTUtils.safeMultiplyLong(baseTile.energy, 15) / maxenergy);
	}
}
