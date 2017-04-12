package com.jhw.adm.client.swing;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableColumnModelListener;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.plaf.ColumnControlButtonAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.table.ColumnControlPopup;

import com.jhw.adm.client.core.Cleanable;

/**
 * reference org.jdesktop.swingx.table.ColumnControlButton
 */
public class RowControlButton extends JButton {
    
	private static final long serialVersionUID = 2130278222386396566L;

	// JW: really want to extend? for builders?
    /** Marker to auto-recognize actions which should be added to the popup. */
    public static final String COLUMN_CONTROL_MARKER = "column.";
    
    /** the key for looking up the control's icon in the UIManager. Typically, it's LAF dependent. */
    public static final String COLUMN_CONTROL_BUTTON_ICON_KEY = "ColumnControlButton.actionIcon";
    
    /** the key for looking up the control's margin in the UIManager. Typically, it's LAF dependent. */
    public static final String COLUMN_CONTROL_BUTTON_MARGIN_KEY = "ColumnControlButton.margin";
    static {
        LookAndFeelAddons.contribute(new ColumnControlButtonAddon());
    }

    protected ColumnControlPopup popup;
    private JXTable table;
    /** Listener for table property changes. */
    private PropertyChangeListener tablePropertyChangeListener;
    /** Listener for table's columnModel. */
    TableColumnModelListener columnModelListener;
    /** the list of actions for column menuitems.*/
    private List<AbstractActionExt> rowActions;
    private final Cleanable model;

    
    /**
     * Creates a column control button for the table. Uses the default
     * icon as provided by the addon.
     * 
     * @param table  the <code>JXTable</code> controlled by this component
     */
    public RowControlButton(Cleanable model) {
        this(model, null);
    }

    /**
     * Creates a column control button for the table. The button
     * uses the given icon and has no text.
     * @param table  the <code>JXTable</code> controlled by this component
     * @param icon the <code>Icon</code> to show
     */
    public RowControlButton(final Cleanable model, Icon icon) {
        super();
        init();
        // JW: icon LF dependent?
        setAction(createControlAction(icon));
        updateActionUI();
        updateButtonUI();
        this.model = model;
    }

    
    @Override
    public void updateUI() {
        super.updateUI();
        // JW: icon may be LF dependent
        updateActionUI();
        updateButtonUI();
        getColumnControlPopup().updateUI();
    }

    /**
     * Updates this button's properties provided by the LAF.
     * Here: overwrites the action's small_icon with the icon from the ui if the current
     *   icon is null or a UIResource.
     */
    protected void updateButtonUI() {
        if ((getMargin() == null) || (getMargin() instanceof UIResource)) {
            Insets insets = UIManager.getInsets(COLUMN_CONTROL_BUTTON_MARGIN_KEY);
            setMargin(insets); 
        }
    }
    
    /**
     * Updates the action properties provided by the LAF.
     * Here: overwrites the action's small_icon with the icon from the ui if the current
     *   icon is null or a UIResource.
     */
    protected void updateActionUI() {
        if (getAction() == null) return;
        Icon icon = (Icon) getAction().getValue(Action.SMALL_ICON);
        if ((icon == null) || (icon instanceof UIResource)) {
            icon = UIManager.getIcon(COLUMN_CONTROL_BUTTON_ICON_KEY);
            getAction().putValue(Action.SMALL_ICON, icon);
        }
    }

    /** 
     * Toggles the popup component's visibility. This method is
     * called by this control's default action. <p>
     * 
     * Here: delegates to getControlPopup().
     */ 
    public void togglePopup() {
        getColumnControlPopup().toggleVisibility(this);
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
        getColumnControlPopup().applyComponentOrientation(o);
    }

    // ---------------------- the popup

    public class RowDeletionPopup implements ColumnControlPopup {
        private JPopupMenu popupMenu;

        //------------------ public methods to control visibility status
        
        /** 
         * @inheritDoc
         * 
         */
        public void updateUI() {
            SwingUtilities.updateComponentTreeUI(getPopupMenu());
        }

        /** 
         * @inheritDoc
         * 
         */
        public void toggleVisibility(JComponent owner) {
        	populatePopup();
            JPopupMenu popupMenu = getPopupMenu();
            if (popupMenu.isVisible()) {
                popupMenu.setVisible(false);
            } else if (popupMenu.getComponentCount() > 0) {
                Dimension buttonSize = owner.getSize();
                int xPos = owner.getComponentOrientation().isLeftToRight() ? buttonSize.width
                        - popupMenu.getPreferredSize().width
                        : 0;
                popupMenu.show(owner,
                        // JW: trying to allow popup without CCB showing
                        // weird behaviour
//                        owner.isShowing()? owner : null, 
                        xPos, buttonSize.height);
            }

        }

        /** 
         * @inheritDoc
         * 
         */
        public void applyComponentOrientation(ComponentOrientation o) {
            getPopupMenu().applyComponentOrientation(o);

        }

        //-------------------- public methods to manipulate popup contents.
        
        /** 
         * @inheritDoc
         * 
         */
        public void removeAll() {
            getPopupMenu().removeAll();
        }


        /** 
         * @inheritDoc
         * 
         */
        public void addVisibilityActionItems(
                List<? extends AbstractActionExt> actions) {
            addItems(new ArrayList<Action>(actions));

        }


        /** 
         * @inheritDoc
         * 
         */
        public void addAdditionalActionItems(List<? extends Action> actions) {
            if (actions.size() == 0) return;
            
            addItems(actions);
        }
        
        //--------------------------- internal helpers to manipulate popups content
        
