package dohyun22.jst3.tiles.energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGTI;
import dohyun22.jst3.container.ContainerGTI;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.api.IDCGenerator;
import dohyun22.jst3.tiles.noupdate.MetaTileDCCable;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Grid-Tie inverters are used for lag free solar and wind power plants. */
public class MT_GTI extends MT_Generator {
	public int size;
	public double output;
	private double buffer;
	private byte error = -1;
	/** If true, this inverter will be controlled by master inverter(which is the inverter that updated and checked the DC grid first). */
	private boolean isSlave;
	
	public MT_GTI() {
		super(5, false);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_GTI();
	}
	
	@Override
	public long getMaxEnergy() {
		return 81920;
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
		TextureAtlasSprite t = getTieredTex(2);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("gti_side")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (this.baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				ret[n] = getTETex("gti_side" + (baseTile.isActive() ? "" : "_off"));
			} else {
				ret[n] = getTieredTex(2);
			}
		}
		return ret;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (this.baseTile != null) this.baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}
	
	@Override
	protected void checkCanGenerate() {
		if (this.baseTile.getTimer() % this.getTickRate() == 0) {
			this.error = -1;
			this.size = 0;
			this.output = 0.0;
			checkGens(new ArrayList(), this.getPos());
			this.baseTile.setActive(this.output >= 0.2F);
		}
	}
	
	private long getTickRate() {
		return this.error < 0 ? 20L + size * 2L : 100L;
	}

	private void checkGens(ArrayList<BlockPos> ls, BlockPos pos) {
		ls.add(pos);
		for (EnumFacing f : EnumFacing.VALUES) {
			BlockPos p = pos.offset(f);
			boolean flag = false;
			if (error < 0 && !ls.contains(p)) {
				World w = getWorld();
				TileEntity te = w.getTileEntity(p);
				MetaTileBase mtb = null;
				if (te instanceof IDCGenerator) {
					output += ((IDCGenerator)te).getPower(w, p);
					flag = true;
				} else if (te instanceof TileEntityMeta && ((TileEntityMeta)te).hasValidMTE()) {
					mtb = ((TileEntityMeta)te).mte;
					if (mtb instanceof IDCGenerator) {
						output += ((IDCGenerator)mtb).getPower(w, p);
						flag = true;
					} else if (mtb instanceof MetaTileDCCable) {
						flag = true;
					}
				}
				if (size > 2048) {
					setErrored((byte) 2);
					return;
				}
				if (output > maxEUTransfer()) {
					setErrored((byte) 3);
					return;
				}
				if (!flag && mtb instanceof MT_GTI) {
					setErrored((byte) 1);
					((MT_GTI)mtb).setErrored((byte) 1);
					return;
				}
			}
			if (flag) {
				size++;
				checkGens(ls, p);
			}
		}
	}
	
	private void setErrored(byte err) {
		error = err;
		size = 0;
		output = 0;
	}
	
	public byte getErrorState() {
		return error;
	}
	
	public void setErrorState(byte num) {
		if (num < 0) {
			error = -1;
			return;
		}
		this.error = num;
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile == null || isClient()) return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}
	
	@Override
	public int maxEUTransfer() {
		return 16384;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		this.size = tag.getInteger("sz");
		this.output = tag.getDouble("out");
		this.buffer = tag.getDouble("buff");
		this.error = tag.getByte("err");
		this.isSlave = tag.getBoolean("sla");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("sz", this.size);
		tag.setDouble("out", this.output);
		tag.setDouble("buff", this.buffer);
		tag.setByte("err", this.error);
		if (this.isSlave)
			tag.setBoolean("sla", this.isSlave);
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerGTI(inv, te);
		return null;
	}
	
	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUIGTI(new ContainerGTI(inv, te));
		return null;
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.gti.desc"));
	}
	
	@Override
    protected void doGenerate() {
        long e = (long)this.output;
        this.buffer += this.output - e;
        TileEntityMeta baseTile = this.baseTile;
        baseTile.energy += e;
        e = (long)this.buffer;
        if (e > 0L) {
            this.buffer -= e;
            TileEntityMeta baseTile2 = this.baseTile;
            baseTile2.energy += e;
        }
    }
}
