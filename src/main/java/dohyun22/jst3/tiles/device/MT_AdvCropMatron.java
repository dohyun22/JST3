package dohyun22.jst3.tiles.device;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.MultiTankHandler;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.FluidItemPredicate;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class MT_AdvCropMatron extends MetaTileEnergyInput implements IGenericGUIMTE {
	private MultiTankHandler tank;
	private static final int RADIUS = 8;
	private int pX = -RADIUS;
	private int pY = -1;
	private int pZ = -RADIUS;
	private int cooldown;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_AdvCropMatron ret = new MT_AdvCropMatron();
		ret.tank = new MultiTankHandler(new MTETank(16000, false, true, ret, 0, false, "water"), new MTETank(16000, false, true, ret, 1, false, "ic2weed_ex"));
		return ret;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		tank.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tank.writeToNBT(tag);
	}
	
	/** 0=Tank1, 1=Tank2, 2&3=Water in/out, 4&5=Weed-EX in/out, 6=Battery, 7~14=Fertilizer */
	@Override
	public int getInvSize() {
		return 15;
	}
	
	@Override
	public int maxEUTransfer() {
		return 512;
	}
	
	@Override
	public long getMaxEnergy() {
		return 100000;
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient()) return; 
		
		injectEnergy(null, JSTUtils.dischargeItem(inv.get(6), Math.min(getMaxEnergy() - baseTile.energy, maxEUTransfer()), 3, false, false), false);
		FluidStack fs = FluidUtil.getFluidContained(inv.get(2));
		if (fs != null && fs.getFluid() == FluidRegistry.WATER)
			JSTUtils.drainFluidItemInv(tank.getTank(0), 1000, baseTile, 2, 3);
		fs = FluidUtil.getFluidContained(inv.get(4));
		if (fs != null && JSTUtils.getRegName(fs).equals("ic2weed_ex"))
			JSTUtils.drainFluidItemInv(tank.getTank(1), 1000, baseTile, 4, 5);
		
		if (baseTile.energy < 200) return;
		if (cooldown > 0) {
			cooldown--;
			return;
		}
		
		pX ++;
		if (pX > RADIUS) {
			pX = -RADIUS;
			pZ ++;
			if (pZ > RADIUS) {
				pZ = -RADIUS;
				pY ++;
				if (pY > 1)
					pY = -1;
			}
		}
	    
		baseTile.energy -= 2;
		BlockPos p = getPos().add(pX, pY, pZ);
		int use = 0;
        if (JSTCfg.ic2Loaded) {
            TileEntity te = getWorld().getTileEntity(p);
        	try {
        		if (te instanceof ICropTile) {
        			ICropTile cr = ((ICropTile)te);
        			if (cr.getCrop() == Crops.weed) {
        				getWorld().playEvent(2001, p, Block.getStateId(Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS)));
        				cr.reset();
        				use += 100;
        			}
        			if (baseTile.energy > 20000 && cr.getCrop() != null && cr.getScanLevel() < 4) {
        				getWorld().playSound(null, p, JSTSounds.SCAN, SoundCategory.BLOCKS, 0.7F, 1.0F);
        				cr.setScanLevel(4);
        				use += 10000;
        			}
        			if (cr.getStorageNutrients() < 100 && useFertilizer()) {
        				cr.setStorageNutrients(cr.getStorageNutrients() + 100);
        				use += 10;
        			}
					if (tank.getTank(0).getFluidAmount() > 0) {
						int amt = cr.getStorageWater();
						if (amt < 200) {
						    fs = tank.getTank(0).drainInternal(200 - cr.getStorageWater(), true);
						    if (fs != null && fs.amount > 0) {
						    	cr.setStorageWater(amt + fs.amount);
						    	use += 10;
						    }
						}
					}
					if (tank.getTank(1).getFluidAmount() > 0) {
						int amt = cr.getStorageWeedEX();
						if (amt < 100) {
						    fs = tank.getTank(1).drainInternal(100 - cr.getStorageWeedEX(), true);
						    if (fs != null && fs.amount > 0) {
						    	cr.setStorageWeedEX(amt + fs.amount);
						    	use += 10;
						    }
						}
					}
        		}
        	} catch (Throwable t) {
        		t.printStackTrace();
        		baseTile.errorCode = 1;
        	}
        }
        
        baseTile.energy = Math.max(0, baseTile.energy - use);
        if (baseTile.energy <= 0) {
        	getWorld().playSound(null, getPos(), JSTSounds.INTERRUPT, SoundCategory.BLOCKS, 1.0F, getWorld().rand.nextFloat() * 0.4F + 0.8F);
        	cooldown = 100;
        }
	}
	
	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		String str = null;
		FluidStack fs = FluidUtil.getFluidContained(st);
		if (fs != null)
			str = fs.getFluid().getName();
		return super.isItemValidForSlot(sl, st) && ((sl == 2 && "water".equals(str)) || (sl == 4 && "ic2weed_ex".equals(str)) || (sl >= 7 && sl <= 14 && MRecipes.isFertilizer(st)));
	}
	
    @Override
    public boolean canExtractItem(int sl, ItemStack st, EnumFacing f) {
    	return sl == 3 || sl == 5;
    }
    
	@Override
	public boolean canSlotDrop(int num) {
		return num != 0 && num != 1;
	}
	
	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) this.tank;
		return null;
	}
	
	private boolean useFertilizer() {
		for (int n = 7; n <= 14; n++) {
			if (MRecipes.isFertilizer(inv.get(n))) {
				inv.get(n).shrink(1);
				return true;
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelKey() {
		return "jst_advmatron";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = getTETex("cmatron");
		return new TextureAtlasSprite[] {getTieredTex(3), getTieredTex(3), s, s, s, s};
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric ret = new ContainerGeneric(te);
		ret.addSlot(JSTSlot.fl(te, 0, 128, 35));
		ret.addSlot(JSTSlot.fl(te, 1, 152, 35));
		ret.addSlot(new JSTSlot(te, 2, 128, 11).setPredicate(new FluidItemPredicate("water")));
		ret.addSlot(JSTSlot.out(te, 3, 128, 59));
		ret.addSlot(new JSTSlot(te, 4, 152, 11).setPredicate(new FluidItemPredicate("ic2weed_ex")));
		ret.addSlot(JSTSlot.out(te, 5, 152, 59));
		ret.addSlot(new BatterySlot(te, 6, 8, 53, false, true));
		Predicate p = new Predicate<ItemStack>() {
			@Override
			public boolean apply(ItemStack in) {
				return MRecipes.isFertilizer(in);
			}
		};
	    for (int r = 0; r < 2; r++)
	    	for (int c = 0; c < 4; c++)
	    		ret.addSlot(new JSTSlot(te, c + r * 4 + 7, 42 + c * 18, 26 + r * 18).setPredicate(p));
		ret.addPlayerSlots(inv);
		return ret;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric ret = new GUIGeneric((ContainerGeneric) getServerGUI(id, inv, te));
		ret.addSlot(128, 35, 3);
		ret.addSlot(152, 35, 3);
		ret.addSlot(128, 11, 0);
		ret.addSlot(128, 59, 0);
		ret.addSlot(152, 11, 0);
		ret.addSlot(152, 59, 0);
		ret.addSlot(8, 53, 2);
		for (int r = 0; r < 2; r++)
	    	for (int c = 0; c < 4; c++)
	    		ret.addSlot(42 + c * 18, 26 + r * 18, 0);
		ret.addPwr(12, 31);
		return ret;
	}
}
