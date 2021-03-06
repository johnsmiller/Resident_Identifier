package com.sjsurha.resident_identifier;

import com.sjsurha.resident_identifier.TreeSet_TableModel_ComboBoxModel.TableModel_ComboModel_Interface;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author John Miller
 */
/**
 * TreeSet<Model.Event> class that implements the TableModel and ComboBoxModel interfaces
 */

public class TreeSet_TableModel_ComboBoxModel<T1 extends TableModel_ComboModel_Interface> extends TreeSet<T1> implements TableModel, ComboBoxModel<T1>
{
    private static final long serialVersionUID = 1L;
    private final String[] headers;
    private boolean[] tableBooleanSelection;
    private static HashSet<TableModelListener> tableModelListeners;
    private static HashSet<ListDataListener> comboBoxModelListDataListeners;
    Object[] modelArray;
    private Object comboBoxModelSelectedItem;

    public TreeSet_TableModel_ComboBoxModel()
    {
        super();
        tableBooleanSelection = new boolean[super.size()];
        tableModelListeners = new HashSet<>(5);
        comboBoxModelListDataListeners = new HashSet<ListDataListener>(10);
        modelArray = this.toArray(new Object[super.size()]);
        headers = new String[0];
    }
    
    public TreeSet_TableModel_ComboBoxModel(String[] Headers)
    {
        super();
        tableBooleanSelection = new boolean[super.size()];
        tableModelListeners = new HashSet<>(5);
        comboBoxModelListDataListeners = new HashSet<ListDataListener>(10);
        modelArray = this.toArray(new Object[super.size()]);
        headers = Headers;
    }
    
    static 
    {
        tableModelListeners = new HashSet<>(5);
        comboBoxModelListDataListeners = new HashSet<ListDataListener>(10);
    }

    /**
     * Utilizes add function of TreeSet. If successful, selected item for combo-
     * box implementation is updated to item just added. Event will not be added
     * if a duplicate event is found
     * 
     * Model is saved to file after Event is added
     * 
     * @param o the event to be added to this implementation
     * @return true if item successfully added (TreeSet parent class' add
     * function returns true). False otherwise.
     */
    @Override
    public boolean add(T1 o)  //Modify to take in Authenication ID for future logging function
    {
        if(super.add(o)) {
            comboBoxModelSelectedItem = o;
            modelListenersNotify();
            ViewerController.saveModel();
            return true;
        }
        return false;
    }
    
    public void updateBoolean(boolean[] bools)
    {
        if(bools.length == size()) {
            tableBooleanSelection = bools;
            notifyTableModelBooleanChange();
        }
    }
    
    
    /**
     * Utilizes TreeSet parent's remove function. If it returns true, item is 
     * removed and, if item is selected in combobox model, selected model is 
     * updated to first item if size > 0, null otherwise.
     * 
     * Model is saved after item is removed
     * 
     * @param o the object to be removed from the set
     * @return true if parent class successfully removes the object. false 
     * otherwise
     */

    @Override
    public boolean remove(Object o) //Modify to take in Authenication ID for future logging function
    {
        if(super.remove(o)){
            if(comboBoxModelSelectedItem.equals(o))
                comboBoxModelSelectedItem = ((this.size()>0)? this.first() : null);
            modelListenersNotify();
            ViewerController.saveModel();
            return true;
        }
        return false;
    }
    
    /**
     * Utilizes TreeSet parent's addAll function. If parent function returns
     * true, the set has changed and, as a result, selected item is updated to 
     * first item in list and the model is saved to file. 
     * 
     * @param c Collection of events to be added to model
     * @return true if the set has changed as a result of this call, false if 
     * otherwise
     */

    @Override
    public boolean addAll(Collection<? extends T1> c) //Modify to take in Authenication ID for future logging function
    {
        if(super.addAll(c)){
            comboBoxModelSelectedItem = this.first();
            modelListenersNotify();
            ViewerController.saveModel();
            return true;
        }
        return false;  
    }

    /**
     * Call's TreeSet parent's clear() function, sets the combobox selected
     * item to null, and notifies the model listeners
     */

    @Override
    public void clear() //Modify to take in Authenication ID for future logging function
    {
        super.clear();
        comboBoxModelSelectedItem = null;
        modelListenersNotify();
    }

    /*
     * Ensures all implemented interfaces' listeners are notified if model data changes
     */

    private void modelListenersNotify()
    {
        modelArray = this.toArray(new Object[this.size()]);
        notifyTableModelChange();
        notifyComboBoxModelChange(0, this.size()-1);
    }

    /**
     * Ensures all implemented interfaces' listeners are notified if model data changes
     * @param index0 beginning index of change
     * @param index1 ending index of change
     */

    private void modelListenersNotify(int index0, int index1)
    {
        modelArray = this.toArray(new Object[this.size()]);
        notifyTableModelChange(index0, index1);
        notifyComboBoxModelChange(index0, index1);

    }


    /**
     * Removes all model listeners to ensure clean program exit 
     * (no additional threads running)
     */

    public void removeAllListeners()
    {
        tableModelListeners.clear();
        comboBoxModelListDataListeners.clear();
    }



    //TABLE MODEL IMPLEMENTATION

    private void notifyTableModelChange()
    {
        tableBooleanSelection = new boolean[super.size()];
        for(TableModelListener t : tableModelListeners)
            t.tableChanged(new TableModelEvent(this));
    }

    private void notifyTableModelChange(int index0, int index1)
    {
        tableBooleanSelection = new boolean[super.size()];
        for(TableModelListener t : tableModelListeners)
            t.tableChanged(new TableModelEvent(this, index0, index1));
    }
    
    private void notifyTableModelBooleanChange()
    {
        for(TableModelListener t : tableModelListeners)
            t.tableChanged(new TableModelEvent(this));
    }

    @Override
    public int getRowCount() {
        return this.size();

    }

    @Override
    public int getColumnCount() {
        return headers.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if(columnIndex>-1 && columnIndex < getColumnCount())
            return headers[columnIndex];
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(getValueAt(0, columnIndex) == null )
            return Object.class;
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(this.size() == 0)
            return null;
        
        switch (columnIndex)
        {
            case 0:
                return tableBooleanSelection[rowIndex];
            default:
                return ((T1)modelArray[rowIndex]).getColumn(columnIndex);
        }
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex == 0)
            tableBooleanSelection[rowIndex] = (boolean) aValue;
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        tableModelListeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        tableModelListeners.remove(l);
    }

    //COMBOBOX MODEL IMPLEMENTATION

    /**
     * used by ComboBoxModel class to notify of data changes
     * @param index0 beginning index of changes
     * @param index1 ending index of changes
     */
    private void notifyComboBoxModelChange(int index0, int index1)
    {
        for(ListDataListener l : comboBoxModelListDataListeners)
            l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1));
    }

    @Override
    public void setSelectedItem(Object anItem) {
        comboBoxModelSelectedItem = anItem;
        notifyComboBoxModelChange(0, getSize()-1); //Should this be the indexes of selected/deselected?
    }

    @Override
    public Object getSelectedItem() {
        return comboBoxModelSelectedItem;
    }

    @Override
    public int getSize() {
        return this.size();
    }

    @Override
    public T1 getElementAt(int index) {
        if(index < modelArray.length)
            return (T1)modelArray[index];
        return null;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        comboBoxModelListDataListeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        comboBoxModelListDataListeners.remove(l);
    }
    
    public interface TableModel_ComboModel_Interface{
        public Object getColumn(int i);
    }

}

