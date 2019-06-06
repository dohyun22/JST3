package dohyun22.jst3.tiles.energy;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUINaquadahGen;
import dohyun22.jst3.container.ContainerNaquadahGen;
import dohyun22.jst3.items.ItemJST1;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileNaquadahGen extends MetaTileGenerator {
	private byte rods;

	public MetaTileNaquadahGen(int tier) {
		super(tier, true);
		if (tier < 4) throw new IllegalArgumentException("Naquadah generator only supports EV+ tiers");
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileNaquadahGen(this.tier);
	}
	
	@Override
	public int getInvSize() {
		return 8;
	}
	
	@Override
	public boolean canProvideEnergy() {
		return true;
	}
	
	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return new TextureAtlasSprite[] {this.getTieredTex(tier), this.getTieredTex(tier), this.getTieredTex(tier), this.getTieredTex(tier), this.getTieredTex(tier), getTETex("nqgen")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (this.baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				ret[n] = getTETex("nqgen" + (this.baseTile.isActive() ? "" : "_off"));
			} else {
				ret[n] = this.getTieredTex(tier);
			}
		}
		return ret;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (this.baseTile != null) this.baseTile.facing = JSTUtils.getClosestSide(p, elb, st, true);
	}
	
	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier) * 2;
	}
	
	@Override
	protected void checkCanGenerate() {
		if (this.baseTile.getTimer() % 20 == 0) {
			boolean flag = false;
			rods = 0;
			for (int n = 0; n < 8; n++) {
				if (!this.inv.get(n).isEmpty() && this.inv.get(n).getItem() == JSTItems.item1 && this.inv.get(n).getCount() == 1 && this.inv.get(n).getMetadata() == 10005) {
					flag = true;
					rods++;
				}
			}
			if (this.baseTile.setActive(flag))
				updateLight();
		}
	}
	
	@Override
	protected void doGenerate() {
		byte num = 0;
		for (int n = 0; n < 8; n++) {
			if (!this.inv.get(n).isEmpty() && this.inv.get(n).getItem() == JSTItems.item1 && this.inv.get(n).getCount() == 1 && this.inv.get(n).getMetadata() == 10005) {
				((ItemJST1)this.inv.get(n).getItem()).getBehaviour(this.inv.get(n).getMetadata()).doDamage(this.inv.get(n), Math.max(JSTUtils.getVoltFromTier(tier) / 2048, 1), null);
				if (this.inv.get(n).isEmpty()) this.inv.set(n, new ItemStack(JSTItems.item1, 1, 39));
				num++;
			}
		}
		this.baseTile.energy += num * JSTUtils.getVoltFromTier(this.tier) / 4;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerNaquadahGen(inv, te);
		return null;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUINaquadahGen(new ContainerNaquadahGen(inv, te));
		return null;
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile == null || getWorld().isRemote) {
			return true;
		}
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}

	public int getOutput() {
		return rods * JSTUtils.getVoltFromTier(tier) / 4;
	}
	
	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return super.isItemValidForSlot(sl, st) && this.inv.get(sl).isEmpty() && !st.isEmpty() && st.getItem() == JSTItems.item1 && st.getMetadata() == 10005;
	}
	
	@Override
	public int getLightValue() {
		return baseTile.isActive() ? 6 : 0;
	}
}
