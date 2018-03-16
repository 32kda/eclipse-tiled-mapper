package com.onpositive.ai.playground.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import com.onpositive.ai.playground.model.Game;
import com.onpositive.ai.playground.model.ITurnController;
import com.onpositive.ai.playground.model.Position;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitAction;
import com.onpositive.ai.playground.model.UnitSide;

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
	protected List<IUITurnController> cellSelectionListeners = new ArrayList<>();
	private Set<Position> reachableCells;
	private List<Unit> attackableUnits;
	
	private java.util.Map<UnitSide,IUITurnController> turnControllers = new HashMap<>();
	private Unit curUnit;
	private Position selectedMovePosition;

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
        
        
        int tileWidth = getTileWidth();
        int tileHeight = getTileHeight();
        if (reachableCells != null) {
        	int mapWidth = game.getGameMap().getWidth();
        	int mapHeight = game.getGameMap().getHeight();
        	
        	g2d.setPaint(new Color(20, 200, 20));
        	g2d.drawRect(curUnit.getPosition().x * tileWidth, curUnit.getPosition().y * tileHeight, tileWidth, tileHeight);
        	
        	g2d.setPaint(new Color(100, 100, 100, 70));
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					if (!reachableCells.contains(new Position(x,y))) {
						g2d.fillRect(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
					}
        		}
        	}
        }
        
        if (attackableUnits != null) {
        	g2d.setPaint(new Color(180, 0, 0, 100));
        	for (Unit unit : attackableUnits) {
        		g2d.fillRect(unit.getPosition().x * tileWidth, unit.getPosition().y * tileHeight, tileWidth, tileHeight);
			}
        }
        
		if (curUnit != null && selectedMovePosition != null) {
			for (UnitView unitView : unitViews) {
				if (unitView.getUnit() == curUnit) {
					final Graphics2D tmp = (Graphics2D) g2d.create();
					try {
						Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
								new float[] { 9 }, 0);
						tmp.setStroke(dashed);
						tmp.drawRect(selectedMovePosition.x * tileWidth, selectedMovePosition.y * tileHeight, tileWidth,
								tileHeight);
						tmp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
						tmp.drawImage(unitView.getImage(), selectedMovePosition.x * tileWidth,
								selectedMovePosition.y * tileHeight, null);
					} finally {
						tmp.dispose();
					}
				}
			}
		}
        
        if (currentTile != null) {
        	g2d.setPaint(new Color(200, 200, 20));
        	g2d.drawRect(currentTile.x * tileWidth, currentTile.y * tileHeight, tileWidth, tileHeight);
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
			public void mouseClicked(MouseEvent e) {
				if (curUnit == null || reachableCells == null) {
					return;
				}
				int tileX = e.getX() / getTileWidth();
				int tileY = e.getY() / getTileHeight();
				if (map.inBounds(tileX, tileY)) {
					Position currentPos = new Position(tileX, tileY);
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (reachableCells.contains(currentPos)) {
							fireCellSelected(currentPos, false);
						}
					}
					if (e.getButton() == MouseEvent.BUTTON3) {
						boolean targetFound = false;
						for (Unit unit : attackableUnits) {
							if (unit.getPosition().equals(currentPos)) {
								fireTargetSelected(unit);
								targetFound = true;
								break;
							}
						}
						if (!targetFound && reachableCells.contains(currentPos)) {
							fireCellSelected(currentPos, true);
						}
					}
				}
			}
			
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				int tileX = e.getX() / getTileWidth();
				int tileY = e.getY() / getTileHeight();
				if (map.inBounds(tileX, tileY)) {
					currentTile = new Position(tileX, tileY);
					refresh();
				}
			}
			
		});
	}

	protected void fireTargetSelected(Unit unit) {
		IUITurnController controller = turnControllers.get(curUnit.getSide());
		if (controller != null) {
			controller.targetSelected(unit);
		}
	}

	protected void fireCellSelected(Position currentPos, boolean rightClick) {
		IUITurnController controller = turnControllers.get(curUnit.getSide());
		if (controller != null) {
			controller.cellSelected(currentPos);
			if (attackableUnits.isEmpty() || rightClick) {
				controller.targetSelected(null);
			}
		}
	}

	public Game getGame() {
		return game;
	}

	public boolean addCellSelectionListener(IUITurnController e) {
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
		reachableCells = null;
		attackableUnits = null;
		selectedMovePosition = null;
		setCurUnit(null);
		List<Unit> units = game.getUnits();
		for (Iterator<UnitView> iterator = unitViews.iterator(); iterator.hasNext();) {
			UnitView unitView = iterator.next();
			if (!units.contains(unitView.getUnit())) {
				iterator.remove();
			}
		}
		refresh();
	}

	public void setCurUnit(Unit curUnit) {
		this.curUnit = curUnit;
	}

	public void setSideTurnController(UnitSide side, ITurnController turnController) {
		game.setSideTurnController(side, turnController);
		if (turnController instanceof IUITurnController) {
			turnControllers.put(side, (IUITurnController) turnController);
		}
	}

	public void setSelectedMovePosition(Position selectedMovePosition) {
		this.selectedMovePosition = selectedMovePosition;
	}
}