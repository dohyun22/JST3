package dohyun22.jst3.tiles.interfaces;

import java.util.List;

public interface IConfigurable {
	short[] getCfg();
	void changeCfg(int idx);
	List<Short> getCfgList();
	void changeCfgList(int idx, boolean en);
	String getCfgName(int num);
}