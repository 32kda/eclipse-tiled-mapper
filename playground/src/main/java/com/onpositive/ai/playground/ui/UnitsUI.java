package com.onpositive.ai.playground.ui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitType;

import tiled.util.BasicTileCutter;

public class UnitsUI {
	
	private UnitType[] unitTypeColsOrder = {UnitType.ARCHER, UnitType.WARRIOR, UnitType.KNIGHT, UnitType.ORC};
	private Map<UnitType, Map<Orientation, Image>> imageMaps;
	private int tileWidth;
	private int tileHeight;
	
	@SuppressWarnings("unchecked")
	public UnitsUI(GameView gameView) {
		
		tileWidth = gameView.getTileWidth();
		tileHeight = gameView.getTileHeight();
		
		BasicTileCutter cutter = new BasicTileCutter(tileWidth,tileHeight,0,0);
		
		try {
			BufferedImage img = ImageIO.read(new File("characters.png"));
			cutter.setImage(img);
			int tilesPerRow = cutter.getTilesPerRow();
			int tileIdx = 0;
			Map[] imageMapArray = new Map[unitTypeColsOrder.length];
			for (int i = 0; i < imageMapArray.length; i++) {
				imageMapArray[i] = new HashMap<Orientation, Image>();
			}
			Image tile;
			while ((tile = cutter.getNextTile()) != null) {
				int row = tileIdx / tilesPerRow;
				int col = tileIdx % tilesPerRow;
				imageMapArray[col].put(Orientation.values()[row], tile);
				tileIdx++;
			}
			imageMaps = new HashMap<>();
			for (int i = 0; i < imageMapArray.length; i++) {
				imageMaps.put(unitTypeColsOrder[i], imageMapArray[i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public UnitView createView(Unit unit) {
		return new UnitView(unit, imageMaps.get(unit.getType()),tileWidth, tileHeight);
	}

}
