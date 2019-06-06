package dohyun22.jst3.tiles.energy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.api.IItemJEU;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.tiles.interfaces.IScanSupport;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.ISpecialElectricItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileBESU extends MetaTileEnergyInput implements IGenericGUIMTE, IScanSupport {
	private final byte tier;

	public MetaTileBESU(int t) {
		tier = (byte) t;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileBESU(tier);
	}
	
	@Override
	public long getMaxEnergy() {
		return JSTUtils.getVoltFromTier(tier) * 50L;
	}
	
	@Override
	public boolean canProvideEnergy() {
		return true;
	}
	
	@Override
	public boolean isEnergyInput(EnumFacing side) {
		if (side == null) return true;
		return side != baseTile.facing;
	}

	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		if (side == null) return true;
		return side == baseTile.facing;
	}
	
	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}
	
	@Override
	public int getInvSize() {
		return 16;
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (getWorld().isRemote)
			return;

		if (baseTile.energy >= JSTUtils.getVoltFromTier(tier) * 40L) {
			for (int n = 0; n < getInvSize(); n++)
				baseTile.energy -= JSTUtils.chargeItem(inv.get(n), maxEUTransfer(), tier, false, false);
		} else if (baseTile.energy < JSTUtils.getVoltFromTier(tier) * 10L) {
			for (int n = 0; n < getInvSize(); n++)
				baseTile.energy += JSTUtils.dischargeItem(inv.get(n), maxEUTransfer(), tier, false, false);
		}

		if (baseTile.energy > 0)
			injectEnergyToSide(baseTile.facing);
	}
	
	@Override
	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		return doSetFacing(f, false);
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile == null || getWorld().isRemote) return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile == null) return;
		super.onPlaced(p, bs, elb, st);
		baseTile.facing = JSTUtils.getClosestSide(p, elb, st, false);
	}

	@Override
	public void getInfo(Map<String, Object[]> map) {
		map.put("jst.msg.scan.batbuff", new Object[0]);
		BigInteger[] bi = getCapacity();
		map.put("jst.tooltip.energy.eu", new Object[] {bi[0], bi[1]});
		BigInteger eu_rf = BigInteger.valueOf(JSTCfg.RFPerEU);
		bi[0] = bi[0].multiply(eu_rf);
		bi[1] = bi[1].multiply(eu_rf);
		map.put("jst.tooltip.energy.rf", new Object[] {bi[0], bi[1]});
	}
	
	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return super.isItemValidForSlot(sl, st) && inv.get(sl).isEmpty() && (JSTUtils.chargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) > 0 || JSTUtils.dischargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) > 0);
	}
	
	@Override
	public boolean isEnergyStorage() {
		return true;
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
	public int getComparatorInput() {
		BigInteger[] bi = getCapacity();
		if (bi[0].compareTo(BigInteger.ZERO) <= 0 || bi[1].compareTo(BigInteger.ZERO) <= 0) return 0;
		return bi[0].multiply(BigInteger.valueOf(15)).divide(bi[1]).intValue();
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric ret = new ContainerGeneric(inv, te);
		for (int y = 0; y < 4; ++y) for (int x = 0; x < 4; ++x) ret.addSlot(new Slot(te, x + y * 4, 53 + x * 18, 8 + y * 18));
		ret.addPlayerSlots(inv);
		return ret;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric ret = new GUIGeneric((ContainerGeneric) getServerGUI(id, inv, te));
		ret.addPwr2(9, 25);
		for (int n = 0; n < getInvSize(); n++) {
			try {
				Slot s = ret.inventorySlots.inventorySlots.get(n);
				ret.addSlot(s.xPos, s.yPos, 2);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.batbuff"));
		int e = JSTUtils.getVoltFromTier(this.tier);
		ls.add(I18n.format("jst.msg.com.out") + " " + e + " EU/t, " + ((long)e * JSTCfg.RFPerEU) + " RF/t");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = this.getTieredTex(tier);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("besu")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = this.baseTile.facing == JSTUtils.getFacingFromNum(n) ? getTETex("besu") : this.getTieredTex(tier);
		return ret;
	}
	
	protected BigInteger[] getCapacity() {
		BigInteger eu = BigInteger.ZERO;
		BigInteger mx = BigInteger.ZERO;
		for (int n = 0; n < getInvSize(); n++) {
			ItemStack st = inv.get(n);
			if (st.isEmpty()) continue;
			Item it = st.getItem();
			try {
				if (it instanceof IItemJEU) {
					eu = eu.add(BigInteger.valueOf(((IItemJEU)it).getEU(st)));
					mx = mx.add(BigInteger.valueOf(((IItemJEU)it).getMaxEU(st)));
				} else if (JSTCfg.ic2Loaded && (it instanceof ISpecialElectricItem || it instanceof IElectricItem || ElectricItem.getBackupManager(st) != null)) {
					eu = eu.add(BigInteger.valueOf((long) ElectricItem.manager.getCharge(st)));
					mx = mx.add(BigInteger.valueOf((long) ElectricItem.manager.getMaxCharge(st)));
				} else if (inv.get(n).hasCapability(CapabilityEnergy.ENERGY, null)) {
					net.minecraftforge.energy.IEnergyStorage es = ((net.minecraftforge.energy.IEnergyStorage)st.getCapability(CapabilityEnergy.ENERGY, null));
					eu = eu.add(BigInteger.valueOf(es.getEnergyStored() / JSTCfg.RFPerEU));
					mx = mx.add(BigInteger.valueOf(es.getMaxEnergyStored() / JSTCfg.RFPerEU));
				}
			} catch (Throwable t) {}
		}
		return new BigInteger[] {eu, mx};
	}
}
