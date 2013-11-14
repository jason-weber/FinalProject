import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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
	
	
	public SystemGUI(String itemDatabasePath) throws ClassNotFoundException, SQLException{
		//Create new connection to database
		this.itemSystem = new ItemSystem(itemDatabasePath);
		this.setTitle("Item System Manager");
		this.setSize(800, 600);
		//Do nothing when closed so our closeListener can perform the close operations
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
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
		this.addWindowListener(new closeListener());
		
		JButton createCharacter = new JButton("Create new character");
		createCharacter.setActionCommand("createCharacter");
		createCharacter.addActionListener(new ButtonListener());
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

		@Override
		public void actionPerformed(ActionEvent e) {
			switch(e.getActionCommand()){
			case "createCharacter":
				JLabel nameL = new JLabel("NameL: ");
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
						SystemGUI.this.itemSystem.insertCharacter(name.getText(),
								health.getText(), attack.getText(), defense.getText(),
								specialAttack.getText(), specialDefense.getText());
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
	private class closeListener implements WindowListener{

		
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
