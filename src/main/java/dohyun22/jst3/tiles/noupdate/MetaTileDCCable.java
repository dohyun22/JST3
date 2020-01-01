package dohyun22.jst3.tiles.noupdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MetaTileGridTieInverter;
import dohyun22.jst3.api.IDCGenerator;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * DC Cables are used to connect DC Generators to the Grid Tie Inverter(GTI) which converts DC to AC.
 * Actually, GTI is the controller block of the system and generates energy based on DC generator calculation.
 * NOTE: DC generators are NOT tickable. (to reduce lag)
 * */
public class MetaTileDCCable extends MetaTileBase {
	private byte connection;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileDCCable();
	}

	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return MetaTileBase.getSingleTETex("cable_dc");
	}
	
	@Override
	public List<AxisAlignedBB> getBox() {
		float d = 0.25F;
		float s = (1.0F - d) / 2.0F;
		List<AxisAlignedBB> ret = new ArrayList(7);

		ret.add(new AxisAlignedBB(s, s, s, s + d, s + d, s + d));

		for (byte n = 0; n < 6; n++) {
			if ((this.connection & 1 << n) != 0) {
				float xS, yS, zS;
				xS = yS = zS = s;
				float xE, yE, zE;
				xE = yE = zE = s + d;
				switch (n) {
				case 0:
					yS = 0.0F;
					yE = s;
					break;
				case 1:
					yS = s + d;
					yE = 1.0F;
					break;
				case 2:
					zS = 0.0F;
					zE = s;
					break;
				case 3:
					zS = s + d;
					zE = 1.0F;
					break;
				case 4:
					xS = 0.0F;
					xE = s;
					break;
				case 5:
					xS = s + d;
					xE = 1.0F;
					break;
				}
				ret.add(new AxisAlignedBB(xS, yS, zS, xE, yE, zE));
			}
		}
		return ret;
	}
	
	@Nullable
	public AxisAlignedBB getBoundingBox() {
		List<AxisAlignedBB> boxes = getBox();
		if (boxes.isEmpty())
			return null;
		if (boxes.size() == 1)
			return (AxisAlignedBB) boxes.get(0);
		double zS;
		double yS;
		double xS = yS = zS = Double.POSITIVE_INFINITY;
		double zE;
		double yE;
		double xE = yE = zE = Double.NEGATIVE_INFINITY;
		for (AxisAlignedBB ab : boxes) {
			xS = Math.min(xS, ab.minX);
			yS = Math.min(yS, ab.minY);
			zS = Math.min(zS, ab.minZ);
			xE = Math.max(xE, ab.maxX);
			yE = Math.max(yE, ab.maxY);
			zE = Math.max(zE, ab.maxZ);
		}
		return new AxisAlignedBB(xS, yS, zS, xE, yE, zE);
	}
	
	@Override
	public void onBlockUpdate() {
		if (isClient()) return;
		
		byte c = 0;
		byte i = 0;
		for (byte j = 0; i < 6; i++) {
			j = (byte) EnumFacing.VALUES[i].getOpposite().getIndex();
			TileEntity te = getWorld().getTileEntity(getPos().offset(EnumFacing.VALUES[i]));
			if (te instanceof TileEntityMeta && ((TileEntityMeta)te).hasValidMTE()) {
				MetaTileBase mtb = ((TileEntityMeta)te).mte;
				if (mtb instanceof MetaTileDCCable || mtb instanceof IDCGenerator || mtb instanceof MetaTileGridTieInverter) {
					c = ((byte) (c | 1 << i));
				}
			}
		}
		if (connection != c) {
			connection = c;
			doBlockUpdate();
			markDirty();
		}
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		this.onBlockUpdate();
	}
	
	@Override
	public void readSyncableDataFromNBT(NBTTagCompound tag) {
		this.connection = tag.getByte("cdir");
	}

	@Override
	public void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setByte("cdir", this.connection);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public boolean isSideSolid(EnumFacing s) {
		return false;
	}
	
	@Override
	public boolean isSideOpaque(EnumFacing f) {
		return false;
	}
	
	@Override
	@Nullable
	public MapColor getMapColor() {
		return MapColor.RED;
	}
	
	@Override
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "jst_dcc" + this.connection;
	}

	@Override
	@SideOnly(value=Side.CLIENT)
	public List<BakedQuad> getModel(boolean isItem) {
		return makePipeModel(this.getDefaultTexture(), 2, connection, isItem);
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.dccable.desc"));
	}
}
