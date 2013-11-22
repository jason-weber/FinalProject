import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SystemGUI extends JFrame{
	//Constants to access table tabs
	public static final int ITEMS = 0;
	public static final int WEAPONS = 1;
	public static final int ARMOR = 2;
	public static final int CONSUMABLES = 3;
	public static final int CHARACTERS = 4;
	public static final int INVENTORY = 5;
	
	
	//Manages all database interactions
	private ItemSystem itemSystem;
	//Contains all tables as separate tabs in the tabPane
	private JTabbedPane tabPane;
	private ButtonListener buttonListener;
	private ArrayList<JTable> tables;
	
	
	public SystemGUI(String itemDatabasePath) throws ClassNotFoundException, SQLException{
		//Create new connection to database
		this.itemSystem = new ItemSystem(itemDatabasePath);
		this.setTitle("Item System Manager");
		this.setSize(800, 600);
		//Do nothing when closed so our closeListener can perform the close operations
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.buttonListener = new ButtonListener();
		
		//Setup the layout for the whole window
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		this.setLayout(layout);
		
		this.tables = new ArrayList<JTable>();
		
		//Create JTabbedPane with tables
		this.createTabbedPane();
		this.add(tabPane, BorderLayout.CENTER);
		
		this.fillTables();
		
		//Add closeListener to perform the closing operations
		this.addWindowListener(new CloseListener());
		
		
		this.add(this.createButtonPanel(), BorderLayout.SOUTH);
		this.setVisible(true);
		
	}
	
	private JPanel createButtonPanel(){
		JPanel buttonPanel = new JPanel();
		
		JButton insert = new JButton("Insert New");
		insert.setActionCommand("insert");
		insert.addActionListener(this.buttonListener);
		buttonPanel.add(insert);
		
		JButton delete = new JButton("Delete Selected Row");
		delete.setActionCommand("delete");
		delete.addActionListener(this.buttonListener);
		buttonPanel.add(delete);
		
		JButton update = new JButton("Update Selected Row");
		update.setActionCommand("update");
		update.addActionListener(this.buttonListener);
		buttonPanel.add(update);
				
		return buttonPanel;
	}
	
	private void fillTables() throws SQLException{
		//Insert all data from database into GUI tables
		this.insertMultipleRows(SystemGUI.ITEMS, this.itemSystem.getAllItems());
		this.insertMultipleRows(SystemGUI.WEAPONS, this.itemSystem.getAllWeapons());
		this.insertMultipleRows(SystemGUI.ARMOR, this.itemSystem.getAllArmor());
		this.insertMultipleRows(SystemGUI.CONSUMABLES, this.itemSystem.getAllConsumables());
		this.insertMultipleRows(SystemGUI.CHARACTERS, this.itemSystem.getAllCharacters());
		this.insertMultipleRows(SystemGUI.INVENTORY, this.itemSystem.getInventories());
	}
	
	private void createTabbedPane(){
		this.tabPane = new JTabbedPane(JTabbedPane.TOP);
		
		//Create each tab of the tabPane
		String[] itemColumns = { "ItemId", "Price", "ImageURL" };
		tabPane.addTab("Items", createTableTab(itemColumns));
				
		String[] weaponColumns = {"ItemId", "Attack", "SpecialAttack"};
		tabPane.addTab("Weapons", createTableTab(weaponColumns));
				
		String[] armorColumns = {"ItemId", "Defense", "SpecialDefense"};
		tabPane.addTab("Armor", createTableTab(armorColumns));
				
		String[] consumableColumns = {"ItemId", "Effected Stat", "Value"};
		tabPane.addTab("Consumables", createTableTab(consumableColumns));
		
		String[] characterColumns = {"CharacterId", "Name", "Health", "Attack", "Defense", "SpecialAttack", "SpecialDefense"};
		tabPane.addTab("Character", createTableTab(characterColumns));
				
		String[] inventoryColumns = {"CharacterId", "ItemId"};
		tabPane.addTab("Inventory", createTableTab(inventoryColumns));
	}
	
	public void updateSelectedRow(){
		JScrollPane scrollPane = (JScrollPane)this.tabPane.getSelectedComponent();
		JTable table = (JTable)scrollPane.getViewport().getView();
		int row = table.getSelectedRow();
		
		if(this.checkRowValidity(row)){
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			for(int i = 0; i < model.getColumnCount(); i++){
				System.out.println(model.getValueAt(row, i));
			}
		}
	}
	
	private boolean checkRowValidity(int row){
		if(row >= 0){
			return true;
		}
		else{
			JOptionPane.showMessageDialog(this, "ERROR: no row is selected");
			return false;
		}
	}
	
	private void removeFromTableWithId(int tableIndex, int id){
		DefaultTableModel itemModel = (DefaultTableModel)tables.get(tableIndex).getModel();
		for(int i = 0; i < itemModel.getRowCount(); i++){
			if(Integer.parseInt((String)itemModel.getValueAt(i, 0)) == id){
				itemModel.removeRow(i);
				return;
			}
		}
	}
	
	private void removeRowFromItems(int tableIndex, int id){
		if(tableIndex == SystemGUI.WEAPONS || 
				tableIndex == SystemGUI.CONSUMABLES || 
				tableIndex == SystemGUI.ARMOR){
			DefaultTableModel itemModel = (DefaultTableModel)tables.get(SystemGUI.ITEMS).getModel();
			for(int i = 0; i < itemModel.getRowCount(); i++){
				if(Integer.parseInt((String)itemModel.getValueAt(i, 0)) == id){
					itemModel.removeRow(i);
					return;
				}
			}
		}
	}
	
	private void deleteCharacterInventory(int characterId){
		DefaultTableModel model = (DefaultTableModel)tables.get(SystemGUI.INVENTORY).getModel();
		for(int i = 0; i < model.getRowCount(); i++){
			if(Integer.parseInt((String)model.getValueAt(i, 0)) == characterId){
				model.removeRow(i);
				i = -1;
			}
		}
	}
	
	//TODO REMOVE FROM DB
	public void deleteSelectedRow() {
		JTable table = tables.get(this.tabPane.getSelectedIndex());
		int row = table.getSelectedRow();
		row = table.convertRowIndexToModel(row);
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		if(this.checkRowValidity(row)){
			int index = this.tabPane.getSelectedIndex();
			int id = Integer.parseInt((String)model.getValueAt(row, 0));
			try{
				switch(index){
				case SystemGUI.CONSUMABLES:
					this.itemSystem.deleteItem("CONSUMABLE", id);
					break;
				case SystemGUI.WEAPONS:
					this.itemSystem.deleteItem("WEAPON", id);
					break;
				case SystemGUI.ARMOR:
					this.itemSystem.deleteItem("ARMOR", id);
					break;
				case SystemGUI.ITEMS:
					this.itemSystem.deleteItem("ITEM", id);
					this.removeFromTableWithId(SystemGUI.WEAPONS, id);
					this.removeFromTableWithId(SystemGUI.ARMOR, id);
					this.removeFromTableWithId(SystemGUI.CONSUMABLES, id);
					break;
				case SystemGUI.INVENTORY:
					int itemId = Integer.parseInt((String)model.getValueAt(row, 1));
					this.itemSystem.removeFromInventory(itemId, id);
					break;
				case SystemGUI.CHARACTERS:
					this.itemSystem.deleteCharacter(id);
					this.deleteCharacterInventory(id);
					break;
				default:
					break;
				}
				model.removeRow(row);
				removeRowFromItems(index, id);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	//inserts rows.size() number of rows into the table at tableIndex
	public void insertMultipleRows(int tableIndex, ArrayList<String[]> rows){
		for(int i = 0; i < rows.size(); i++){
			this.insertRow(tableIndex, rows.get(i));
		}
	}
	
	//Creates a table with the specified columnNames as the column headers
	//Returns a JScrollPane with the table as its only component
	private JScrollPane createTableTab(String[] columnNames){
		JTable table = new JTable(new DefaultTableModel(columnNames, 0));
		this.tables.add(table);
		return new JScrollPane(table);
	}
	
	//Inserting new row into table at tableIndex
	//ONLY USE THE STATIC CONSTANTS OF SYSTEMGUI FOR INDICES (ITEMS, WEAPONS, ARMOR, etc...)
	public void insertRow(int tableIndex, String[] row){
		JTable table = tables.get(tableIndex);
		DefaultTableModel model = (DefaultTableModel)(table.getModel());
		model.addRow(row);
	}
	
	//Just create a SystemGUI
	public static void main(String[] args){
		try {
			SystemGUI gui = new SystemGUI("res/projectDatabase.db");
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//ADD ERROR CODE
	private class ButtonListener implements ActionListener{
		private void createCharacterDialog(){
			JLabel nameL = new JLabel("Name: ");
			JLabel healthL = new JLabel("Health: ");
			JLabel attackL = new JLabel("Attack: ");
			JLabel defenseL = new JLabel("Defense: ");
			JLabel specialAttackL = new JLabel("Special Attack: ");
			JLabel specialDefenseL = new JLabel("Special Defense: ");
			
			JTextField name = new JTextField(10);
			JTextField health = new JTextField(10);
			JTextField attack = new JTextField(10);
			JTextField defense = new JTextField(10);
			JTextField specialAttack = new JTextField(10);
			JTextField specialDefense = new JTextField(10);
			
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(6, 2));
			panel.add(nameL);
			panel.add(name);
			panel.add(healthL);
			panel.add(health);
			panel.add(attackL);
			panel.add(attack);
			panel.add(defenseL);
			panel.add(defense);
			panel.add(specialAttackL);
			panel.add(specialAttack);
			panel.add(specialDefenseL);
			panel.add(specialDefense);
			
			int result = JOptionPane.showConfirmDialog(null,
					panel, "Please enter all information for the character", 
					JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.OK_OPTION){
				try {
					int id = SystemGUI.this.itemSystem.getNextId("CHARACTERS");
					String[] row = {Integer.toString(id), name.getText(), health.getText(), attack.getText(),
							defense.getText(), specialAttack.getText(), specialDefense.getText()};
					this.parseCharacterDialog(row);
					
					SystemGUI.this.itemSystem.insertCharacter(name.getText(),
							health.getText(), attack.getText(), defense.getText(),
							specialAttack.getText(), specialDefense.getText());
					
					SystemGUI.this.insertRow(SystemGUI.CHARACTERS, row);
				
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
		}
		
		private void createWeaponDialog(int id){
			JLabel attackL = new JLabel("Attack: ");
			JTextField attack = new JTextField();
			JLabel specialAttackL = new JLabel("Special Attack: ");
			JTextField specialAttack = new JTextField();
			
			JPanel panel = new JPanel(new GridLayout(2, 2));
			panel.add(attackL);
			panel.add(attack);
			panel.add(specialAttackL);
			panel.add(specialAttack);
			
			int result = JOptionPane.showConfirmDialog(null, panel, "Enter Weapon information", JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.OK_OPTION){
				try{
					String[] row = {Integer.toString(id), attack.getText(), specialAttack.getText() };
					SystemGUI.this.itemSystem.insertWeapon(id, attack.getText(), specialAttack.getText());
					SystemGUI.this.insertRow(SystemGUI.WEAPONS, row);
				} catch(SQLException e){
					e.printStackTrace();
				}
			}
		}
		
		private void createArmorDialog(int id){
			JLabel defenseL = new JLabel("Defense: ");
			JTextField defense = new JTextField();
			JLabel specialDefenseL = new JLabel("Special Defense: ");
			JTextField specialDefense = new JTextField();
			
			JPanel panel = new JPanel(new GridLayout(2, 2));
			panel.add(defenseL);
			panel.add(defense);
			panel.add(specialDefenseL);
			panel.add(specialDefense);
			
			int result = JOptionPane.showConfirmDialog(null, panel, "Enter Armor information", JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.OK_OPTION){
				try{
					String[] row = {Integer.toString(id), defense.getText(), specialDefense.getText() };
					SystemGUI.this.itemSystem.insertArmor(id, defense.getText(), specialDefense.getText());
					SystemGUI.this.insertRow(SystemGUI.ARMOR, row);
				} catch(SQLException e){
					e.printStackTrace();
				}
			}
		}
		
		private void createConsumableDialog(int id){
			JLabel effectedStatL = new JLabel("Effected Stat: ");
			String[] stats = {"Health", "Attack", "Special Attack", "Defense", "Special Defense" };
			JComboBox<String> effectedStat = new JComboBox<String>(stats);
			JLabel valueL = new JLabel("Value: ");
			JTextField value = new JTextField();
			
			JPanel panel = new JPanel(new GridLayout(2, 2));
			panel.add(effectedStatL);
			panel.add(effectedStat);
			panel.add(valueL);
			panel.add(value);
			
			int result = JOptionPane.showConfirmDialog(null, panel, "Enter Consumable information", JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.OK_OPTION){
				try{
					String[] row = {Integer.toString(id), (String)effectedStat.getSelectedItem(), value.getText() };
					SystemGUI.this.itemSystem.insertConsumable(id, (String)effectedStat.getSelectedItem(), value.getText());
					SystemGUI.this.insertRow(SystemGUI.CONSUMABLES, row);
				} catch(SQLException e){
					e.printStackTrace();
				}
			}
		}
		
		private void addToInventoryDialog(){
			JLabel itemIdL = new JLabel("Item Id: ");
			JTextField itemId = new JTextField();
			JLabel characterIdL = new JLabel("Character Id: ");
			JTextField characterId = new JTextField();
			
			JPanel panel = new JPanel(new GridLayout(2, 2));
			panel.add(itemIdL);
			panel.add(itemId);
			panel.add(characterIdL);
			panel.add(characterId);
			
			int result = JOptionPane.showConfirmDialog(null, panel, "Enter existing Ids to add to the character's inventory", JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.OK_OPTION){
				try {
					String[] row = {characterId.getText(), itemId.getText()};
					SystemGUI.this.itemSystem.insertIntoInventory(characterId.getText(), itemId.getText());
					SystemGUI.this.insertRow(SystemGUI.INVENTORY, row);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		private void createItemDialog(String defaultCheckboxVal){
			JLabel choicesL = new JLabel("Choose an item type: ");
			String[] choices = {"Basic Item", "Weapon", "Armor", "Consumable"};
			JComboBox<String> dropDown = new JComboBox<String>(choices);
			dropDown.setSelectedItem(defaultCheckboxVal);
			JLabel priceL = new JLabel("Price: ");
			JLabel imageL = new JLabel("Image URL: ");
			JTextField price = new JTextField();
			JTextField image = new JTextField();
			
			JPanel panel = new JPanel(new GridLayout(3, 2));
			panel.add(choicesL);
			panel.add(dropDown);
			panel.add(priceL);
			panel.add(price);
			panel.add(imageL);
			panel.add(image);
			
			int result = JOptionPane.showConfirmDialog(null, panel, "Enter the basic information for the item", JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.OK_OPTION){
				try{
					int id = SystemGUI.this.itemSystem.getNextId("ITEM");
					String[] row = {Integer.toString(id), price.getText(), image.getText()};
					SystemGUI.this.itemSystem.insertNewItem(price.getText(), image.getText());
					SystemGUI.this.insertRow(SystemGUI.ITEMS, row);
					
					switch((String)dropDown.getSelectedItem()){
					case "Weapon":
						this.createWeaponDialog(id);
						break;
					case "Armor":
						this.createArmorDialog(id);
						break;
					case "Consumable":
						this.createConsumableDialog(id);
						break;
					default:
						break;
					}
					
					
				}catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
		
		private boolean parseCharacterDialog(String[] characterData) throws NumberFormatException{
			for(int i = 2; i < characterData.length; i++){
				Integer.parseInt(characterData[i]);
			}
			return true;
		}

		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			switch(e.getActionCommand()){
			case "insert":
				int currentTab = SystemGUI.this.tabPane.getSelectedIndex();
				if(currentTab >= 0){
					switch(currentTab){
					case SystemGUI.WEAPONS:
						this.createItemDialog("Weapon");
						break;
					case SystemGUI.ARMOR:
						this.createItemDialog("Armor");
						break;
					case SystemGUI.CONSUMABLES:
						this.createItemDialog("Consumable");
						break;
					case SystemGUI.ITEMS:
						this.createItemDialog("Basic Item");
						break;
					case SystemGUI.CHARACTERS:
						this.createCharacterDialog();
						break;
					case SystemGUI.INVENTORY:
						this.addToInventoryDialog();
						break;
					default:
						break;
					}
				}
				break;
			case "delete":
				SystemGUI.this.deleteSelectedRow();
				break;
				
			case "update":
				SystemGUI.this.updateSelectedRow();
				break;
			default: 
				break;
			}
			
		}
		
	}
	
	//This just implements a custom function when the window closes to 
	//Close any connection to the database and cleanup anything as needed
	private class CloseListener implements WindowListener{

		
		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			try {
				SystemGUI.this.itemSystem.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				SystemGUI.this.dispose();
				System.exit(0);
			}
			
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub	
		}
	}
}
