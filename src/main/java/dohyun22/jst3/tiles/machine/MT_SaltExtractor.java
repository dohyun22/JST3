package dohyun22.jst3.tiles.machine;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_SaltExtractor extends MT_Machine {
	private boolean isOceanBiome;

	public MT_SaltExtractor(int t) {
		super(t);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_SaltExtractor(tier);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite ws = getTETex("watergen_side");
		return new TextureAtlasSprite[] {getTieredTex(tier), getTETex("vent"), ws, ws, ws, ws};
	}

	@Override
	protected boolean checkCanWork() {
		if (check()) {
			ItemStack st = new ItemStack(JSTItems.item1, 1, 107);
			ItemStack st2 = inv.get(0);
			if (st2.isEmpty() || (st2.isItemEqual(st) && st2.getCount() + st.getCount() <= st2.getMaxStackSize())) {
				energyUse = 10;
				mxprogress = isOceanBiome ? 200 : 1000;
				return true;
			}
		}
		return false;
	}

	@Override
	protected void finishWork() {
		ItemStack st = new ItemStack(JSTItems.item1, 1, 107);
		ItemStack st2 = inv.get(0);
		if (st2.isEmpty()) {
			inv.set(0, st);
		} else if (st2.isItemEqual(st) && st2.getCount() + st.getCount() <= st2.getMaxStackSize()) {
			st2.grow(st.getCount());
		}
	}
	
	@Override
	public int getInvSize() {
		return 1;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		onBlockUpdate();
	}
	
	@Override
	public void onBlockUpdate() {
		if (isClient()) return;
		isOceanBiome = JSTUtils.hasType(getWorld().getBiome(getPos()), BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.BEACH);
	}
	
	private boolean check() {
		int numWater = 0;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if ((x | y | z) == 0) continue;
					Block b = getWorld().getBlockState(getPos().add(x, y, z)).getBlock();
					if (b == Blocks.WATER || b == Blocks.FLOWING_WATER)
						numWater++;
					if (numWater >= 6)
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		isOceanBiome = tag.getBoolean("Ocean");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("Ocean", isOceanBiome);
	}
	
	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new JSTSlot(te, 0, 100, 35, false, true, 64, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(100, 35, 0);
		gg.addPrg(56, 35);
		gg.addPwr(12, 31);
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hX, float hY, float hZ) {
		if (baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient() && baseTile.isActive()) {
			World w = getWorld();
			if (w.rand.nextInt(8) == 0) {
				for (int i = 0; i < 3; i++) {
					BlockPos p = getPos();
					double x = p.getX() + 0.5D + w.rand.nextFloat() * 0.6D - 0.3D;
					double y = p.getY() + 1.1D + w.rand.nextFloat() * 0.2D;
					double z = p.getZ() + 0.5D + w.rand.nextFloat() * 0.6D - 0.3D;
	                w.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x, y, z, w.rand.nextGaussian() * 0.02D, w.rand.nextGaussian() * 0.02D, w.rand.nextGaussian() * 0.02D);
				}
			}
		}
	}
	
	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 0.5F);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.saltext"));
	}
}
