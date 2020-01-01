package dohyun22.jst3.tiles.energy;

import java.util.ArrayList;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIMeter;
import dohyun22.jst3.container.ContainerMeter;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileEnergyMeter extends MetaTileCable {
	
	public MetaTileEnergyMeter() {
		super(null, -1, (byte)2, (byte)8, 10);
	}

	public long total;
	public long avg20X100;
	public int avgAmp20X100;
	private int amp;
	private int ampSum20;
	private long sum20;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileEnergyMeter();
	}

	@Override
	public void onPostTick() {
		if (isClient()) return;
		if (JSTCfg.ic2Loaded && !checked)
			checkIC2();
		total = JSTUtils.safeAddLong(total, trans);
		sum20 += this.trans;
		ampSum20 += this.amp;
		trans = 0L;
		amp = 0;
		if (this.baseTile.getTimer() % 20 == 0) {
			avg20X100 = JSTUtils.safeMultiplyLong(sum20, 5);
			avgAmp20X100 = ampSum20 * 5;
			sum20 = 0;
			ampSum20 = 0;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		trans = tag.getLong("trans");
		total = tag.getLong("total");
		avg20X100 = tag.getLong("avg");
		sum20 = tag.getLong("sum");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setLong("trans", trans);
		tag.setLong("total", total);
		tag.setLong("avg", avg20X100);
		tag.setLong("sum", sum20);
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public String getModelKey() {
		return "emeter" + JSTUtils.getNumFromFacing(getFacing());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return new TextureAtlasSprite[] {getTieredTex(1), getTieredTex(1), getTieredTex(1), getTieredTex(1), getTieredTex(1), getTETex("meter_side")};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = getFacing() == JSTUtils.getFacingFromNum(n) ? getTETex("meter_side") : getTieredTex(1);
		return ret;
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerMeter(inv, te);
		return null;
	}
	
	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUIMeter(new ContainerMeter(inv, te));
		return null;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.isClient())
			return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		JSTUtils.clearAdvancement(pl, JustServerTweak.MODID, "main/elecmeter");
		return true;
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {}
	
	@Override
	public boolean isSideSolid(EnumFacing s) {
		return true;
	}
	
	@Override
	public SoundType getSoundType(Entity e) {
		return SoundType.METAL;
	}
	
	@Override
	public boolean isOpaque() {
		return true;
	}
	
	@Override
	protected long transferEnergy(EnumFacing dir, long input, long dist, ArrayList<BlockPos> loc, boolean sim) {
		long ret = super.transferEnergy(dir, input, dist, loc, sim);
		if (!sim && input > ret)
			ampSum20++;
		return ret;
	}

	@Override
	public void onPreTick() {}
}
