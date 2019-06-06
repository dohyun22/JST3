package dohyun22.jst3.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerAdvChest;
import dohyun22.jst3.tiles.device.MT_AdvChest;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIAdvChest extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/9by6.png");

	public GUIAdvChest(Container con) {
		super(con);
		this.xSize = 176;
		this.ySize = 224;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
	    int x = (this.width - this.xSize) / 2;
	    int y = (this.height - this.ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	    drawTexturedModalRect(x + 7, y + 199, 176, 0 + (((ContainerAdvChest)this.inventorySlots).secured ? 18 : 0), 18, 18);
	}
	
	@Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		ContainerAdvChest con = (ContainerAdvChest) this.inventorySlots;
	    for (int r = 0; r < 54; r++) {
	    	Slot sl = con.getSlot(r);
	    	ItemStack st = sl.getStack();
	    	if (st.getCount() != 1) continue;
	    	String str = format(st.hasTagCompound() ? st.getTagCompound().getLong(MT_AdvChest.numTagName) : 0);
	    	if (str != null)
	    		this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, st, sl.xPos, sl.yPos, str);
	    }
    }
	
	@Override
    protected void renderToolTip(ItemStack st, int x, int y) {
        List<String> list = st.getTooltip(null, this.mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL);

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0)
                list.set(i, st.getRarity().rarityColor + (String)list.get(i));
            else
                list.set(i, TextFormatting.GRAY + (String)list.get(i));
        }
        
        long num = st.hasTagCompound() ? st.getTagCompound().getLong(MT_AdvChest.numTagName) : 0;
        if (num > 999) list.add("Stack Size: " + Long.valueOf(num).toString());

        FontRenderer font = st.getItem().getFontRenderer(st);
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(st);
        this.drawHoveringText(list, x, y, (font == null ? fontRenderer : font));
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }
	
	private static String format(Long l) {
		if (l <= 0) return TextFormatting.RED + Long.valueOf(l).toString();
		if (l == 1) return null;
		
		if (l > 999999999999999999L)
			return ">1E"; 
		else if (l > 999999999999999L)
			return ">1P"; 
		else if (l > 999999999999L)
			return ">1T"; 
		else if (l > 999999999L)
			return ">1G"; 
		else if (l > 999999L)
			return ">1M"; 
		else if (l > 999L)
			return ">1k"; 
		return Long.valueOf(l).toString();
	}
}
