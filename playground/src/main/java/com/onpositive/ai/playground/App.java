package com.onpositive.ai.playground;


import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import com.onpositive.ai.playground.model.Game;
import com.onpositive.ai.playground.model.Position;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitSide;
import com.onpositive.ai.playground.model.UnitType;
import com.onpositive.ai.playground.ui.GameView;
import com.onpositive.ai.playground.ui.UITurnController;

import tiled.core.Map;
import tiled.io.TMXMapReader;

/**
 * Simple game app
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	try {
			Map currentMap = new TMXMapReader().readMap("playground1.tmx");

	        System.out.println(currentMap.toString() + " loaded");

	        Game game = new Game(currentMap);
	        addUnits(game);
	        
	        GameView gameView = new GameView(game);
//			JScrollPane scrollPane = new JScrollPane(gameView);
//	        scrollPane.setBorder(null);
//	        scrollPane.setPreferredSize(new Dimension(800, 700));
	        
	        JPanel panel = new JPanel(new BorderLayout());
	        JTextPane infoPane = new JTextPane();
	        infoPane.setEditable(false);
	        infoPane.setPreferredSize(new Dimension(200, 480));
	        panel.add(infoPane, BorderLayout.LINE_START);
	        panel.add(gameView, BorderLayout.CENTER);
	        
	        gameView.setSideTurnController(UnitSide.LEFT, new UITurnController(gameView, infoPane));
	        gameView.setSideTurnController(UnitSide.RIGHT, new UITurnController(gameView, infoPane));
//	        scrollPane.add(new GameView(game));

	        JFrame appFrame = new JFrame("Sample Game");
	        appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	        appFrame.setContentPane(panel);
	        appFrame.pack();
	        appFrame.setVisible(true);
	        game.nextTurn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private static void addUnits(Game game) {
		int leftCol = 3;
		int rightCol = 16;
		int row = 6;
//		game.addUnit(new Unit(UnitType.KNIGHT,UnitSide.LEFT, new Position(leftCol,row)));
//		game.addUnit(new Unit(UnitType.KNIGHT,UnitSide.RIGHT, new Position(rightCol,row)));
//		row++;
		
		game.addUnit(new Unit(UnitType.WARRIOR,UnitSide.LEFT, new Position(leftCol,row)));
		game.addUnit(new Unit(UnitType.WARRIOR,UnitSide.RIGHT, new Position(rightCol,row)));
		row++;
		
//		game.addUnit(new Unit(UnitType.WARRIOR,UnitSide.LEFT, new Position(leftCol,row)));
//		game.addUnit(new Unit(UnitType.WARRIOR,UnitSide.RIGHT, new Position(rightCol,row)));
//		row++;
		
		game.addUnit(new Unit(UnitType.ARCHER,UnitSide.LEFT, new Position(leftCol,row)));
		game.addUnit(new Unit(UnitType.ARCHER,UnitSide.RIGHT, new Position(rightCol,row)));
		row++;
		
//		game.addUnit(new Unit(UnitType.ARCHER,UnitSide.LEFT, new Position(leftCol,row)));
//		game.addUnit(new Unit(UnitType.ARCHER,UnitSide.RIGHT, new Position(rightCol,row)));
//		row++;
		
		game.addUnit(new Unit(UnitType.ORC,UnitSide.LEFT, new Position(leftCol,row)));
		game.addUnit(new Unit(UnitType.ORC,UnitSide.RIGHT, new Position(rightCol,row)));
		row++;
		
//		game.addUnit(new Unit(UnitType.ORC,UnitSide.LEFT, new Position(leftCol,row)));
//		game.addUnit(new Unit(UnitType.ORC,UnitSide.RIGHT, new Position(rightCol,row)));
//		row++;
	}
}
