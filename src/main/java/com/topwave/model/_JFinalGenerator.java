package com.topwave.model;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;
import com.topwave.app.AppConfig;

import javax.sql.DataSource;

/**
 * 鏈� demo 浠呰〃杈炬渶涓虹矖娴呯殑 jfinal 鐢ㄦ硶锛屾洿涓烘湁浠峰�肩殑瀹炵敤鐨勪紒涓氱骇鐢ㄦ硶
 * 璇﹁ JFinal 淇变箰閮�: http://jfinal.com/club
 * 
 * 鍦ㄦ暟鎹簱琛ㄦ湁浠讳綍鍙樺姩鏃讹紝杩愯涓�涓� main 鏂规硶锛屾瀬閫熷搷搴斿彉鍖栬繘琛屼唬鐮侀噸鏋�
 */
public class _JFinalGenerator {
	
	public static DataSource getDataSource() {
		PropKit.use("db.properties");
		DruidPlugin druidPlugin = AppConfig.createDruidPlugin();
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 鎵�浣跨敤鐨勫寘鍚�
		String baseModelPackageName = "com.topwave.model.base";
		// base model 鏂囦欢淇濆瓨璺緞
		String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/topwave/model/base";
		
		// model 鎵�浣跨敤鐨勫寘鍚� (MappingKit 榛樿浣跨敤鐨勫寘鍚�)
		String modelPackageName = "com.topwave.model";
		// model 鏂囦欢淇濆瓨璺緞 (MappingKit 涓� DataDictionary 鏂囦欢榛樿淇濆瓨璺緞)
		String modelOutputDir = baseModelOutputDir + "/..";
		
		// 鍒涘缓鐢熸垚鍣�
		Generator generator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 璁剧疆鏄惁鐢熸垚閾惧紡 setter 鏂规硶
		generator.setGenerateChainSetter(false);
		// 娣诲姞涓嶉渶瑕佺敓鎴愮殑琛ㄥ悕
		generator.addExcludedTable("t_park");
		// 璁剧疆鏄惁鍦� Model 涓敓鎴� dao 瀵硅薄
		generator.setGenerateDaoInModel(true);
		// 璁剧疆鏄惁鐢熸垚閾惧紡 setter 鏂规硶
		generator.setGenerateChainSetter(true);
		// 璁剧疆鏄惁鐢熸垚瀛楀吀鏂囦欢
		generator.setGenerateDataDictionary(false);
		// 璁剧疆闇�瑕佽绉婚櫎鐨勮〃鍚嶅墠缂�鐢ㄤ簬鐢熸垚modelName銆備緥濡傝〃鍚� "osc_user"锛岀Щ闄ゅ墠缂� "osc_"鍚庣敓鎴愮殑model鍚嶄负 "User"鑰岄潪 OscUser
		generator.setRemovedTableNamePrefixes("m_");
		generator.setRemovedTableNamePrefixes("ord_");
		generator.setRemovedTableNamePrefixes("sys_");
		// 鐢熸垚
		generator.generate();
	}
}




