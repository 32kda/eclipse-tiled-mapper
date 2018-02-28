package com.onpositive.ai.playground.ui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Map;

import com.onpositive.ai.playground.model.Position;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitSide;

public class UnitView {

	private Unit unit;
	private Map<Orientation, Image> unitImages;
	private int tileWidth;
	private int tileHeight;

	public UnitView(Unit unit, Map<Orientation, Image> unitImages, int tileWidth, int tileHeight) {
		this.unit = unit;
		this.unitImages = unitImages;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Map<Orientation, Image> getUnitImages() {
		return unitImages;
	}

	public void setUnitImages(Map<Orientation, Image> unitImages) {
		this.unitImages = unitImages;
	}

	public void paint(Graphics2D g2d) {
		Image image = unitImages.get(Orientation.LEFT);
		if (unit.getSide() == UnitSide.LEFT) {
			image = unitImages.get(Orientation.RIGHT);
		}
		Position position = unit.getPosition();
		g2d.drawImage(image, position.x * tileWidth, (position.y + 1) * tileHeight - image.getHeight(null), null);
	}

}
