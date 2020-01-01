package dohyun22.jst3.tiles.device;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_InductiveCharger extends MetaTileEnergyInput {
	private final byte tier;
	
	public MT_InductiveCharger(int type) {
		this.tier = (byte)type;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_InductiveCharger(tier);
	}

	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}
	
	@Override
	public long getMaxEnergy() {
		return maxEUTransfer() * 16;
	}
	
	@Override
	public boolean isEnergyInput(EnumFacing side) {
		return side != EnumFacing.UP;
	}

	@Override
	public List<AxisAlignedBB> getBox() {
		return Arrays.asList(new AxisAlignedBB[] { SLAB_BOTTOM_AABB });
	}
	
	@Override
	public AxisAlignedBB getBoundingBox() {
		return SLAB_BOTTOM_AABB;
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public boolean isSideSolid(EnumFacing s) {
		return s == EnumFacing.DOWN;
	}
	
	@Override
	public boolean isSideOpaque(EnumFacing s) {
		return s == EnumFacing.DOWN;
	}
	
	@Override
	public void onEntityCollided(Entity e) {
		if (isClient() || e == null) return;
		if (e instanceof EntityPlayer) {
			EntityPlayer pl = (EntityPlayer) e;
			ItemStack hs = pl.getHeldItem(EnumHand.MAIN_HAND);
			NonNullList<ItemStack>[] invs = new NonNullList[] {pl.inventory.mainInventory, pl.inventory.armorInventory};
			for (NonNullList<ItemStack> inv : invs)
				for (int n = 0; n < inv.size() && baseTile.energy > 0; n++) {
					ItemStack st2 = (ItemStack) inv.get(n);
					if (st2 == hs)
						continue;
					if (!st2.isEmpty())
						baseTile.energy -= JSTUtils.chargeItem(st2, baseTile.energy, tier, false, false);
				}
		} else if (e instanceof EntityItem) {
			ItemStack st = ((EntityItem)e).getItem();
			if (st != null && !st.isEmpty()) {
				long eu = JSTUtils.chargeItem(st, baseTile.energy, tier, false, false);
				if (eu > 0) {
					baseTile.energy -= eu;
					((EntityItem)e).setNoDespawn();
				}
			}
		} else if (e instanceof IInventory) {
			try {
				for (int n = 0; n < ((IInventory)e).getSizeInventory(); n++)
					baseTile.energy -= JSTUtils.chargeItem(((IInventory)e).getStackInSlot(n), baseTile.energy, tier, false, false);
			} catch (Throwable t) {}
		}
		if (e.hasCapability(CapabilityEnergy.ENERGY, null))
			baseTile.energy -= e.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(JSTUtils.convLongToInt(baseTile.energy * JSTCfg.RFPerEU), false) / JSTCfg.RFPerEU;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelKey() {
		return "jst_idcg" + tier;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<BakedQuad> getModel(boolean isItem) {
		return JSTUtils.makeCubeAABB(getDefaultTexture(), SLAB_BOTTOM_AABB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = getTieredTex(tier);
		return new TextureAtlasSprite[] {s , getTETex("inductor"), s, s, s, s};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		int a = tier * 2 + 1, b = 4 + tier;
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.ichgr"));
	}
}
