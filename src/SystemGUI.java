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
		
		//Create each tab of the tabPane
		this.tabPane = new JTabbedPane(JTabbedPane.TOP);
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
		
		//Add tabPane to window
		this.add(tabPane, BorderLayout.CENTER);
		
		//Insert all data from database into GUI tables
		this.insertMultipleRows(SystemGUI.ITEMS, this.itemSystem.getAllItems());
		this.insertMultipleRows(SystemGUI.WEAPONS, this.itemSystem.getAllWeapons());
		this.insertMultipleRows(SystemGUI.ARMOR, this.itemSystem.getAllArmor());
		this.insertMultipleRows(SystemGUI.CONSUMABLES, this.itemSystem.getAllConsumables());
		this.insertMultipleRows(SystemGUI.CHARACTERS, this.itemSystem.getAllCharacters());
		this.insertMultipleRows(SystemGUI.INVENTORY, this.itemSystem.getInventories());
		
		//Add closeListener to perform the closing operations
		this.addWindowListener(new CloseListener());
		
		JButton createCharacter = new JButton("Insert New");
		createCharacter.setActionCommand("insert");
		createCharacter.addActionListener(this.buttonListener);
		this.add(createCharacter, BorderLayout.SOUTH);
		this.setVisible(true);
		
	}
	
	//inserts rows.size() number of rows into the table at tableIndex
	public void insertMultipleRows(int tableIndex, ArrayList<String[]> rows){
		for(int i = 0; i < rows.size(); i++){
			this.insertRow(tableIndex, rows.get(i));
		}
	}
	
	//Creates a table with the specified columnNames as the column headers
	//Returns a JScrollPane with the table as its only component
	private static JScrollPane createTableTab(String[] columnNames){
		JTable table = new JTable(new DefaultTableModel(columnNames, 0));
		return new JScrollPane(table);
	}
	
	//Inserting new row into table at tableIndex
	//ONLY USE THE STATIC CONSTANTS OF SYSTEMGUI FOR INDICES (ITEMS, WEAPONS, ARMOR, etc...)
	public void insertRow(int tableIndex, String[] row){
		JScrollPane pane = (JScrollPane)this.tabPane.getComponentAt(tableIndex);
		JViewport view = pane.getViewport();
		JTable table = (JTable)view.getView();
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
					panel, "Please enter all information for the character: ", 
					JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.OK_OPTION){
				try {
					int id = SystemGUI.this.itemSystem.getCharacterCount() + 1;
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
		
		private void createItemDialog(){
			JLabel choicesL = new JLabel("Choose an item type: ");
			String[] choices = {"Basic Item", "Weapon", "Armor", "Consumable"};
			JComboBox dropDown = new JComboBox(choices);
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
			
			int result = JOptionPane.showConfirmDialog(null, panel, "Enter the basic information for the item: ", JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.OK_OPTION){
				try{
					int id = SystemGUI.this.itemSystem.getItemCount() + 1;
					String[] row = {Integer.toString(id), price.getText(), image.getText()};
					SystemGUI.this.itemSystem.insertNewItem(price.getText(), image.getText());
					SystemGUI.this.insertRow(SystemGUI.ITEMS, row);
					
					switch((String)dropDown.getSelectedItem()){
					case "Weapon":
						break;
					case "Armor":
						break;
					case "Consumable":
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
			for(int i = 1; i < characterData.length; i++){
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
					case SystemGUI.ITEMS:
						this.createItemDialog();
						break;
					case SystemGUI.WEAPONS:
						break;
					case SystemGUI.ARMOR:
						break;
					case SystemGUI.CONSUMABLES:
						break;
					case SystemGUI.CHARACTERS:
						this.createCharacterDialog();
						break;
					case SystemGUI.INVENTORY:
						break;
					default:
						break;
					}
					
				}
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
