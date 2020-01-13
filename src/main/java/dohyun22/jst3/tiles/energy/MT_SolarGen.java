package dohyun22.jst3.tiles.energy;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
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

public class MT_SolarGen extends MT_Generator implements IGenericGUIMTE {

	public MT_SolarGen(int tier) {
		super(tier, false);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_SolarGen(tier);
	}
	
	@Override
	public int maxEUTransfer() {
		if (tier < 0) return 1;
		return JSTUtils.getVoltFromTier(tier);
	}

	@Override
	public boolean canProvideEnergy() {
		return true;
	}
	
	@Override
	public boolean isEnergyOutput(EnumFacing f) {
		return f != EnumFacing.UP;
	}
	
	@Override
	protected void checkCanGenerate() {
		if (baseTile.getTimer() % 50 == 0) baseTile.setActive(JSTUtils.checkSun(getWorld(), getPos()));
	}
	
	@Override
	protected long getPower() {
		return maxEUTransfer();
	}
	
	@Override
	public long getMaxEnergy() {
		return maxEUTransfer();
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (!getWorld().isRemote)
			baseTile.setActive(JSTUtils.checkSun(getWorld(), getPos()));
	}

	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (!isClient())
			baseTile.energy -= JSTUtils.chargeItem(inv.get(0), baseTile.energy, Math.max(1, tier), false, false);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!isClient()) pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric r = new ContainerGeneric(te);
		r.addSlot(new BatterySlot(baseTile, 0, 80, 35, true, false));
		r.addPlayerSlots(inv);
		return r;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric r = new GUIGeneric((ContainerGeneric)getServerGUI(id, inv, te));
		r.addSlot(80, 35, 2);
		return r;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = tier < 0 ? getTETex("basic_side") : getTieredTex(tier);
		return new TextureAtlasSprite[] {t , getTETex("basic_solar"), t, t, t, t};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelKey() {
		return "jst_solar" + tier;
	}
}