        /**
         * Here: creates and adds a menuItem to the popup for every 
         * Action in the list. Does nothing if 
         * if the list is empty.
         * 
         * PRE: actions != null.
         * 
         * @param actions a list containing the actions to add to the popup.
         *        Must not be null.
         * 
         */
        protected void addItems(List<? extends Action> actions) {
            ActionContainerFactory factory = new ActionContainerFactory(null);
            for (Action action : actions) {
                addItem(factory.createMenuItem(action));
            }

        }
        
        /**
         * adds a separator to the popup.
         *
         */
        protected void addSeparator() {
            getPopupMenu().addSeparator();
        }

        /**
         * 
         * @param item the menuItem to add to the popup.
         */
        protected void addItem(JMenuItem item) {
            getPopupMenu().add(item);
        }

        /**
         * 
         * @return the popup to add menuitems. Guaranteed to be != null.
         */
        protected JPopupMenu getPopupMenu() {
            if (popupMenu == null) {
                popupMenu = new JPopupMenu();
            }
            return popupMenu;
        }

    }


    /**
     * Returns to popup component for user interaction. Lazily 
     * creates the component if necessary.
     * 
     * @return the ColumnControlPopup for showing the items, guaranteed
     *   to be not <code>null</code>.
     * @see #createColumnControlPopup()  
     */
    protected ColumnControlPopup getColumnControlPopup() {
        if (popup == null) {
            popup = createColumnControlPopup();
        }
        return popup;
    }

    /**
     * Factory method to return a <code>ColumnControlPopup</code>.
     * Subclasses can override to hook custom implementations.
     * 
     * @return the <code>ColumnControlPopup</code> used.
     */
    protected ColumnControlPopup createColumnControlPopup() {
        return new RowDeletionPopup();
    }


//-------------------------- updates from table propertyChangelistnere
       
    /**
     * Synchs this button's enabled with table's enabled.
     *
     */
    protected void updateFromTableEnabledChanged() {
        getAction().setEnabled(table.isEnabled());
        
    }
 
//  ------------------------ updating the popup

    protected void populatePopup() {
        clearAll();
        createRowActions();
        addRowItems();
    }

    /**
     * 
     * removes all components from the popup, making sure to release all
     * columnVisibility actions.
     * 
     */
    protected void clearAll() {
        clearRowActions();
        getColumnControlPopup().removeAll();
    }


    /**
     * Releases actions and clears list of actions.
     * 
     */
    protected void clearRowActions() {
        if (rowActions == null) return;
        
        rowActions.clear();
    }

   
    /**
     * Adds visibility actions into the popup view.
     * 
     * Here: delegates the list of actions to the DefaultColumnControlPopup.
     * <p>
     * PRE: columnVisibilityActions populated before calling this.
     * 
     */
    protected void addRowItems() {
        getColumnControlPopup().addVisibilityActionItems(
                Collections.unmodifiableList(getRowActions()));
    }

    protected void createRowActions() {    	
    	RowCleanAction clearAction = new RowCleanAction(model);
    	getRowActions().add(clearAction);

    }

    protected List<AbstractActionExt> getRowActions() {
        if (rowActions == null) {
            rowActions = new ArrayList<AbstractActionExt>();
        }
        return rowActions;
    }

    /**
     * Looks up and returns action keys to access actions in the 
     * table's actionMap which should be included into the popup.
     * 
     * Here: all keys with isColumnControlActionKey(key). The list 
     * is sorted by those keys.
     * 
     * @return the action keys of table's actionMap entries whose
     *   action should be included into the popup.
     */
    @SuppressWarnings("unchecked")
    protected List getColumnControlActionKeys() {
        Object[] allKeys = table.getActionMap().allKeys();
        List columnKeys = new ArrayList();
        for (int i = 0; i < allKeys.length; i++) {
            if (isColumnControlActionKey(allKeys[i])) {
                columnKeys.add(allKeys[i]);
            }
        }
        // JW: this will blow for non-String keys!
        // so this method is less decoupled from the
        // decision method isControl than expected. 
        Collections.sort(columnKeys);
        return columnKeys;
    }

    /**
     * Here: true if a String key starts with #COLUMN_CONTROL_MARKER.
     * 
     * @param actionKey a key in the table's actionMap.
     * @return a boolean to indicate whether the given actionKey maps to
     *    an action which should be included into the popup.
     *    
     */
    protected boolean isColumnControlActionKey(Object actionKey) {
        return (actionKey instanceof String) && 
            ((String) actionKey).startsWith(COLUMN_CONTROL_MARKER);
    }


    /**
     * Initialize the column control button's gui
     */
    private void init() {
        setFocusPainted(false);
        setFocusable(false);
        // this is a trick to get hold of the client prop which
        // prevents closing of the popup
        JComboBox box = new JComboBox();
        Object preventHide = box.getClientProperty("doNotCancelPopup");
        putClientProperty("doNotCancelPopup", preventHide);
    }


    /** 
     * Creates and returns the default action for this button.
     * @param icon 
     * 
     * @param icon the Icon to use in the action.
     * @return the default action.
     */
    private Action createControlAction(Icon icon) {
        
        Action control = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                togglePopup();
            }

        };
        control.putValue(Action.SMALL_ICON, icon);
        return control;
    }
    
    public class RowCleanAction extends AbstractActionExt {

		public RowCleanAction(Cleanable model) {
			setName("Çå¿Õ");
    	}
    	
		@Override
		public void actionPerformed(ActionEvent e) {
			model.clean();
		}
		
		private static final long serialVersionUID = -4884773339436048016L;
    }
}