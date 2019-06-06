package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerTMonitor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUITMonitor extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/temp.png");
	
	public GUITMonitor(Container con) {
		super(con);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float t, int mx, int my) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
	    int x = (this.width - this.xSize) / 2;
	    int y = (this.height - this.ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	    ContainerTMonitor con = (ContainerTMonitor) this.inventorySlots;
	    if (con.invert) drawTexturedModalRect(x + 151, y + 7, 176, 0, 18, 18);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		ContainerTMonitor con = (ContainerTMonitor) this.inventorySlots;
		this.fontRenderer.drawString(Integer.toString(con.temp), 56, 20, 0xFF00);
	}
}
