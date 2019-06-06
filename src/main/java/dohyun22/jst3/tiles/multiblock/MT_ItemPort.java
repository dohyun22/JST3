package dohyun22.jst3.tiles.multiblock;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIItemPort;
import dohyun22.jst3.container.ContainerItemPort;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IMultiBlockIO;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_ItemPort extends MetaTileBase implements IMultiBlockIO {
	private String displayTexture;
	public final boolean isOutput;
	
	public MT_ItemPort(boolean out) {
		isOutput = out;
	}

	@Override
	public void setTexture(String texName) {
		if (!texName.equals(this.displayTexture)) {
			displayTexture = texName;
			baseTile.issueUpdate();
		}
	}

	@Override
	public boolean isMBOutput() {
		return isOutput;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_ItemPort(isOutput);
	}

	@Override
	public int getInvSize() {
		return 9;
	}
	
	@Override
	public boolean isMultiBlockPart() {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("t1_side");
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex(isOutput ? "st_out" : "st_in")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) ret[n] = getTETex(this.getFacing() == JSTUtils.getFacingFromNum(n) ? isOutput ? "st_out" : "st_in" : this.displayTexture == null ? "t1_side" : this.displayTexture);
		return ret;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerItemPort(inv, te);
		return null;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUIItemPort(new ContainerItemPort(inv, te));
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
	
	@Override
	public void onPostTick() {
		World w = getWorld();
		if (w.isRemote || !this.isOutput || this.baseTile.getTimer() % 20 != 0) return;
		EnumFacing f = this.getFacing();
		BlockPos p = JSTUtils.getOffset(this.getPos(), f, 1);
		TileEntity te = this.getWorld().getTileEntity(p);
		if (te instanceof IInventory) {
			IInventory iinv = ((IInventory) te);
			if (!JSTUtils.checkInventoryFull(iinv, f.getOpposite())) {
				for (int n = 0; n < this.inv.size(); n++) {
					if (!this.getStackInSlot(n).isEmpty()) {
						ItemStack st = JSTUtils.sendStackToInv(iinv, this.decrStackSize(n, 64), f.getOpposite());
						if (st.isEmpty()) {
							iinv.markDirty();
						} else {
							this.setInventorySlotContents(n, st);
						}
						break;
					}
				}
			}
		}
	}
	
	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		return !super.canInsertItem(sl, st, dir) || (this.isOutput && dir == this.getFacing()) ? false : true;
	}
	
	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return !super.canExtractItem(sl, st, dir) || (!this.isOutput && dir == this.getFacing()) ? false : true;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (this.baseTile != null) this.baseTile.facing = JSTUtils.getClosestSide(p, elb, st, false);
	}
	
	@Override
	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		return doSetFacing(f, false);
	}
	
	@Override
	public void readSyncableDataFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("dt"))
			this.displayTexture = tag.getString("dt");
	}

	@Override
	public void writeSyncableDataToNBT(NBTTagCompound tag) {
		if (this.displayTexture != null)
			tag.setString("dt", this.displayTexture);
	}
}
