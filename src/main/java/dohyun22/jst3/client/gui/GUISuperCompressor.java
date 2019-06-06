package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerSuperCompressor;
import dohyun22.jst3.tiles.device.MT_SuperCompressor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUISuperCompressor extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/comp.png");

	public GUISuperCompressor(Container con) {
		super(con);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	    
	    ContainerSuperCompressor con = (ContainerSuperCompressor) inventorySlots;
        int s = Math.min(12, (int) (con.amt != 0 ? con.amt * 12L / MT_SuperCompressor.itemNeededPerNeutronium : 0));
        if (s > 0) drawTexturedModalRect(x + 118, y + 23, 176, 0, s + 1, 12);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2){
		ContainerSuperCompressor con = (ContainerSuperCompressor) inventorySlots;
		fontRenderer.drawString(con.amt + " / " + MT_SuperCompressor.itemNeededPerNeutronium, 21, 65, 0);
	}
}