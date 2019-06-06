package dohyun22.jst3.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialJST extends Material {
	public static final MaterialJST INSTANCE = new MaterialJST();
	
	public MaterialJST() {
		super(MapColor.STONE);
		setRequiresTool();
		setAdventureModeExempt();
	}
}
