package dohyun22.jst3.tiles.earlytech;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileKinetic;
import dohyun22.jst3.tiles.TileEntityMeta;

public class MetaTileGrinder extends MetaTileKinetic {

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileGrinder();
	}
	
	
}
