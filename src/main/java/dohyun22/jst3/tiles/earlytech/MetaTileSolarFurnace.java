package dohyun22.jst3.tiles.earlytech;

import java.util.Random;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.client.gui.GUISolarFurnace;
import dohyun22.jst3.container.ContainerSolarFurnace;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileSolarFurnace extends MetaTileBase {
    private static final int[] SLOTS_TOP = new int[] {};
    private static final int[] SLOTS_BOTTOM_SIDE = new int[] {0, 1};
    
    public boolean sunVisible;
    public int cookTime;
    public int totalCookTime;
    private final int duration = 200;
    
	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileSolarFurnace();
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (this.baseTile != null) this.baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return new TextureAtlasSprite[] {getTex("minecraft:blocks/furnace_top"), getTETex("solarthermal"), getTex("minecraft:blocks/furnace_side"), getTex("minecraft:blocks/furnace_side"), getTex("minecraft:blocks/furnace_side"), getTETex("sfurnace")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (this.baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				ret[n] = getTETex("sfurnace" + (this.baseTile.isActive() ? "" : "_off"));
			} else if (n == 0) {
				ret[n] = getTex("minecraft:blocks/furnace_top");
			} else if (n == 1) {
				ret[n] = getTETex("solarthermal");
			} else {
				ret[n] = getTex("minecraft:blocks/furnace_side");
			}
		}
		return ret;
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.sunVisible = nbt.getBoolean("sunny");
        this.cookTime = nbt.getShort("CookTime");
        this.totalCookTime = nbt.getShort("CookTimeT");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setBoolean("sunny", this.sunVisible);
        nbt.setShort("CookTime", (short)this.cookTime);
        nbt.setShort("CookTimeT", (short)this.totalCookTime);
    }
    
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.UP ? SLOTS_TOP : SLOTS_BOTTOM_SIDE;
    }
    
    @Override
    public boolean isItemValidForSlot(int sl, ItemStack st) {
		return sl == 0;
    }
    
    @Override
    public boolean canExtractItem(int sl, ItemStack st, EnumFacing f) {
        return sl == 1;
    }
    
	@Override
	public void onPostTick() {
        if (!this.getWorld().isRemote) {
    		boolean burn = this.sunVisible;
            boolean update = false;

            if (this.baseTile.getTimer() % 20 == 0) this.sunVisible = JSTUtils.checkSun(getWorld(), getPos());
            
            if (this.sunVisible || !(this.inv.get(0)).isEmpty() && !(this.inv.get(1)).isEmpty()) {

                if (this.sunVisible && this.canSmelt()) {
                    ++this.cookTime;

                    if (this.cookTime >= this.totalCookTime) {
                        this.cookTime = 0;
                        this.totalCookTime = duration;
                        this.smeltItem();
                        update = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.sunVisible && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
            }

            if (burn != this.sunVisible) {
                update = true;
                this.baseTile.setActive(this.sunVisible);
            }
            
            if (update) this.baseTile.issueUpdate();
        }
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
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerSolarFurnace(inv, te);
		return null;
	}
	
	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUISolarFurnace(new ContainerSolarFurnace(inv, te));
		return null;
	}
    
	@Override
	public int getInvSize() {
		return 2;
	}
	
	@Override
    public void setInventorySlotContents(int sl, ItemStack st) {
		ItemStack st2 = (ItemStack)this.inv.get(sl);
        boolean flag = !st.isEmpty() && st.isItemEqual(st2) && ItemStack.areItemStackTagsEqual(st, st2);
        this.inv.set(sl, st);

        if (st.getCount() > this.baseTile.getInventoryStackLimit())
            st.setCount(this.baseTile.getInventoryStackLimit());

        if (sl == 0 && !flag) {
            this.totalCookTime = duration;
            this.cookTime = 0;
            this.markDirty();
        }
    }
	
	@Override
	@Nullable
	public MapColor getMapColor() {
		return MapColor.GRAY;
	}
	
	private void smeltItem() {
        if (this.canSmelt()) {
            ItemStack in = (ItemStack)this.inv.get(0);
            ItemStack res = FurnaceRecipes.instance().getSmeltingResult(in);
            ItemStack out = (ItemStack)this.inv.get(1);

            if (out.isEmpty()) {
                this.inv.set(1, res.copy());
            } else if (out.getItem() == res.getItem()) {
                out.grow(res.getCount());
            }

            in.shrink(1);
        }
	}

	private boolean canSmelt() {
        if (((ItemStack)this.inv.get(0)).isEmpty()) {
            return false;
        } else {
            ItemStack st = FurnaceRecipes.instance().getSmeltingResult((ItemStack)this.inv.get(0));

            if (st.isEmpty()) {
                return false;
            } else {
                ItemStack out = (ItemStack)this.inv.get(1);
                if (out.isEmpty()) return true;
                if (!out.isItemEqual(st)) return false;
                int result = out.getCount() + st.getCount();
                return result <= this.baseTile.getInventoryStackLimit() && result <= out.getMaxStackSize();
            }
        }
	}
	
	@Override
	public SoundType getSoundType(Entity e) {
		return SoundType.STONE;
	}
}
