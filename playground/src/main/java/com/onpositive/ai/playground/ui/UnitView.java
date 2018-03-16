package com.onpositive.ai.playground.ui;

import java.awt.Color;
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
	
	public Image getImage() {
		return unit.getSide() == UnitSide.LEFT ? unitImages.get(Orientation.RIGHT) : unitImages.get(Orientation.LEFT);
	}

	public void paint(Graphics2D g2d) {
		Image image = unitImages.get(Orientation.LEFT);
		if (unit.getSide() == UnitSide.LEFT) {
			image = unitImages.get(Orientation.RIGHT);
		}
		Position position = unit.getPosition();
		int imgX = position.x * tileWidth;
		int imgY = (position.y + 1) * tileHeight - image.getHeight(null);
		g2d.drawImage(image, imgX, imgY, null);
		double ratio = Math.max(0, unit.getHealth()) * 1.0 / unit.getType().health;
		int barHeight = (int) (tileHeight * ratio);
		g2d.setColor(new Color((int)(255 * (1 - ratio)),(int) (255 * ratio),0));
		g2d.drawLine((position.x + 1) * tileWidth - 2, imgY + tileHeight - barHeight,(position.x + 1) * tileWidth - 2, imgY + tileHeight);
		g2d.drawLine((position.x + 1) * tileWidth - 3, imgY + tileHeight - barHeight,(position.x + 1) * tileWidth - 3, imgY + tileHeight);
	}

}
