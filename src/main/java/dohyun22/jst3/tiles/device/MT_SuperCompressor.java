package dohyun22.jst3.tiles.device;

import java.util.ArrayList;
import java.util.Arrays;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.client.gui.GUISuperCompressor;
import dohyun22.jst3.container.ContainerSuperCompressor;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_SuperCompressor extends MetaTileEnergyInput {
	public int comp;
	private byte counter;
	private static final int energyUse = 4;
	public static final int itemNeededPerNeutronium = 48000000;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_SuperCompressor();
	}

	@Override
	public int getInvSize() {
		return 11;
	}
	
    @Override
    public boolean isItemValidForSlot(int sl, ItemStack st) {
    	return sl >= 0 && sl < 10;
    }
    
    @Override
    public boolean canExtractItem(int sl, ItemStack st, EnumFacing f) {
    	return sl == 10;
    }
    
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (this.isClient()) return;
		
		if (counter > 0) counter--;
		
		boolean worked = false;
		for (int n = 0; n < 10; n++) {
			ItemStack st = this.inv.get(n);
			if (st.isEmpty())
				continue;
			int e = st.getCount() * energyUse;
			if (this.baseTile.energy < e)
				continue;
			this.baseTile.energy -= e;
			this.comp += st.getCount();
			this.inv.set(n, ItemStack.EMPTY);
			worked = true;
		}
		
		if (worked && this.counter <= 0) {
			getWorld().playSound(null, getPos(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, 2.5F);
			counter = 20;
		}
		
		if (this.comp >= itemNeededPerNeutronium) {
			if (this.inv.get(10).isEmpty())
				this.inv.set(10, new ItemStack(JSTItems.item1, 1, 24));
			else
				this.inv.get(10).grow(1);
			this.comp -= itemNeededPerNeutronium;
			getWorld().playSound((EntityPlayer)null, getPos(), JSTSounds.SWITCH, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
		
		/*ItemStack st = this.inv.get(0);
		if (!st.isEmpty() && this.inv.get(1).getCount() < this.inv.get(1).getMaxStackSize() && this.baseTile.energy > energyUse) {
			this.baseTile.energy -= energyUse;
			this.items += st.getCount();
			st.setCount(0);
			if (this.counter <= 0) {
				getWorld().playSound((EntityPlayer)null, getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, 2.0F);
				counter = 40;
			}
			if (this.items >= itemNeededPerNeutronium) {
				if (this.inv.get(1).isEmpty())
					this.inv.set(1, new ItemStack(JSTItems.item1, 1, 24));
				else
					this.inv.get(1).grow(1);
				this.items -= itemNeededPerNeutronium;
				getWorld().playSound((EntityPlayer)null, getPos(), JSTSounds.SWITCH, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}*/
	}
	
	@Override
	public long getMaxEnergy() {
		return 81920;
	}
	
	@Override
	public int maxEUTransfer() {
		return 8192;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("comp");
		return new TextureAtlasSprite[] {getTieredTex(5), getTieredTex(5), t, t, t, t};
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.comp = tag.getInteger("comp");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("comp", this.comp);
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (this.baseTile == null) return;
		super.onPlaced(p, bs, elb, st);
		this.comp = st.hasTagCompound() ? st.getTagCompound().getInteger("comp") : 0;
	}
	
	@Override
	public ArrayList<ItemStack> getDrops() {
		if (this.baseTile == null) return new ArrayList();
	    ItemStack st = new ItemStack(JSTBlocks.blockTile, 1, this.baseTile.getID());
	    if (!this.isClient() && this.comp > 0) {
	    	NBTTagCompound nbt = new NBTTagCompound();
	    	nbt.setInteger("comp", this.comp);
	    	st.setTagCompound(nbt);
	    }
	    return new ArrayList(Arrays.asList(new ItemStack[] {st}));
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerSuperCompressor(inv, te);
		return null;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUISuperCompressor(new ContainerSuperCompressor(inv, te));
		return null;
	}
}
