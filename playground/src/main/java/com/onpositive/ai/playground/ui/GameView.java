package com.onpositive.ai.playground.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import com.onpositive.ai.playground.model.Game;
import com.onpositive.ai.playground.model.Position;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitAction;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.TileLayer;
import tiled.view.MapRenderer;
import tiled.view.OrthogonalRenderer;

public class GameView extends JPanel implements Scrollable
{
    private final Map map;
    private final MapRenderer renderer;
	private Game game;
	private List<UnitView> unitViews;
	protected Position currentTile;
	protected List<ICellSelectionListener> cellSelectionListeners = new ArrayList<>();
	private Set<Position> reachableCells;
	private List<Unit> attackableUnits;
	private Unit curUnit;

    public GameView(Game game) {
        this.game = game;
		this.map = game.getGameMap();
        renderer = new OrthogonalRenderer(map);
        UnitsUI unitsUI = new UnitsUI(this);
        unitViews = game.getUnits().stream().map(unit -> unitsUI.createView(unit)).collect(Collectors.toList());
        setPreferredSize(renderer.getMapSize());
        setOpaque(true);
        hookListeners();
    }

    @Override
    public void paintComponent(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g.create();
        final Rectangle clip = g2d.getClipBounds();

        // Draw a gray background
        g2d.setPaint(new Color(100, 100, 100));
        g2d.fill(clip);

        // Draw each map layer
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TileLayer) {
                renderer.paintTileLayer(g2d, (TileLayer) layer);
            } 
        }
        
        for (UnitView unitView : unitViews) {
			unitView.paint(g2d);
		}
        
        if (reachableCells != null) {
        	int mapWidth = game.getGameMap().getWidth();
        	int mapHeight = game.getGameMap().getHeight();
        	int tileWidth = getTileWidth();
        	int tileHeight = getTileHeight();
        	
        	g2d.setPaint(new Color(20, 200, 20));
        	g2d.drawRect(curUnit.getPosition().x * tileWidth, curUnit.getPosition().y * tileHeight, tileWidth, tileHeight);
        	
        	g2d.setPaint(new Color(100, 100, 100, 100));
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					if (!reachableCells.contains(new Position(x,y))) {
						g2d.fillRect(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
					}
        		}
        	}
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL)
            return map.getTileWidth();
        else
            return map.getTileHeight();
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            final int tileWidth = map.getTileWidth();
            return (visibleRect.width / tileWidth - 1) * tileWidth;
        } else {
            final int tileHeight = map.getTileHeight();
            return (visibleRect.height / tileHeight - 1) * tileHeight;
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

	public int getTileWidth() {
		return map.getTileWidth();
	}

	public int getTileHeight() {
		return map.getTileHeight();
	}

	protected void hookListeners() {
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				int tileX = e.getX() / getTileWidth();
				int tileY = e.getY() / getTileHeight();
				if (map.inBounds(tileX, tileY)) {
					currentTile = new Position(tileX, tileY);
				}
				super.mouseMoved(e);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int tileX = e.getX() / getTileWidth();
				int tileY = e.getY() / getTileHeight();
				if (map.inBounds(tileX, tileY)) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						Position currentPos = new Position(tileX, tileY);
						if (reachableCells != null && reachableCells.contains(currentPos)) {
							fireCellSelected(currentPos);
						}
					}
				}
			}
			
		});
	}

	protected void fireCellSelected(Position currentPos) {
		for (ICellSelectionListener listener: cellSelectionListeners) {
			listener.cellSelected(currentPos);
		}
	}

	public Game getGame() {
		return game;
	}

	public boolean addCellSelectionListener(ICellSelectionListener e) {
		return cellSelectionListeners.add(e);
	}

	public boolean removeCellSelectionListener(Object o) {
		return cellSelectionListeners.remove(o);
	}

	public void setReachableCells(List<Position> reachableCells) {
		this.reachableCells = new HashSet<>(reachableCells);
	}

	public void setAttackableUnits(List<Unit> attackableUnits) {
		this.attackableUnits = attackableUnits;
	}
	
	public void refresh() {
		repaint();
	}

	public void finishTurn(UnitAction action) {
		game.finishTurn(action);
		reachableCells = null;
		attackableUnits = null;
		refresh();
	}

	public void setCurUnit(Unit curUnit) {
		this.curUnit = curUnit;
	}
}