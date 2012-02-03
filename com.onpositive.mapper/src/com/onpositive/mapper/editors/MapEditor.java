package com.onpositive.mapper.editors;

import java.awt.geom.Area;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.EditorPart;

import tiled.core.Map;
import tiled.core.MapChangeListener;
import tiled.core.MapChangedEvent;
import tiled.core.MapLayer;
import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.io.TMXMapReader;
import tiled.io.TMXMapWriter;
import tiled.mapeditor.brush.AbstractBrush;
import tiled.mapeditor.brush.CustomBrush;
import tiled.mapeditor.brush.ITileBrush;
import tiled.mapeditor.brush.ShapeBrush;
import tiled.mapeditor.resources.Resources;
import tiled.mapeditor.selection.SelectionLayer;
import tiled.mapeditor.undo.AddObjectEdit;
import tiled.mapeditor.undo.MapLayerEdit;
import tiled.mapeditor.undo.MoveLayerEdit;
import tiled.mapeditor.undo.MoveObjectEdit;
import tiled.mapeditor.widget.BrushPreview;
import tiled.util.Converter;
import tiled.util.TiledConfiguration;
import tiled.view.MapView;

import com.onpositive.mapper.actions.CopyAction;
import com.onpositive.mapper.actions.PasteAction;
import com.onpositive.mapper.dialogs.ObjectPropertyDialog;
import com.onpositive.mapper.perspective.MapperPerspective;
import com.onpositive.mapper.ui.UIUtil;

/**
 * Main Editor class for Map Editor Part
 * Based on Tiled Java
 * @author Dmitry Karpenko
 *
 */
public class MapEditor extends EditorPart implements MapChangeListener, ILocalUndoSupport {

	public static final String CURRENT_LAYER_PROP = "currentLayer";
	
