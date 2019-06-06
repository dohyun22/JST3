package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerNaquadahGen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUINaquadahGen extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/nqgen.png");

	public GUINaquadahGen(ContainerNaquadahGen r) {
		super(r);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2){
		ContainerNaquadahGen con = (ContainerNaquadahGen) this.inventorySlots;
		this.fontRenderer.drawString("Output: " + con.output + " EU", 84, 10, 4210752);
		this.fontRenderer.drawString("" + (Math.min(con.energy, con.mxenergy)), 84, 40, 4210752);
		this.fontRenderer.drawString("/" + (con.mxenergy) + " EU", 84, 50, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float t, int mx, int my) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
	    int x = (this.width - this.xSize) / 2;
	    int y = (this.height - this.ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	    
	    ContainerNaquadahGen con = (ContainerNaquadahGen) this.inventorySlots;
	    if (con.energy > 0) {
	    	int i = (int) ((Math.min(Math.max(con.energy, 0), con.mxenergy) * 24L) / Math.max(con.mxenergy, 1L));
	    	drawTexturedModalRect(x + 31, y + 53, 176, 0, i, 16);
	    }
	}
}
