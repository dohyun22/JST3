package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerSolarFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUISolarFurnace extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/sfurnace.png");

	public GUISolarFurnace(Container r) {
		super(r);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
	    int x = (this.width - this.xSize) / 2;
	    int y = (this.height - this.ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	    
	    ContainerSolarFurnace con = (ContainerSolarFurnace) this.inventorySlots;
        if (con.sunVisible) this.drawTexturedModalRect(x + 53, y + 16, 176, 17, 14, 14);
	    
        int s = con.totalCookTime != 0 && con.cookTime != 0 ? con.cookTime * 24 / con.totalCookTime : 0;
        this.drawTexturedModalRect(x + 79, y + 34, 176, 0, s + 1, 16);
	}

}
