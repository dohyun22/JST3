package dohyun22.jst3.tiles.earlytech;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
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

public class MetaTileAlloyFurnace extends MetaTileBase implements IGenericGUIMTE {
    private static final int[] SLOTS_TOP = new int[] {0, 1};
    private static final int[] SLOTS_BOTTOM = new int[] {3};
    private static final int[] SLOTS_SIDES = new int[] {3, 2};
    private int burnTime, currentBurnTime, cookTime, totalCookTime;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileAlloyFurnace();
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTex("minecraft:blocks/furnace_top"), s = getTex("minecraft:blocks/furnace_side");
		return new TextureAtlasSprite[] {t, t, s, s, s, getTETex("alloy")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (baseTile.facing == JSTUtils.getFacingFromNum(n))
				ret[n] = getTETex("alloy" + (baseTile.isActive() ? "" : "_off"));
			else if (n == 0 || n == 1)
				ret[n] = getTex("minecraft:blocks/furnace_top");
			else
				ret[n] = getTex("minecraft:blocks/furnace_side");
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

                    if (burnTime > 0) {
                        update = true;
                        if (!st.isEmpty()) {
                            Item it = st.getItem();
                            st.shrink(1);
                            
                            if (st.isEmpty()) {
                                ItemStack st2 = it.getContainerItem(st);
                                inv.set(3, st2);
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
                cookTime = MathHelper.clamp(cookTime - 2, 0, totalCookTime);
            }

            if (burn != (burnTime > 0)) {
                update = true;
                baseTile.setActive(burnTime > 0);
                updateLight();
            }
            
            if (update) baseTile.issueUpdate();
        } else if (baseTile.isActive() && getWorld().rand.nextInt(8) == 0) {
            EnumFacing ef = baseTile.facing;
            World w = baseTile.getWorld();
            Random rd = w.rand;
            
            double x = (double)baseTile.getPos().getX() + 0.5D;
			double y = (double)baseTile.getPos().getY() + rd.nextDouble() * 6.0D / 16.0D;
            double z = (double)baseTile.getPos().getZ() + 0.5D;
            double o = rd.nextDouble() * 0.6D - 0.3D;

            if (rd.nextInt(8) == 0)
            	baseTile.getWorld().playSound((double)baseTile.getPos().getX() + 0.5D, (double)baseTile.getPos().getY(), (double)baseTile.getPos().getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

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
        burnTime = nbt.getShort("BurnTime");
        cookTime = nbt.getShort("CookTime");
        totalCookTime = nbt.getShort("CookTimeT");
        currentBurnTime = nbt.getShort("BurnTimeC");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setShort("BurnTime", (short)burnTime);
        nbt.setShort("CookTime", (short)cookTime);
        nbt.setShort("CookTimeT", (short)totalCookTime);
        nbt.setShort("BurnTimeC", (short)currentBurnTime);
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
            ItemStack st2 = (ItemStack)inv.get(3);
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
		ContainerGeneric r = new ContainerGeneric(te);
		r.addSlot(new Slot(te, 0, 43, 17));
		r.addSlot(new Slot(te, 1, 61, 17));
		r.addSlot(new JSTSlot(te, 2, 116, 35, false, true, 64, true));
		r.addSlot(new SlotFurnaceFuel(te, 3, 52, 53));
		r.addPlayerSlots(inv);
		return r;
	}
	
	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric r = new GUIGeneric((ContainerGeneric) getServerGUI(id, inv, te));
		r.addSlot(43, 17, 0);
		r.addSlot(61, 17, 0);
		r.addSlot(116, 35, 0);
		r.addSlot(52, 53, 9);
		r.addFuel(53, 36);
		r.addPrg(80, 35, JustServerTweak.MODID + ".alloyfurnace", "minecraft.fuel");
		return r;
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile == null || getWorld().isRemote)
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
		ItemStack st2 = (ItemStack)inv.get(sl);
        boolean flag = !st.isEmpty() && st.isItemEqual(st2) && ItemStack.areItemStackTagsEqual(st, st2);
        inv.set(sl, st);

        if (st.getCount() > baseTile.getInventoryStackLimit()) {
            st.setCount(baseTile.getInventoryStackLimit());
        }

        if ((sl == 0 || sl == 1) && !flag) {
            totalCookTime = 200;
            cookTime = 0;
            markDirty();
        }
    }
	
	@Override
	@Nullable
	public MapColor getMapColor() {
		return MapColor.GRAY;
	}
	
	private void smeltItem() {
		RecipeContainer rc = getRecipe();
        if (rc != null) {
            ItemStack out = (ItemStack)inv.get(2);
            ItemStack ro = rc.getOutputItems()[0];
            
            rc.process(new ItemStack[] {(ItemStack)inv.get(0), (ItemStack)inv.get(1)}, null, 0, true, false, true);

            if (out.isEmpty()) {
                inv.set(2, ro.copy());
            } else if (out.getItem() == ro.getItem()) {
                out.grow(ro.getCount());
            }
        }
	}

	private RecipeContainer getRecipe() {
        if (inv.get(0).isEmpty() || inv.get(1).isEmpty()) {
            return null;
        } else {
            RecipeContainer rc = MRecipes.getRecipe(MRecipes.AlloyFurnaceRecipes, new ItemStack[] {(ItemStack)inv.get(0), (ItemStack)inv.get(1)}, null, 0, true, false);
            if (rc == null) {
                return null;
            } else {
            	ItemStack ro = rc.getOutputItems()[0];
                ItemStack out = (ItemStack)inv.get(2);
                if (out.isEmpty()) return rc;
                if (!out.isItemEqual(ro)) return null;
                int cnt = out.getCount() + ro.getCount();
                return cnt <= baseTile.getInventoryStackLimit() && cnt <= out.getMaxStackSize() ? rc : null;
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
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.add(I18n.format("jst.tooltip.tile.com.ulvonly"));
	}
	
	@Override
	public int getComparatorInput() {
		if (cookTime > 0 && totalCookTime > 0)
			return cookTime * 15 / totalCookTime;
		return 0;
	}

	@Override
	public int getPrg() {
		return cookTime;
	}

	@Override
	public int getMxPrg() {
		return totalCookTime;
	}

	@Override
	public int getFuel() {
		return burnTime;
	}

	@Override
	public int getMxFuel() {
		return currentBurnTime;
	}
}