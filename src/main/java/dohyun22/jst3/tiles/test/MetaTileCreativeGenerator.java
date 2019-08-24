package dohyun22.jst3.tiles.test;

import java.util.List;
import java.util.Random;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileCreativeGenerator extends TestMetaTileEntityGenerator{

	public MetaTileCreativeGenerator(int tier) {
		super(tier);
	}
	
	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileCreativeGenerator(tier);
	}
	

	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}
	@Override
	protected void doGenerate() {
		baseTile.energy += 10;
	}

	@Override
	public void onPreTick() {
		if (isClient()) return;
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, st, true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("basic_side");
		return new TextureAtlasSprite[] {t, t, t, t, t, t};
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
	protected void checkCanGenerate() {
		baseTile.setActive(true);
	}
	
	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return null;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {}
	
}
