package com.onpositive.mapper.dialogs;

import java.util.Comparator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import tiled.core.MapObject;
import tiled.mapeditor.undo.ChangeObjectEdit;

import com.onpositive.mapper.editors.ILocalUndoSupport;
import com.onpositive.mapper.views.DblClickActivationStrategy;

public class ObjectPropertyDialog extends Dialog {

	private static final String NAME_PROP = "Name";
	private static final String VALUE_PROP = "Value";

	public class ViewContentProvider implements IStructuredContentProvider, ICellModifier {

		private MapObject mapObject;

		@Override
		public void dispose() {
			// Do nothing
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof MapObject) {
				this.mapObject = (MapObject) newInput;
			}
		}

		@Override
		public boolean canModify(Object element, String property) {
			return true;
		}

		@Override
		public Object getValue(Object element, String property) {
			if (element instanceof KeyValue) {
				if (property.equals(NAME_PROP))
					return ((KeyValue) element).getKey();
				if (property.equals(VALUE_PROP))
					return ((KeyValue) element).getValue();
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			if (element instanceof TableItem) {
				element = ((TableItem) element).getData();
			} 
			if (element instanceof KeyValue) {
				Properties properties = mapObject.getProperties();
				KeyValue keyValue = (KeyValue) element;
				if (property.equals(NAME_PROP)) {
					properties.remove(keyValue.getKey());
					keyValue.setKey(value.toString());
					properties.put(keyValue.getKey(),keyValue.getValue());
				} else if (property.equals(VALUE_PROP)) {
					keyValue.setValue(value.toString());
					properties.put(keyValue.getKey(),keyValue.getValue());
				}
			}
			viewer.update(element,new String[]{property});
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof MapObject) {
				Properties properties = ((MapObject) inputElement).getProperties();
				Set<Object> keySet = properties.keySet();
				KeyValue[] result = new KeyValue[keySet.size()];
				int i = 0;
				for (Object object : keySet) {
					result[i++] = new KeyValue(object.toString(),properties.getProperty(object.toString()));
				}
				return result;
			}
			return new KeyValue[0];
		}

	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof KeyValue) {
				if (columnIndex == 0)
					return ((KeyValue) element).getKey();
				if (columnIndex == 1)
					return ((KeyValue) element).getValue();
			}
			return null;
		}

	}
	
	private static final int DIALOG_WIDTH = 250;
	private static final int DIALOG_HEIGHT = 400;
	private final MapObject mapObject;
	private TableViewer viewer;
	private final ILocalUndoSupport undoSupport;
	private Text nameText;
	private Text typeText;
	private Spinner widthSpinner;
	private Spinner heightSpinner;

	public ObjectPropertyDialog(Shell parentShell, MapObject mapObject, ILocalUndoSupport undoSupport) {
		super(parentShell);
		this.mapObject = mapObject;
		this.undoSupport = undoSupport;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setMinimumSize(DIALOG_WIDTH,DIALOG_HEIGHT);
		getShell().setText("Object properties");
		Composite composite = new Composite(parent,SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		composite.setLayout(new GridLayout(2,false));
		
		createLabel(composite,"Name:");
		nameText = new Text(composite,SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		
		createLabel(composite,"Type:");
		typeText = new Text(composite,SWT.SINGLE | SWT.BORDER);
		typeText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		
		createLabel(composite,"Width:");
		widthSpinner = new Spinner(composite,SWT.SINGLE | SWT.BORDER);
		widthSpinner.setMinimum(0);
		widthSpinner.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));

		createLabel(composite,"Height:");
		heightSpinner = new Spinner(composite,SWT.SINGLE | SWT.BORDER);
		heightSpinner.setMinimum(0);
		heightSpinner.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));

		final Table table = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);
		table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
		
		TableLayout layout = new TableLayout();
	    layout.addColumnData(new ColumnWeightData(50, 75, true));
	    layout.addColumnData(new ColumnWeightData(50, 75, true));
	    table.setLayout(layout);
		
		parent.setBounds(parent.getClientArea());
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer = new TableViewer(table);
		TableColumn nameColumn = new TableColumn(table,SWT.LEFT);
		nameColumn.setText("name");
		nameColumn.setWidth(100);
		TableColumn valueColumn = new TableColumn(table,SWT.LEFT);
		valueColumn.setText("value");
		
		
		ViewContentProvider provider = new ViewContentProvider();
		viewer.setContentProvider(provider);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setCellModifier(provider);
		updateViewer();
		viewer.setColumnProperties(new String[]{NAME_PROP,VALUE_PROP}); 
		viewer.setCellEditors(new CellEditor[]{new TextCellEditor(table),new TextCellEditor(table)});
		TableViewerEditor.create(viewer,new DblClickActivationStrategy(viewer),TreeViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR);
		viewer.setComparator(new ViewerComparator());
		table.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// Do nothing
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// Do nothing
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (table.getItem(new Point(e.x,e.y)) == null) { //Create new item
					mapObject.getProperties().put("","");
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							viewer.setInput(mapObject);
							TableItem lastItem = table.getItem(table.getItemCount() - 1);
							viewer.editElement(lastItem.getData(),0);
						}
					});
				}
				
			}
		});

		updateInfo();
		return composite;
	}
	
	protected void updateViewer() {
		viewer.setInput(mapObject);
	}
	

	

	protected void createLabel(Composite composite, String text) {
		Label label = new Label(composite,SWT.NONE);
		label.setText(text);
	}
	
	@Override
	protected void okPressed() {
		buildProperties();
		super.okPressed();
	}
	
    public void updateInfo() {
        nameText.setText(mapObject.getName());
        typeText.setText(mapObject.getType());
//        objectImageSource.setText(object.getImageSource());
        widthSpinner.setSelection(mapObject.getWidth());
        heightSpinner.setSelection(mapObject.getHeight());
    }

    protected void buildProperties() {
        // Make sure the changes to the object can be undone
        undoSupport.addEdit(new ChangeObjectEdit(mapObject));

        mapObject.setName(nameText.getText());
        mapObject.setType(typeText.getText());
//        mapObject.setImageSource(objectImageSource.getText());
        mapObject.setWidth(widthSpinner.getSelection());
        mapObject.setHeight(heightSpinner.getSelection());
    }

}
