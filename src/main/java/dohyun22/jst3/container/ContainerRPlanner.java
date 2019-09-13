package dohyun22.jst3.container;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.device.MT_ReactorPlanner;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.reactor.IReactorComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerRPlanner extends ContainerMTE {
	public int timer;
	public int heat;
	public int mxheat;
	public int boom;
	public int output;
	public byte speed;
	public long eu;
	public boolean active;

	public ContainerRPlanner(IInventory inv, TileEntityMeta te) {
		super(te);
		addSlotToContainer(new JSTSlot(te, 0, 6, 52, false, false, 64, false));
	    for (int r = 0; r < 6; r++) for (int c = 0; c < 9; c++) addSlotToContainer(new JSTSlot(te, 1 + c + r * 9, 26 + c * 18, 52 + r * 18, false, false, 64, false));
	    for (int n = 0; n < 6; n++) addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, n, 190, 52 + n * 18, false, false, 1, false));
	    addSlotToContainer(new Slot(te, 55, 6, 74));
	    addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, 6, 6, 96, false, false, 1, false));
	    addSlotToContainer(new JSTSlot(InventoryDummy.INSTANCE, 7, 6, 114, false, false, 1, false));
	    addPlayerInventorySlots(inv, 26, 164);
	}

	@Override
	public ItemStack slotClick(int si, int dt, ClickType ct, EntityPlayer pl) {
	    if (!(te.mte instanceof MT_ReactorPlanner)) return ItemStack.EMPTY;
	    if (si >= 0 && si < inventorySlots.size()) {
	    	Slot s = getSlot(si);
	    	if (s != null) {
	    		MT_ReactorPlanner mte = ((MT_ReactorPlanner)te.mte);
    			ItemStack st = pl.inventory.getItemStack();
	    		if (s.inventory == te && si != 61) {
	    			if (s.getSlotIndex() == 0) {
	    				int mode = -1;
	    				if (ct == ClickType.CLONE)
	    					mode = 0;
	    				if (ct == ClickType.PICKUP)
	    					mode = dt + 1;
	    				mte.toggleItem(mode);
	    			} else if (ct == ClickType.PICKUP && mte.boomPowerX10 <= 0) {
	    				ItemStack st2 = ItemStack.EMPTY;
	    				if (dt != 1) {
	    					if (st.isEmpty())
	    						st2 = te.mte.getStackInSlot(0);
	    					else
	    						st2 = st;
	    					st2 = st2.copy();
	    					st2.setCount(1);
	    				}
	    				if (st2.isEmpty() || (JSTCfg.ic2Loaded && st2.getItem() instanceof IReactorComponent && ((IReactorComponent)st2.getItem()).canBePlacedIn(st2, ((MT_ReactorPlanner)te.mte))))
	    					s.putStack(st2);
	    			}
	    			return st;
	    		}
	    		if (s.inventory == InventoryDummy.INSTANCE && ct == ClickType.PICKUP) {
	    			if (!JSTUtils.isClient()) {
		    			int n = s.getSlotIndex();
	    				if (n == 1) {
		    				mte.reset(dt == 1);
		    				detectAndSendChanges();
	    				}
	    				if (mte.boomPowerX10 <= 0) {
	    					if (n == 0) {
	    						mte.toggleActive();
	    					} else if (n == 2 || n == 3) {
	    						int v = dt == 1 ? 1 : 10;
	    						if (n == 3) v *= -1;
	    						mte.changeSpeed(v);
	    					} else if (n == 4 || n == 5) {
	    						int v = dt == 1 ? 100 : 1000;
	    						if (n == 5) v *= -1;
	    						v += mte.heat;
	    						mte.heat = Math.max(0, v);
	    					} else if (n == 6) {
	    						mte.saveToDSD();
	    						super.detectAndSendChanges();
	    					} else if (n == 7) {
	    						mte.readFromDSD();
	    						super.detectAndSendChanges();
	    					} 
		    			}
	    			}
	    			return st;
	    		}
	    	}
	    }
	    return super.slotClick(si, dt, ct, pl);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (JSTUtils.isClient() || !(te.mte instanceof MT_ReactorPlanner))
	        return;
		
		MT_ReactorPlanner r = (MT_ReactorPlanner)te.mte;
		
        for (IContainerListener icl : listeners) {
            if (timer != r.timer)
            	splitIntAndSend(icl, this, 100, r.timer);
            if (heat != r.heat)
            	splitIntAndSend(icl, this, 102, r.heat);
            if (mxheat != r.maxHeat)
            	splitIntAndSend(icl, this, 104, r.maxHeat);
            if (boom != r.boomPowerX10)
            	splitIntAndSend(icl, this, 106, r.boomPowerX10);
            if (output != ((int)(r.output * 50)))
            	splitIntAndSend(icl, this, 108, (int)(r.output * 50));
            if (eu != r.powerGenerated)
            	splitLongAndSend(icl, this, 110, r.powerGenerated);
            if (speed != r.getSimSpeed())
            	icl.sendWindowProperty(this, 114, r.getSimSpeed());
            if (active != r.baseTile.isActive())
            	icl.sendWindowProperty(this, 115, r.baseTile.isActive() ? 1 : 0);
        }
        
	    timer = r.timer;
	    heat = r.heat;
	    mxheat = r.maxHeat;
	    boom = r.boomPowerX10;
	    output = (int)(r.output * 50);
	    eu = r.powerGenerated;
	    speed = r.getSimSpeed();
	    active = r.baseTile.isActive();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		
		timer = getIntFromData(timer, 100, id, data);
		heat = getIntFromData(heat, 102, id, data);
		mxheat = getIntFromData(mxheat, 104, id, data);
		boom = getIntFromData(boom, 106, id, data);
		output = getIntFromData(output, 108, id, data);
		eu = getLongFromData(eu, 110, id, data);
		if (id == 114) speed = (byte) data;
		if (id == 115) active = data == 1;
	}
}
