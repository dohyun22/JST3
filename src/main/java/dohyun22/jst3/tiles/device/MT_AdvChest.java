package dohyun22.jst3.tiles.device;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIAdvChest;
import dohyun22.jst3.container.ContainerAdvChest;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_AdvChest extends MetaTileBase {
	public final AdvChestType type;
	public boolean secured;
	private HashMap<UUID, Boolean> acl = new HashMap();
	public static final String numTagName = "JST_Count";
	private static final int[] bufferSlot = new int[] {0};

	public MT_AdvChest(AdvChestType in) {
		this.type = in;
		this.inv = NonNullList.<ItemStack>withSize(in.page * 54 + 1, ItemStack.EMPTY);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_AdvChest(type);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		this.secured = tag.getBoolean("secured");
		try {
			NBTTagCompound tag2 = (NBTTagCompound) tag.getTag("ACL");
			for (int n = 0; n < tag2.getInteger("SZ"); n++) {
				if (!tag2.hasKey("P" + n)) continue;
				NBTTagCompound tag3 = (NBTTagCompound) tag2.getTag("P" + n);
				acl.put(new UUID(tag3.getLong("M"), tag3.getLong("L")), tag3.getBoolean("A"));
			}
		} catch (Exception e) {}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("secured", this.secured);
		NBTTagCompound tag2 = new NBTTagCompound();
		tag2.setInteger("SZ", acl.size());
		int n = 0;
		try {
			for (UUID id : acl.keySet()) {
				tag2.setTag("P" + n, createACLTag(id, acl.get(id)));
			}
		} catch (Exception e) {}
		tag.setTag("ACL", tag2);
	}

	@Override
	public float getHardness() {
		return -1.0F;
	}
	
	@Override
	public float getResistance(Entity ee, Explosion ex) {
		return 10000000.0F;
	}
	
	@Override
	public boolean canEntityDestroy(Entity e) {
		return false;
	}
	
	@Override
	public boolean onBlockExploded(Explosion ex) {
		return false;
	}
	
	@Override
	public int getInvSize() {
		return 55;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(this.type.ordinal() + 1);
		return new TextureAtlasSprite[] {t, getTETex("screen1"), t, t, t, t};
	}
    
	@Override
	public boolean canSlotDrop(int num) {
		return false;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerAdvChest(inv, te);
		return null;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUIAdvChest(new ContainerAdvChest(inv, te));
		return null;
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile == null || getWorld().isRemote || pl == null) {
			return true;
		}
		if (haveAccess(pl, false)) {
			pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
			if (this.acl.isEmpty()) this.acl.put(pl.getUniqueID(), true);
		} else { 
			this.getWorld().playSound((EntityPlayer)null, this.getPos(), SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			JSTUtils.sendSimpleMessage(pl, "You don't have access to this Advanced Chest");
		}
		return true;
	}
	
	/** Checks access permission
	 * @param pl Player to check
	 * @param mod set this to true to check destroy/modify the block or the ACL
	 * */
	public boolean haveAccess(EntityPlayer pl, boolean mod) {
		return haveAccess(pl, mod, true);
	}
	
	public boolean haveAccess(EntityPlayer pl, boolean mod, boolean checkSecured) {
		if (this.acl.isEmpty()) return true;
		UUID id = pl.getUniqueID();
		Boolean b = this.acl.get(id);
		if (b == null) b = false;
		return (checkSecured && !this.secured) || (mod && b) || this.acl.containsKey(id) || JSTUtils.isOPorSP(pl, false);
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer pl) {
		if (this.getWorld().isRemote) return false;
		for (ItemStack st : this.inv) {
			if (!st.isEmpty()) {
				this.getWorld().playSound((EntityPlayer)null, this.getPos(), SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
				JSTUtils.sendSimpleMessage(pl, "You can't remove non-empty Advanced Chest");
				return false;
			}
		}
		if (!haveAccess(pl, true)) {
			this.getWorld().playSound((EntityPlayer)null, this.getPos(), SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			JSTUtils.sendSimpleMessage(pl, "You can't remove this Advanced Chest");
			return false;
		}
		return true;
	}
	
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
    	if (this.secured)
    		return new int[0];
    	return bufferSlot;
    }
    
	@Override
	public void onPostTick() {
		ItemStack st = this.inv.get(0);
		if (!st.isEmpty()) {
			for (int n = 1; n < this.inv.size(); n++) {
				st = insertItemToSlot(n, st);
				if (st.isEmpty()) break;
			}
		}
		this.inv.set(0, st);
	}
	
	@Override
	public boolean isUsable(EntityPlayer pl) {
		return haveAccess(pl, false);
	}
	
	/*@Override
	@SideOnly(value = Side.CLIENT)
	public String getModelKey() {
		return "AdvChest" + this.type.toString();
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public List<BakedQuad> getModel(boolean isItem) {
		TextureAtlasSprite[] tx = checkTextures(isItem ? this.getDefaultTexture() : this.getTexture());
		return JSTUtils.makeCube(tx, new float[] {0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F});
	}*/
	
	public ItemStack insertItemToSlot(int sl, ItemStack st) {
		if (st.isEmpty()) return st;
		ItemStack ret = st.copy();
		ItemStack con = this.inv.get(sl);
		if (isItemEmpty(con)) {
			NBTTagCompound tag = JSTUtils.getOrCreateNBT(ret);
			tag.setLong(numTagName, ret.getCount());
			ret.setCount(1);
			this.inv.set(sl, ret);
			return ItemStack.EMPTY;
		} else if (canCombine(ret, con)) {
			NBTTagCompound tag = JSTUtils.getOrCreateNBT(con);
			long amt = tag.getLong(numTagName);
			long amt2 = Math.min(this.type.stackSize, amt + ret.getCount());
			ret.shrink((int) (amt2 - amt));
			tag.setLong(numTagName, amt2);
		}
		//System.out.println(ret.toString());
		return ret;
	}
	
	public ItemStack getItemFromSlot(int sl, int amt) {
		ItemStack con = this.inv.get(sl);
		if (isItemEmpty(con)) return ItemStack.EMPTY;
		long sa = con.getTagCompound().getLong(numTagName);
		int rs = (int) Math.min(sa, Math.min(amt, con.getItem().getItemStackLimit(con)));
		ItemStack ret = con.copy();
		ret.getTagCompound().removeTag(numTagName);
		if (ret.getTagCompound().hasNoTags()) ret.setTagCompound(null);
		ret.setCount(rs);
		if (sa - rs <= 0)
			this.inv.set(sl, ItemStack.EMPTY);
		else
			con.getTagCompound().setLong(numTagName, sa - rs);
		//System.out.println(ret.toString());
		return ret;
	}
	
	public static boolean isItemEmpty(ItemStack st) {
		return st.isEmpty() || !st.hasTagCompound() || st.getTagCompound().getLong(numTagName) <= 0;
	}
	
    private static boolean canCombine(ItemStack in, ItemStack sl) {
		ItemStack sl2 = sl.copy();
		if (sl2.hasTagCompound()) sl2.getTagCompound().removeTag(numTagName);
		if (sl2.getTagCompound().hasNoTags()) sl2.setTagCompound(null);
		boolean ret = in.getItem() != sl2.getItem() ? false : (in.getMetadata() != sl2.getMetadata() ? false : (in.getCount() > in.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(in, sl2)));
		return ret;
    }
    
    public static ItemStack splitStack(ItemStack in, int amount) {
    	if (!in.hasTagCompound()) return ItemStack.EMPTY;
    	NBTTagCompound tag = in.getTagCompound();
    	long cnt = tag.getLong(numTagName);
        int r = (int)Math.min(amount, cnt);
        ItemStack ret = in.copy();
        if (ret.hasTagCompound()) ret.getTagCompound().removeTag(numTagName);
        ret.setCount(r);
        tag.setLong(numTagName, r);
        return ret;
    }
	
	/*@Override
    public ItemStack decrStackSize(int sl, int amt) {
		if (sl == 0) super.decrStackSize(sl, amt);
		//System.out.println("decrStackSize: Slot " + sl + ",Count " + amt);
		return getItemFromSlot(sl, amt);
    }
	
	@Override
	public ItemStack removeStackFromSlot(int sl) {
        ItemStack ret = this.inv.get(sl);

        if (isItemEmpty(ret)) {
            return ItemStack.EMPTY;
        } else {
        	this.inv.set(sl, ItemStack.EMPTY);
            ret = ret.copy();
            ret.getTagCompound().removeTag(numTagName);
            return ret;
        }
	}

	@Override
	public ItemStack getStackInSlot(int sl) {
		if (sl == 0) super.getStackInSlot(sl);
        ItemStack ret = this.inv.get(sl);
        return ret;
	}
	
	@Override
	public void setInventorySlotContents(int sl, ItemStack st) {
		if (sl == 0) super.setInventorySlotContents(sl, st);
		this.insertItemToSlot(sl, st);
	}
	
	@Override
    public void setInventorySlotContents(int sl, ItemStack st) {
		if (sl == 0) super.setInventorySlotContents(sl, st);
    }*/
    
    private static NBTTagCompound createACLTag(UUID uuid, boolean admin) {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setLong("M", uuid.getMostSignificantBits());
        ret.setLong("L", uuid.getLeastSignificantBits());
        ret.setBoolean("A", admin);
        return ret;
    }
    
    public static enum AdvChestType {
    	STANDARD(1, 256L),
    	IMPROVED(1, 1024L),
    	GOOD(2, 4096L),
    	ADVANCED(2, 32768L),
    	HIGHTECH(4, 262144L),
    	UHIGHTECH(4, 4194304L),
    	SUPER(8, 33554432L),
    	ULTIMATE(8, 536870912L),
    	INFINITE(8, Long.MAX_VALUE);
    	
    	public final int page;
    	public final long stackSize;
		private AdvChestType(int pg, long ssz) {
    		page = pg;
    		stackSize = ssz;
    	}
    }
}
