package dohyun22.jst3.tiles.energy;

import java.util.List;
import java.util.Random;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIFurnaceGen;
import dohyun22.jst3.container.ContainerFurnaceGen;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileFurnaceGen extends MetaTileGenerator {
	public double fuelLeft;
	public int fuelValue;
	
	public MetaTileFurnaceGen(int tier) {
		super(tier, true);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileFurnaceGen(tier);
	}

	@Override
	protected void checkCanGenerate() {
		if (baseTile.setActive(fuelLeft > 0.0D))
			updateLight();
	}

	@Override
	protected void doGenerate() {
		double use = Math.min(fuelLeft, JSTUtils.getVoltFromTier(tier) / 2.5D);
		fuelLeft -= use;
		baseTile.energy += use * 2.5D;
	}
	
	@Override
	public int getInvSize() {
		return 2;
	}
	
	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}

	@Override
	public void onPreTick() {
		if (isClient()) return;
		ItemStack in = inv.get(0);
		if (this.fuelLeft <= 0.0D) {
			int fv = getFuelValue(in);
			if (fv > 0) {
				fuelLeft = fv;
				fuelValue = fv;
				Item it = in.getItem();
				in.shrink(1);
				if (in.isEmpty()) {
					ItemStack st2 = it.getContainerItem(in);
					inv.set(0, st2);
				}
			}
		}
	    in = inv.get(1);
	    if (!in.isEmpty() && baseTile.energy > 0L)
	    	baseTile.energy -= JSTUtils.chargeItem(in, Math.min(baseTile.energy, maxEUTransfer()), tier, false, false);
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		
		if (isClient() && baseTile.isActive() && getWorld().rand.nextInt(8) == 0) {
            EnumFacing ef = baseTile.facing;
            World w = baseTile.getWorld();
            Random rd = w.rand;
            
            double x = (double)baseTile.getPos().getX() + 0.5D;
			double y = (double)baseTile.getPos().getY() + rd.nextDouble() * 6.0D / 16.0D;
            double z = (double)baseTile.getPos().getZ() + 0.5D;
            double o = rd.nextDouble() * 0.6D - 0.3D;

            if (rd.nextInt(6) <= this.tier)
            	w.playSound(getPos().getX() + 0.5D, getPos().getY(), getPos().getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

            switch (ef) {
                case WEST:
                    w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - 0.52D, y, z + o, 0.0D, 0.0D, 0.0D, new int[0]);
                    w.spawnParticle(EnumParticleTypes.FLAME, x - 0.52D, y, z + o, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case EAST:
                    w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.52D, y, z + o, 0.0D, 0.0D, 0.0D, new int[0]);
                    w.spawnParticle(EnumParticleTypes.FLAME, x + 0.52D, y, z + o, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case NORTH:
                    w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + o, y, z - 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    w.spawnParticle(EnumParticleTypes.FLAME, x + o, y, z - 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case SOUTH:
                    w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + o, y, z + 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    w.spawnParticle(EnumParticleTypes.FLAME, x + o, y, z + 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                default:
            }
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		fuelLeft = tag.getDouble("fuel");
		fuelValue = tag.getInteger("fuelval");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("fuel", fuelLeft);
		tag.setInteger("fuelval", fuelValue);
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, st, true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return new TextureAtlasSprite[] {getTieredTex(tier), getTieredTex(tier), getTieredTex(tier), getTieredTex(tier), getTieredTex(tier), getTETex("furnacegen")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				ret[n] = getTETex("furnacegen" + (baseTile.isActive() ? "" : "_off"));
			} else {
				ret[n] = getTieredTex(tier);
			}
		}
		return ret;
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (isClient())
			return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
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
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerFurnaceGen(inv, te);
	}
	
	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new GUIFurnaceGen(new ContainerFurnaceGen(inv, te));
	}
	
	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return sl == 0;
	}
	
	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return sl == 0 && getFuelValue(st) > 0;
	}
	
	@Override
	public int getLightValue() {
		return baseTile.isActive() ? 14 : 0;
	}

	@Override
	public int getDust() {
		return 15 * JSTUtils.getVoltFromTier(tier) / 32;
	}
	
	public static int getFuelValue(ItemStack in) {
		if (in == null || in.isEmpty()) return 0;
		FluidStack fs = FluidUtil.getFluidContained(in);
		if (fs != null && fs.getFluid() == FluidRegistry.LAVA) return 0;
		return TileEntityFurnace.getItemBurnTime(in);
	}
}
