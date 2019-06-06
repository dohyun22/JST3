package dohyun22.jst3.container;

import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.earlytech.MetaTileSolarFurnace;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSolarFurnace extends ContainerMTE {
	public boolean sunVisible;
    public int cookTime;
    public int totalCookTime;
	
	public ContainerSolarFurnace(InventoryPlayer inv, TileEntityMeta te) {
		super(te);
		
		addSlotToContainer(new Slot(te, 0, 52, 34));
		addSlotToContainer(new SlotFurnaceOutput(inv.player, te, 1, 116, 35));
		
		addPlayerInventorySlots(inv, 8, 84);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (JSTUtils.isClient() || !(te.mte instanceof MetaTileSolarFurnace)) return;
	    
	    MetaTileSolarFurnace r = (MetaTileSolarFurnace)te.mte;
	    
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icl = (IContainerListener)this.listeners.get(i);
            
            if (this.sunVisible != r.sunVisible)
            	icl.sendWindowProperty(this, 100, r.sunVisible ? 1 : 0);
            
            if (this.cookTime != r.cookTime)
            	icl.sendWindowProperty(this, 102, r.cookTime);
            
            if (this.totalCookTime != r.totalCookTime)
            	icl.sendWindowProperty(this, 103, r.totalCookTime);
        }
	    
        this.sunVisible = r.sunVisible;
        this.cookTime = r.cookTime;
        this.totalCookTime = r.totalCookTime;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id == 100) this.sunVisible = data != 0;
		if (id == 102) this.cookTime = data;
		if (id == 103) this.totalCookTime = data;
	}
}