	protected static class PartListener implements IPartListener2 {
		private static final String MAPPER_CONTEXT_ID = "com.onpositive.mapper.context";
		private static final IContextService service = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
		private static IContextActivation activation;
		
		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
			if (partRef.getId().indexOf("mapper") > 0 && UIUtil.getActiveEditor() instanceof MapEditor) {
				if (activation == null) {
				activation = service.activateContext(MAPPER_CONTEXT_ID);
				}
			} else if (activation != null) {
				service.deactivateContext(activation);
				activation = null;
			}
		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}
		
	}

	protected class MapMouseListener implements MouseListener,
			MouseMoveListener, MouseTrackListener {

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseDown(MouseEvent e) {
			mousePressed(e);
		}

		@Override
		public void mouseUp(MouseEvent e) {
			mouseReleased(e);
		}

		@Override
		public void mouseMove(MouseEvent e) {
			mouseMoved(e);
		}

		@Override
		public void mouseEnter(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExit(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseHover(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	};

	// Constants and the like
	public static final String POINTER_STATE_PROP = "POINTER_STATE_PROP";
	
	public static final String MARQEE_SELECTION_PROP = "MARQEE_SELECTION_PROP";
	public static final String CLIPBOARD_CONTENT_PROP = "CLIPBOARD_CONTENT_PROP";
	
	public static final int PS_POINT = 0;
	public static final int PS_PAINT = 1;
	public static final int PS_ERASE = 2;
	public static final int PS_POUR = 3;
	public static final int PS_EYED = 4;
	public static final int PS_MARQUEE = 5;
	public static final int PS_MOVE = 6;
	public static final int PS_ADDOBJ = 7;
	public static final int PS_REMOVEOBJ = 8;
	public static final int PS_MOVEOBJ = 9;

	private static final String TOOL_PAINT = Resources.getString("tool.paint.name");
	private static final String TOOL_ERASE = Resources.getString("tool.erase.name");
	private static final String TOOL_FILL = Resources.getString("tool.fill.name");
//	private static final String TOOL_EYE_DROPPER = Resources.getString("tool.eyedropper.name");
//	private static final String TOOL_SELECT = Resources.getString("tool.select.name");
//	private static final String TOOL_MOVE_LAYER = Resources.getString("tool.movelayer.name");
//	private static final String TOOL_ADD_OBJECT = Resources.getString("tool.addobject.name");
//	private static final String TOOL_REMOVE_OBJECT = Resources.getString("tool.removeobject.name");
//	private static final String TOOL_MOVE_OBJECT = Resources.getString("tool.moveobject.name");
	private static final Preferences prefs = TiledConfiguration.root();

	private static IPartListener2 mapEditorListener = new PartListener();
	
	private Map currentMap;
	private Tile currentTile;
	private AbstractBrush currentBrush;

	private Point mousePressLocation, mouseInitialPressLocation;
	private Point mouseLastPixelLocation;
	private Point mouseInitialScreenLocation;
	private Point moveDist;
	private int mouseButton;
	private boolean bMouseIsDown, bMouseIsDragging;

	private int currentPointerState;
	private MapObject currentObject = null;
	private int currentLayer = -1;
	private SelectionLayer cursorHighlight;

	private SelectionLayer marqueeSelection;

	private MapView mapView;
	private ScrolledComposite mapScrollView;
	private IStatusLineManager statusLineManager;
	private MapMouseListener mouseListener = new MapMouseListener();
	private BrushPreview brushPreview;
	private FileDocumentProvider documentProvider;
	private boolean dirty = false;
	private IDocument document;
	private boolean init;
	private IOperationHistory operationHistory;
	private MapLayerEdit paintEdit;
	private IUndoContext undoContext = new ObjectUndoContext(this,"MapEditor");

	private UndoActionHandler undoAction;
	private RedoActionHandler redoAction;
	
	private Action copyAction;
	private Action pasteAction;

	private MapLayer clipboardLayer;

	
    @Override
	public void doSave(IProgressMonitor monitor) {
		try {
			String basePath = "/.";
			IEditorInput input = getEditorInput();
			if (input instanceof IPathEditorInput) {
				IPath path = ((IPathEditorInput) input).getPath();
				basePath = path.toOSString();
			}
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			new TMXMapWriter().writeMap(currentMap, outputStream, basePath);
			byte[] bytes = outputStream.toByteArray();
			document.set(new String(bytes));
			documentProvider.saveDocument(monitor,input,document,true);
			setDirty(false);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
		try {
			documentProvider = new FileDocumentProvider();
			documentProvider.connect(input);
			document = documentProvider.getDocument(input);
			IAnnotationModel annotationModel = documentProvider.getAnnotationModel(input);
			if (annotationModel != null)
				annotationModel.connect(document);
			if (input instanceof IPathEditorInput) {
				IPath path = ((IPathEditorInput) input).getPath();
				System.out.println(path.toOSString());
				currentMap = new TMXMapReader().readMap(
						new ByteArrayInputStream(document.get().getBytes()),
						path.toFile());
			} else {
				currentMap = new TMXMapReader()
						.readMap(new ByteArrayInputStream(document.get()
								.getBytes()));
			}
			
			IActionBars actionBars = getEditorSite().getActionBars();
			undoAction= new UndoActionHandler(getSite(), undoContext);
			undoAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_UNDO);
			redoAction= new RedoActionHandler(getSite(), undoContext);
			redoAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_REDO);
			actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
			actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
			
			copyAction = new CopyAction(this);
			copyAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);
			addPartPropertyListener((IPropertyChangeListener) copyAction);
			copyAction.setEnabled(false);
			pasteAction = new PasteAction(this);
			pasteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_PASTE);
			addPartPropertyListener((IPropertyChangeListener) pasteAction);
			actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
			actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);
			pasteAction.setEnabled(false);

			
			
			operationHistory.addOperationHistoryListener(new IOperationHistoryListener() {
				
				@Override
				public void historyNotification(OperationHistoryEvent event) {
					if (event.getEventType() == OperationHistoryEvent.UNDONE ||
						event.getEventType() == OperationHistoryEvent.REDONE)
						mapChanged(null);
				}
			});
			site.getPage().addPartListener(mapEditorListener);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setPartName(input.getName());
		IActionBars actionBars = site.getActionBars();
		if (actionBars != null) {
			statusLineManager = actionBars.getStatusLineManager();
		}
		firePropertyChange(IEditorPart.PROP_TITLE);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		init = true;
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		mapScrollView = new ScrolledComposite(composite, SWT.H_SCROLL
				| SWT.V_SCROLL);
		mapView = MapView.createViewforMap(mapScrollView, currentMap);
		mapView.addMouseListener(mouseListener);
		mapView.addMouseMoveListener(mouseListener);
		mapView.addMouseTrackListener(mouseListener);
		mapScrollView.setContent(mapView);
		currentMap.addMapChangeListener(this);

		cursorHighlight = new SelectionLayer(1, 1);
		cursorHighlight.select(0, 0);
		cursorHighlight.setVisible(prefs.getBoolean("cursorhighlight", true));

		ShapeBrush sb = new ShapeBrush();
		sb.makeQuadBrush(new Rectangle(0, 0, 1, 1));
		setBrush(sb);
		setCurrentMap(currentMap);
		init = false;
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				IWorkbenchWindow workbenchWindow = getSite().getPage().getWorkbenchWindow();
				try {
					PlatformUI.getWorkbench().showPerspective(MapperPerspective.ID,workbenchWindow);
				} catch (WorkbenchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void setFocus() {
		mapView.setFocus();
	}

	public Map getMap() {
		return currentMap;
	}

	public void setBrush(AbstractBrush currentBrush) {
		// Make sure a possible current highlight gets erased from screen
		if (mapView != null && prefs.getBoolean("cursorhighlight", true)) {
			Rectangle redraw = cursorHighlight.getBounds();
			mapView.repaintRegion(redraw);
		}

		this.currentBrush = currentBrush;

		// Resize and select the region
		Rectangle brushRedraw = currentBrush.getBounds();
		cursorHighlight.resize(brushRedraw.width, brushRedraw.height, 0, 0);
		cursorHighlight.selectRegion(currentBrush.getShape());
		MapLayer layer = currentBrush.getLayer(0);
		cursorHighlight.copyTileData(layer);
		if (mapView != null) {
			mapView.setBrush(currentBrush);
		}
	}

	public void resetBrush() {
		// FIXME: this is an in-elegant hack, but it gets the user out
		// of custom brush mode
		// (reset the brush if necessary)
		if (currentBrush instanceof CustomBrush) {
			ShapeBrush sb = new ShapeBrush();
			sb.makeQuadBrush(new Rectangle(0, 0, 1, 1));
			sb.setTile(currentTile);
			setBrush(sb);
		}
	}

	/**
	 * Changes the currently selected tile.
	 * 
	 * @param tile
	 *            the new tile to be selected
	 */
	public void setCurrentTile(Tile tile) {
		resetBrush();

		if (currentTile != tile) {
			currentTile = tile;
			if (currentBrush instanceof ShapeBrush) {
				((ShapeBrush) currentBrush).setTile(tile);
			}
			if (brushPreview != null)
				brushPreview.setBrush(currentBrush);
			if (mapView != null) {
				mapView.setBrush(currentBrush);
			}
		}
		cursorHighlight.setSelTile(currentTile);
	}

	// private ColorManager colorManager;
	//
	// public MapEditor() {
	// super();
	// colorManager = new ColorManager();
	// setSourceViewerConfiguration(new XMLConfiguration(colorManager));
	// setDocumentProvider(new XMLDocumentProvider());
	// }
	// public void dispose() {
	// colorManager.dispose();
	// super.dispose();
	// }

	public void mousePressed(MouseEvent e) {
		Point tile = mapView.screenToTileCoords(e.x, e.y);
		mouseButton = e.button;
		bMouseIsDown = true;
		bMouseIsDragging = false;
		mousePressLocation = mapView.screenToTileCoords(e.x, e.y);
		mouseInitialPressLocation = mousePressLocation;

		MapLayer layer = getCurrentLayer();

		if (mouseButton == 2
				|| (mouseButton == 1 && (e.stateMask & SWT.ALT) != 0)) {
			// Remember screen location for scrolling with middle mouse button
			mouseInitialScreenLocation = new Point(e.x, e.y);
		} else if (mouseButton == 1) {
			switch (currentPointerState) {
			case PS_PAINT:
				if (layer instanceof TileLayer) {
					currentBrush.startPaint(currentMap, tile.x, tile.y,
							mouseButton, currentLayer);
				}
			case PS_ERASE:
			case PS_POUR:
				 paintEdit = new MapLayerEdit(layer, createLayerCopy(layer),
				 null);
				break;
			default:
			}
		}

		if (currentPointerState == PS_MARQUEE) {
			boolean contains = false;
			if (marqueeSelection != null
					&& marqueeSelection.getSelectedArea().contains(tile.x,
							tile.y)) {
				contains = true;
			}
			if (marqueeSelection == null && !contains) {
				setMarqueeSelection(new SelectionLayer(currentMap.getWidth(),
						currentMap.getHeight()));
				currentMap.addLayerSpecial(marqueeSelection);
			} else if (marqueeSelection != null
					&& (e.stateMask & SWT.BUTTON1) > 0) {
				currentMap.removeLayerSpecial(marqueeSelection);
				if (contains) {
					setMarqueeSelection(null);
				} else {
					setMarqueeSelection(new SelectionLayer(
							currentMap.getWidth(), currentMap.getHeight()));
					currentMap.addLayerSpecial(marqueeSelection);
				}
			}
		} else if (currentPointerState == PS_MOVE) {
			// Initialize move distance to (0, 0)
			moveDist = new Point(0, 0);
		}

		doMouse(e);
		bMouseIsDragging = true;
	}

	public void mouseReleased(MouseEvent event) {
		final MapLayer layer = getCurrentLayer();
		final Point limp = mouseInitialPressLocation;

		if (currentPointerState == PS_MARQUEE) {
			// Uncommented to allow single tile selections
			/*
			 * Point tile = mapView.screenToTileCoords(event.x, event.y); if
			 * (tile.y - limp.y == 0 && tile.x - limp.x == 0) { if
			 * (marqueeSelection != null) {
			 * currentMap.removeLayerSpecial(marqueeSelection); marqueeSelection
			 * = null; } }
			 */

			// There should be a proper notification mechanism for this...
			// if (marqueeSelection != null) {
			// tileInstancePropertiesDialog.setSelection(marqueeSelection);
			// }
		} else if (currentPointerState == PS_MOVE) {
			if (layer != null && (moveDist.x != 0 || moveDist.x != 0)) {
				 addEdit(new MoveLayerEdit(layer, moveDist));
			}
		} else if (currentPointerState == PS_PAINT) {
			if (layer instanceof TileLayer) {
				currentBrush.endPaint();
			}
		} else if (currentPointerState == PS_MOVEOBJ) {
			if (layer instanceof ObjectGroup && currentObject != null
					&& (moveDist.x != 0 || moveDist.x != 0)) {
				 addEdit(
				 new MoveObjectEdit(currentObject, moveDist));
			}
		}

		if (/* bMouseIsDragging && */currentPointerState == PS_PAINT
				|| currentPointerState == PS_ADDOBJ) {
			Point tile = mapView.screenToTileCoords(event.x, event.y);
			int minx = Math.min(limp.x, tile.x);
			int miny = Math.min(limp.y, tile.y);

			Rectangle bounds = new Rectangle(minx, miny, (Math.max(limp.x,
					tile.x) - minx) + 1, (Math.max(limp.y, tile.y) - miny) + 1);

			// STAMP
			if (mouseButton == 3 && layer instanceof TileLayer) {
				// Right mouse button dragged: create and set custom brush
				TileLayer brushLayer = new TileLayer(bounds);
				brushLayer.copyFrom(getCurrentLayer());
				brushLayer.setOffset(tile.x - (int) bounds.width / 2, tile.y
						- (int) bounds.height / 2);

				// Do a quick check to make sure the selection is not empty
				if (brushLayer.isEmpty()) {
					MessageDialog.openInformation(getSite().getShell(),
					Resources.getString("dialog.selection.empty"),
					Resources.getString("dialog.selection.empty"));
				} else {
					setBrush(new CustomBrush(brushLayer));
					cursorHighlight.setOffset(tile.x - (int) bounds.width / 2,
							tile.y - (int) bounds.height / 2);
				}
			} else if (mouseButton == 1 && layer instanceof ObjectGroup) {
				// TODO: Fix this to use pixels in the first place
				// (with optional snap to grid)
				int w = currentMap.getTileWidth();
				int h = currentMap.getTileHeight();
				MapObject object = new MapObject(bounds.x * w, bounds.y * h,
						bounds.width * w, bounds.height * h);
				/*
				 * Point pos = mapView.screenToPixelCoords( event.x, event.y);
				 */
				ObjectGroup group = (ObjectGroup) layer;
				 addEdit(new AddObjectEdit(group, object));
				group.addObject(object);
				mapView.redraw();
			}

			// get rid of any visible marquee
			if (marqueeSelection != null) {
				currentMap.removeLayerSpecial(marqueeSelection);
				setMarqueeSelection(null);
			}
		}

		 if (paintEdit != null) {
		 if (layer != null) {
		 try {
		 MapLayer endLayer = paintEdit.getStart().createDiff(layer);
		 paintEdit.end(endLayer);
		 addEdit(paintEdit);
		 } catch (Exception e) {
		 e.printStackTrace();
		 }
		 }
		 paintEdit = null;
		 }

		currentObject = null;

		mouseButton = SWT.DEFAULT;
		bMouseIsDown = false;
		bMouseIsDragging = false;
	}
	
	/* (non-Javadoc)
	 * @see com.onpositive.mapper.editors.ILocalUndoSupport#addEdit(org.eclipse.core.commands.operations.AbstractOperation)
	 */
	@Override
	public void addEdit(AbstractOperation operation) {
		operation.addContext(undoContext);
		operationHistory.add(operation);
		setDirty(true);
	}

	/**
	 * Returns the currently selected layer.
	 * 
	 * @return the currently selected layer
	 */
	public MapLayer getCurrentLayer() {
		return currentMap.getLayer(currentLayer);
	}

	private void doMouse(MouseEvent event) {
		if (currentMap == null || currentLayer < 0) {
			return;
		}

		Point tile = mapView.screenToTileCoords(event.x, event.y);
		MapLayer layer = getCurrentLayer();

		if (layer == null) {
			return;
		} else if (mouseButton == 3) {
			if (layer instanceof TileLayer) {
				if (!bMouseIsDragging) {
					// Click event is sent before the drag event
					// so this one always happens
					Tile newTile = ((TileLayer) layer)
							.getTileAt(tile.x, tile.y);
					setCurrentTile(newTile);
				} else if (currentPointerState == PS_PAINT) {
					// In case we are dragging to create a custom brush, let
					// the user know where we are creating it from
					if (marqueeSelection == null) {
						setMarqueeSelection(new SelectionLayer(
								currentMap.getWidth(), currentMap.getHeight()));
						currentMap.addLayerSpecial(marqueeSelection);
					}

					Point limp = mouseInitialPressLocation;
					Rectangle oldArea = marqueeSelection
							.getSelectedAreaBounds();
					int minx = Math.min(limp.x, tile.x);
					int miny = Math.min(limp.y, tile.y);

					Rectangle selRect = new Rectangle(minx, miny, (Math.max(
							limp.x, tile.x) - minx) + 1, (Math.max(limp.y,
							tile.y) - miny) + 1);

					marqueeSelection.selectRegion(Converter
							.SWTRectToAWT(selRect));
					if (oldArea != null) {
						oldArea.add(marqueeSelection.getSelectedAreaBounds());
						mapView.repaintRegion(oldArea);
					}
				}
			} else if (layer instanceof ObjectGroup && !bMouseIsDragging) {
				// Get the object on this location and display the relative
				// options dialog
				ObjectGroup group = (ObjectGroup) layer;
				Point pos = mapView.screenToPixelCoords(event.x, event.y);
				MapObject obj = group.getObjectNear(pos.x, pos.y,
						mapView.getZoom());
				if (obj != null) {
					 ObjectPropertyDialog dialog = new ObjectPropertyDialog(getSite().getShell(), obj,
					 this);
					 dialog.open();
				}
			}
		} else if (mouseButton == 2
				|| (mouseButton == 1 && (event.stateMask & SWT.ALT) != 0)) {
			// Scroll with middle mouse button
			int dx = event.x - mouseInitialScreenLocation.x;
			int dy = event.y - mouseInitialScreenLocation.y;
			Point currentPosition = mapScrollView.getOrigin();
			// JViewport mapViewPort = mapScrollPane.getViewport();
			// Point currentPosition = mapViewPort.getViewPosition();
			mouseInitialScreenLocation = new Point(event.x - dx, event.y - dy);

			Point newPosition = new Point(currentPosition.x - dx,
					currentPosition.y - dy);

			// Take into account map boundaries in order to prevent
			// scrolling past them
			Point viewSize = mapView.getSize();
			Point viewportSize = mapScrollView.getSize();
			int maxX = viewSize.x - viewportSize.x;
			int maxY = viewSize.y - viewportSize.y;
			newPosition.x = Math.min(maxX, Math.max(0, newPosition.x));
			newPosition.y = Math.min(maxY, Math.max(0, newPosition.y));

			mapScrollView.setOrigin(newPosition);
			// mapViewPort.setViewPosition(newPosition);
		} else if (mouseButton == 1) {
			switch (currentPointerState) {
			case PS_PAINT:
				 paintEdit.setPresentationName(TOOL_PAINT); 
				if (layer instanceof TileLayer && canPaint(currentBrush)) {
					try {
						mapView.repaintRegion(currentBrush.doPaint(tile.x,
								tile.y));
						currentMap.fireMapChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case PS_ERASE:
				 paintEdit.setPresentationName(TOOL_ERASE);
				if (layer instanceof TileLayer) {
					((TileLayer) layer).setTileAt(tile.x, tile.y, null);
					mapView.repaintRegion(new Rectangle(tile.x, tile.y, 1, 1));
				}
				break;
			case PS_POUR:
				 paintEdit = null;
				if (layer instanceof TileLayer) {
					TileLayer tileLayer = (TileLayer) layer;
					Tile oldTile = tileLayer.getTileAt(tile.x, tile.y);
					pour(tileLayer, tile.x, tile.y, currentTile, oldTile);
					mapView.redraw();
				}
				break;
			case PS_EYED:
				if (layer instanceof TileLayer) {
					TileLayer tileLayer = (TileLayer) layer;
					Tile newTile = tileLayer.getTileAt(tile.x, tile.y);
					setCurrentTile(newTile);
				}
				break;
			case PS_MOVE: {
				Point translation = new Point(tile.x - mousePressLocation.x,
						tile.y - mousePressLocation.y);

				layer.translate(translation.x, translation.y);
				mousePressLocation = tile;
				moveDist.x += translation.x;
				moveDist.y += translation.y;
				mapView.redraw();
				break;
			}
			case PS_MARQUEE:
				if (!(layer instanceof TileLayer)) {
					break;
				}
				if (marqueeSelection != null) {
					Point limp = mouseInitialPressLocation;
					Rectangle oldArea = marqueeSelection
							.getSelectedAreaBounds();
					int minx = Math.min(limp.x, tile.x);
					int miny = Math.min(limp.y, tile.y);

					Rectangle selRect = new Rectangle(minx, miny, (Math.max(
							limp.x, tile.x) - minx) + 1, (Math.max(limp.y,
							tile.y) - miny) + 1);

					if ((event.stateMask & SWT.SHIFT) > 0) {
						marqueeSelection.add(new Area(Converter
								.SWTRectToAWT(selRect)));
					} else if ((event.stateMask & SWT.CONTROL) > 0) {
						marqueeSelection.subtract(new Area(Converter
								.SWTRectToAWT(selRect)));
					} else {
						marqueeSelection.selectRegion(Converter
								.SWTRectToAWT(selRect));
					}
					if (oldArea != null) {
						oldArea.add(marqueeSelection.getSelectedAreaBounds());
						mapView.repaintRegion(oldArea);
					}
				}
				break;
			case PS_ADDOBJ:
				if (layer instanceof ObjectGroup) {
					if (marqueeSelection == null) {
						setMarqueeSelection(new SelectionLayer(
								currentMap.getWidth(), currentMap.getHeight()));
						currentMap.addLayerSpecial(marqueeSelection);
					}

					Point limp = mouseInitialPressLocation;
					Rectangle oldArea = marqueeSelection
							.getSelectedAreaBounds();
					int minx = Math.min(limp.x, tile.x);
					int miny = Math.min(limp.y, tile.y);

					Rectangle selRect = new Rectangle(minx, miny, (Math.max(
							limp.x, tile.x) - minx) + 1, (Math.max(limp.y,
							tile.y) - miny) + 1);

					marqueeSelection.selectRegion(Converter
							.SWTRectToAWT(selRect));
					if (oldArea != null) {
						oldArea.add(marqueeSelection.getSelectedAreaBounds());
						mapView.repaintRegion(oldArea);
					}
				}
				break;
			case PS_REMOVEOBJ:
				if (layer instanceof ObjectGroup) {
					ObjectGroup group = (ObjectGroup) layer;
					Point pos = mapView.screenToPixelCoords(event.x, event.y);
					MapObject obj = group.getObjectNear(pos.x, pos.y,
							mapView.getZoom());
					if (obj != null) {
						// addEdit(new RemoveObjectEdit(group,
						// obj));
						group.removeObject(obj);
						// TODO: repaint only affected area
						mapView.redraw();
					}
				}
				break;
			case PS_MOVEOBJ:
				if (layer instanceof ObjectGroup) {
					Point pos = mapView.screenToPixelCoords(event.x, event.y);
					if (currentObject == null) {
						ObjectGroup group = (ObjectGroup) layer;
						currentObject = group.getObjectNear(pos.x, pos.y,
								mapView.getZoom());
						if (currentObject == null) { // No object to move
							break;
						}
						mouseLastPixelLocation = pos;
						moveDist = new Point(0, 0);
						break;
					}
					Point translation = new Point(pos.x
							- mouseLastPixelLocation.x, pos.y
							- mouseLastPixelLocation.y);
					currentObject.translate(translation.x, translation.y);
					moveDist.x += translation.x;
					moveDist.y += translation.y;
					mouseLastPixelLocation = pos;
					mapView.redraw();
				}
				break;
			}
		}
	}
	
	protected boolean canPaint(AbstractBrush brush) {
		if (brush  instanceof ShapeBrush && ((ShapeBrush) brush).getTile() == null)
			return false;
		return true;
	}

	private void pour(TileLayer layer, int x, int y, Tile newTile, Tile oldTile) {
		if (newTile == oldTile || !layer.canEdit())
			return;

		Rectangle area;
		TileLayer before = (TileLayer) createLayerCopy(layer);
		TileLayer after;

		// Check that the copy was succesfully created
		if (before == null) {
			return;
		}

		if (marqueeSelection == null) {
			area = new Rectangle(x, y, 0, 0);
			Stack<Point> stack = new Stack<Point>();

			stack.push(new Point(x, y));
			while (!stack.empty()) {
				// Remove the next tile from the stack
				Point p = stack.pop();

				// If the tile it meets the requirements, set it and push its
				// neighbouring tiles on the stack.
				if (layer.contains(p.x, p.y)
						&& layer.getTileAt(p.x, p.y) == oldTile) {
					layer.setTileAt(p.x, p.y, newTile);
					Converter.add(area, p.x, p.y);

					stack.push(new Point(p.x, p.y - 1));
					stack.push(new Point(p.x, p.y + 1));
					stack.push(new Point(p.x + 1, p.y));
					stack.push(new Point(p.x - 1, p.y));
				}
			}
		} else {
			if (marqueeSelection.getSelectedArea().contains(x, y)) {
				area = marqueeSelection.getSelectedAreaBounds();
				for (int i = area.y; i < area.height + area.y; i++) {
					for (int j = area.x; j < area.width + area.x; j++) {
						if (marqueeSelection.getSelectedArea().contains(j, i)) {
							layer.setTileAt(j, i, newTile);
						}
					}
				}
			} else {
				return;
			}
		}

		Rectangle bounds = new Rectangle(area.x, area.y, area.width + 1,
				area.height + 1);
		after = new TileLayer(bounds);
		after.copyFrom(layer);

		 MapLayerEdit mle = new MapLayerEdit(layer, before, after); 
		 mle.setPresentationName(TOOL_FILL);
		 addEdit(mle);
	}

	public void mouseMoved(MouseEvent e) {
		// Update state of mouse buttons
		bMouseIsDown = e.button > 0;
		if (bMouseIsDragging) { // Was bMouseIsDown
			doMouse(e);
		}
//		if (bMouseIsDown)
//			mousePressLocation = mapView.screenToTileCoords(e.x, e.y);

		Point tile = mapView.screenToTileCoords(e.x, e.y);
		updateTileCoordsLabel(tile);
		updateCursorHighlight(tile);
	}

	public void mouseDragged(MouseEvent e) {
		bMouseIsDragging = true;

		doMouse(e);

		mousePressLocation = mapView.screenToTileCoords(e.x, e.y);
		Point tile = mapView.screenToTileCoords(e.x, e.y);

		updateTileCoordsLabel(tile);
		updateCursorHighlight(tile);
	}

	private static MapLayer createLayerCopy(MapLayer layer) {
		try {
			return (MapLayer) layer.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void updateTileCoordsLabel(Point tile) {
		if (currentMap.inBounds(tile.x, tile.y)) {
			statusLineManager
					.setMessage(String.valueOf(tile.x) + ", " + tile.y);
		} else {
			statusLineManager.setMessage("");
		}
	}

	private void updateCursorHighlight(Point tile) {
		if (prefs.getBoolean("cursorhighlight", true)) {
			Rectangle redraw = cursorHighlight.getBounds();
			Rectangle brushRedraw = currentBrush.getBounds();

			brushRedraw.x = tile.x - brushRedraw.width / 2;
			brushRedraw.y = tile.y - brushRedraw.height / 2;

			if (!redraw.equals(brushRedraw)) {
				if (currentBrush instanceof CustomBrush) {
					CustomBrush customBrush = (CustomBrush) currentBrush;
					ListIterator<MapLayer> layers = customBrush.getLayers();
					while (layers.hasNext()) {
						MapLayer layer = layers.next();
						layer.setOffset(brushRedraw.x, brushRedraw.y);
					}
				}
				if (currentBrush instanceof ITileBrush)
					mapView.repaintRegion(redraw, ((ITileBrush) currentBrush).getTile().getSize());
				else
					mapView.repaintRegion(redraw);
				cursorHighlight.setOffset(brushRedraw.x, brushRedraw.y);
				// cursorHighlight.selectRegion(currentBrush.getShape());
				mapView.repaintRegion(brushRedraw);
			}
		}
	}

	public void setCurrentMap(Map newMap) {
		// Cancel any active selection
		if (marqueeSelection != null && currentMap != null) {
			currentMap.removeLayerSpecial(marqueeSelection);
		}
		marqueeSelection = null;

		currentMap = newMap;
//		boolean mapLoaded = currentMap != null;

		// Create a default brush (protect against a bug with custom brushes)
		ShapeBrush sb = new ShapeBrush();
		sb.makeQuadBrush(new Rectangle(0, 0, 1, 1));
		setBrush(sb);

		// Get the first non-null tile from the first tileset containing
		// non-null tiles.
		Vector<TileSet> tilesets = currentMap.getTilesets();
		Tile firstTile = null;
		if (!tilesets.isEmpty()) {
			Iterator<TileSet> it = tilesets.iterator();
			while (it.hasNext() && firstTile == null) {
				firstTile = it.next().getFirstTile();
			}
		}
		setCurrentTile(firstTile);
		setCurrentPointerState(PS_PAINT);

		currentMap.addLayerSpecial(cursorHighlight);
		updateLayerInfo();
	}

	public void setCurrentLayer(int index) {
		if (currentMap != null) {
			int totalLayers = currentMap.getTotalLayers();
			if (totalLayers > index && index >= 0) {
				/*
				 * if (paintEdit != null) { MapLayer layer = getCurrentLayer();
				 * try { MapLayer endLayer =
				 * paintEdit.getStart().createDiff(layer); if (endLayer != null)
				 * { endLayer.setId(layer.getId());
				 * endLayer.setOffset(layer.getBounds().x,layer.getBounds().y);
				 * } paintEdit.end(endLayer); addEdit(paintEdit); }
				 * catch (Exception e) { e.printStackTrace(); } }
				 */
				int oldCurLayer = currentLayer;
				currentLayer = index;
				firePartPropertyChanged(CURRENT_LAYER_PROP, "" + oldCurLayer,
						"" + currentLayer);
				mapView.setCurrentLayer(currentLayer);
				// layerTable.changeSelection(totalLayers - currentLayer - 1, 0,
				// false, false);
			}
		}
	}

	/**
	 * Returns the currently selected layer index.
	 * 
	 * @return the currently selected layer index
	 */
	public int getCurrentLayerIndex() {
		return currentLayer;
	}
	
    public void setCurrentPointerState(int state) {
        /*
        if (currentPointerState == PS_MARQUEE && state != PS_MARQUEE) {
            // Special logic for selection
            if (marqueeSelection != null) {
                currentMap.removeLayerSpecial(marqueeSelection);
                marqueeSelection = null;
            }
        }
        */
    	if (currentPointerState == state || !isValidNewState(state))
    		return;

    	int oldState = currentPointerState;
    	currentPointerState = state;
        mapView.setCurrentPointerState(state);
        if (needBrushReset(state))
        	resetBrush();
        firePartPropertyChanged(POINTER_STATE_PROP,"" + oldState,"" + currentPointerState);

        // Select the matching button  //TODO
//        paintButton.setSelected(state == PS_PAINT);
//        eraseButton.setSelected(state == PS_ERASE);
//        pourButton.setSelected(state == PS_POUR);
//        eyedButton.setSelected(state == PS_EYED);
//        marqueeButton.setSelected(state == PS_MARQUEE);
//        moveButton.setSelected(state == PS_MOVE);
//        objectAddButton.setSelected(state == PS_ADDOBJ);
//        objectRemoveButton.setSelected(state == PS_REMOVEOBJ);
//        objectMoveButton.setSelected(state == PS_MOVEOBJ);

        // Set the matching cursor //TODO
//        if (mapView != null) {
//            switch (currentPointerState) {
//                case PS_PAINT:
//                case PS_ERASE:
//                case PS_POINT:
//                case PS_POUR:
//                case PS_MARQUEE:
//                    mapView.setCursor(curDefault);
//                    break;
//                case PS_EYED:
//                    mapView.setCursor(curEyed);
//                    break;
//            }
//        }
    }
    
    protected boolean needBrushReset(int state) {
		return state != PS_PAINT;
	}

	private void updateLayerInfo() {
        int cl = currentLayer;
      
        if (currentMap != null) {
            if (currentMap.getTotalLayers() > 0 && cl == -1) {
                cl = 0;
            }
            setCurrentLayer(cl);
        }

    }
    
	@Override
	public void tilesetsSwapped(MapChangedEvent e, int index0,
			int index1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void tilesetRemoved(MapChangedEvent e, int index) {
		mapView.redraw();
	}

	@Override
	public void tilesetAdded(MapChangedEvent e, TileSet tileset) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mapChanged(MapChangedEvent e) {
		updateLayerInfo();
		mapView.redraw();
		if (!init)
			setDirty(true);
	}

	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		firePropertyChange(PROP_DIRTY);
	}

	public int getCurrentPointerState() {
		return currentPointerState;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		undoAction.dispose();
		redoAction.dispose();
	}

	private boolean isValidNewState(int state) {
		if (isObjectLayerActive() && (state == PS_PAINT || state == PS_MOVE || state == PS_ERASE))
			return false;
		if (!isObjectLayerActive() && (state == PS_ADDOBJ || state == PS_MOVEOBJ || state == PS_REMOVEOBJ))
			return false;
		return true;
	}

	public boolean isObjectLayerActive() {
		return getCurrentLayer() instanceof ObjectGroup;
	}

	public void copySelection() {
		if (currentMap != null && marqueeSelection != null) {
            if (getCurrentLayer() instanceof TileLayer) {
                clipboardLayer = new TileLayer(
                        marqueeSelection.getSelectedAreaBounds());
            } else if (getCurrentLayer() instanceof ObjectGroup) {
                clipboardLayer = new ObjectGroup(
                        marqueeSelection.getSelectedAreaBounds());
            }
            clipboardLayer.maskedCopyFrom(
                    getCurrentLayer(),
                    marqueeSelection.getSelectedArea());
            firePartPropertyChanged(CLIPBOARD_CONTENT_PROP,"","");
        }
		
	}
	
	public void copyAll() {
		//FIXME: only works for TileLayers
        if (currentMap != null && marqueeSelection != null) {
            clipboardLayer = new TileLayer(
                    marqueeSelection.getSelectedAreaBounds());
            ListIterator<?> itr = currentMap.getLayers();
            while(itr.hasNext()) {
                MapLayer layer = (MapLayer) itr.next();
                if (layer instanceof TileLayer) {
                    clipboardLayer.maskedMergeOnto(
                            layer,
                            marqueeSelection.getSelectedArea());
                }
            }
        }
	}
	
	public void paste() {
		if (clipboardLayer instanceof TileLayer) {
			setBrush(new CustomBrush((TileLayer)clipboardLayer));
			setCurrentPointerState(PS_PAINT);
		}
	}
	
	protected void setMarqueeSelection(SelectionLayer marqueeSelection) {
		this.marqueeSelection = marqueeSelection;
		firePartPropertyChanged(MARQEE_SELECTION_PROP,"","");
	}

	public boolean hasSelection() {
		return marqueeSelection != null;
	}
	
	public boolean hasClipboardData() {
		return clipboardLayer != null;
	}
	
	public void setShowGrid(boolean showGrid) {
		mapView.setShowGrid(showGrid);
		mapView.redraw();
	}

	public void setHighlightCurrentLayer(boolean hightlight) {
		mapView.setHighlightSelectedLayer(hightlight);
		mapView.redraw();
	}

	public void selectNextLayer() {
		if (currentMap.getTotalLayers() < 2)
			return;
		if (currentLayer == 0) 
			setCurrentLayer(currentMap.getTotalLayers() - 1);
		else
			setCurrentLayer(currentLayer - 1);
	}
	
	public void selectPrevLayer() {
		if (currentMap.getTotalLayers() < 2)
			return;
		if (currentLayer == currentMap.getTotalLayers() - 1) 
			setCurrentLayer(0);
		else
			setCurrentLayer(currentLayer + 1);
	}
	
	
}
