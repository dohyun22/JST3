package dohyun22.jst3.tiles.earlytech;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.client.gui.GUIAlloyFurnace;
import dohyun22.jst3.container.ContainerAlloyFurnace;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileAlloyFurnace extends MetaTileBase {
    private static final int[] SLOTS_TOP = new int[] {0, 1};
    private static final int[] SLOTS_BOTTOM = new int[] {3};
    private static final int[] SLOTS_SIDES = new int[] {3, 2};
	
    public int burnTime;
    public int currentBurnTime;
    public int cookTime;
    public int totalCookTime;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileAlloyFurnace();
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (this.baseTile != null) this.baseTile.facing = JSTUtils.getClosestSide(p, elb, st, true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return new TextureAtlasSprite[] {getTex("minecraft:blocks/furnace_top"), getTex("minecraft:blocks/furnace_top"), getTex("minecraft:blocks/furnace_side"), getTex("minecraft:blocks/furnace_side"), getTex("minecraft:blocks/furnace_side"), getTETex("alloy")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (this.baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				ret[n] = getTETex("alloy" + (this.baseTile.isActive() ? "" : "_off"));
			} else if (n == 0 || n == 1) {
				ret[n] = getTex("minecraft:blocks/furnace_top");
			} else {
				ret[n] = getTex("minecraft:blocks/furnace_side");
			}
		}
		return ret;
	}
	
	@Override
	public void onPostTick() {
        if (!getWorld().isRemote) {
    		boolean burn = burnTime > 0;
            boolean update = false;

            if (burnTime > 0) burnTime--;
        	
            ItemStack st = (ItemStack)inv.get(3);

            if (burnTime > 0 || !st.isEmpty() && !(inv.get(0)).isEmpty() && !(inv.get(1)).isEmpty()) {
                if (burnTime <= 0 && getRecipe() != null) {
                    burnTime = TileEntityFurnace.getItemBurnTime(st);
                    currentBurnTime = burnTime;

                    if (this.burnTime > 0) {
                        update = true;
                        if (!st.isEmpty()) {
                            Item it = st.getItem();
                            st.shrink(1);
                            
                            if (st.isEmpty()) {
                                ItemStack st2 = it.getContainerItem(st);
                                this.inv.set(3, st2);
                            }
                        }
                    }
                }

                if (burnTime > 0 && getRecipe() != null) {
                    cookTime++;

                    if (cookTime >= totalCookTime) {
                        cookTime = 0;
                        totalCookTime = 200;
                        smeltItem();
                        update = true;
                    }
                } else {
                    cookTime = 0;
                }
            } else if (burnTime <= 0 && cookTime > 0) {
                this.cookTime = MathHelper.clamp(cookTime - 2, 0, totalCookTime);
            }

            if (burn != (burnTime > 0)) {
                update = true;
                baseTile.setActive(this.burnTime > 0);
                updateLight();
            }
            
            if (update) this.baseTile.issueUpdate();
        } else if (baseTile.isActive() && getWorld().rand.nextInt(8) == 0) {
            EnumFacing ef = this.baseTile.facing;
            World w = this.baseTile.getWorld();
            Random rd = w.rand;
            
            double x = (double)this.baseTile.getPos().getX() + 0.5D;
			double y = (double)this.baseTile.getPos().getY() + rd.nextDouble() * 6.0D / 16.0D;
            double z = (double)this.baseTile.getPos().getZ() + 0.5D;
            double o = rd.nextDouble() * 0.6D - 0.3D;

            if (rd.nextInt(8) == 0)
            	this.baseTile.getWorld().playSound((double)this.baseTile.getPos().getX() + 0.5D, (double)this.baseTile.getPos().getY(), (double)this.baseTile.getPos().getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

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
    public void readFromNBT(NBTTagCompound nbt) {
        this.burnTime = nbt.getShort("BurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.totalCookTime = nbt.getShort("CookTimeT");
        this.currentBurnTime = nbt.getShort("BurnTimeC");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setShort("BurnTime", (short)this.burnTime);
        nbt.setShort("CookTime", (short)this.cookTime);
        nbt.setShort("CookTimeT", (short)this.totalCookTime);
        nbt.setShort("BurnTimeC", (short)this.currentBurnTime);
    }
    
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? SLOTS_BOTTOM : (side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES);
    }
    
    @Override
    public boolean isItemValidForSlot(int sl, ItemStack st) {
        if (sl == 2) {
        	return false;
        } else if (sl == 0 || sl == 1) {
            return true;
        } else if (sl == 3) {
            ItemStack st2 = (ItemStack)this.inv.get(3);
            return TileEntityFurnace.isItemFuel(st) || SlotFurnaceFuel.isBucket(st) && st2.getItem() != Items.BUCKET;
        }
		return false;
    }
    
    @Override
    public boolean canExtractItem(int sl, ItemStack st, EnumFacing f) {
        if (sl == 3 && f == EnumFacing.DOWN) {
        	return st.getItem() == Items.BUCKET;
        }
        return sl == 2;
    }
    
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerAlloyFurnace(inv, te);
		return null;
	}
	
	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUIAlloyFurnace(new ContainerAlloyFurnace(inv, te));
		return null;
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile == null || getWorld().isRemote)
			return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}
	
	@Override
	public int getInvSize() {
		return 4;
	}
	
	@Override
    public void setInventorySlotContents(int sl, ItemStack st) {
		ItemStack st2 = (ItemStack)this.inv.get(sl);
        boolean flag = !st.isEmpty() && st.isItemEqual(st2) && ItemStack.areItemStackTagsEqual(st, st2);
        this.inv.set(sl, st);

        if (st.getCount() > baseTile.getInventoryStackLimit()) {
            st.setCount(baseTile.getInventoryStackLimit());
        }

        if (sl == 0 && !flag) {
            this.totalCookTime = 200;
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
		RecipeContainer rc = this.getRecipe();
        if (rc != null) {
            ItemStack out = (ItemStack)this.inv.get(2);
            ItemStack ro = rc.getOutputItems()[0];
            
            rc.process(new ItemStack[] {(ItemStack)this.inv.get(0), (ItemStack)this.inv.get(1)}, null, 0, true, false, true);

            if (out.isEmpty()) {
                this.inv.set(2, ro.copy());
            } else if (out.getItem() == ro.getItem()) {
                out.grow(ro.getCount());
            }
        }
	}

	private RecipeContainer getRecipe() {
        if (this.inv.get(0).isEmpty() || this.inv.get(1).isEmpty()) {
            return null;
        } else {
            RecipeContainer rc = MRecipes.getRecipe(MRecipes.AlloyFurnaceRecipes, new ItemStack[] {(ItemStack)this.inv.get(0), (ItemStack)this.inv.get(1)}, null, 0, true, false);
            if (rc == null) {
                return null;
            } else {
            	ItemStack ro = rc.getOutputItems()[0];
                ItemStack out = (ItemStack)this.inv.get(2);
                if (out.isEmpty()) return rc;
                if (!out.isItemEqual(ro)) return null;
                int cnt = out.getCount() + ro.getCount();
                return cnt <= this.baseTile.getInventoryStackLimit() && cnt <= out.getMaxStackSize() ? rc : null;
            }
        }
	}
	
	@Override
	public SoundType getSoundType(Entity e) {
		return SoundType.STONE;
	}
	
	@Override
	public int getLightValue() {
		return baseTile.isActive() ? 13 : 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.add(I18n.format("jst.tooltip.tile.com.ulvonly"));
	}
	
	@Override
	public int getComparatorInput() {
		if (cookTime > 0 && totalCookTime > 0)
			return cookTime * 15 / totalCookTime;
		return 0;
	}
}
