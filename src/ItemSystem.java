import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class ItemSystem {
	private Connection connection;
	
	public ItemSystem() throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
	}
	
	public ItemSystem(String databaseFilePath) throws ClassNotFoundException, SQLException{
		this();
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
	}
	
	//Inserts a new Character Tuple into the Character table
	public void insertCharacter(int characterId, String name, int health, int attack, int defense, int specialAttack, int specialDefense) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO Character VALUES(" + 
				characterId + ", " + name + ", " + health + ", " +
				attack + ", " + defense + ", " + specialAttack + ", " + specialDefense + ")");
	}
	
	//Inserts an item into a character's inventory
	public void insertIntoInventory(int characterId, int itemId) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO Inventory VALUES(" + characterId + ", " + itemId + ")");
	}
	
	//Insert a new Armor item into the Item and Armor tables
	public void insertArmor(int itemId, int price, String imageURL, int defense, int specialDefense) throws SQLException{
		insertNewItem(itemId, price, imageURL);
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO Armor VALUES(" + 
				itemId + ", " + defense + ", " + specialDefense + ")");
	}
	
	//Inserts a new Weapon into the Item and Weapon tables
	public void insertWeapon(int itemId, int price, String imageURL, int attack, int specialAttack) throws SQLException{
		insertNewItem(itemId, price, imageURL);
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO Weapon VALUES(" + 
				itemId + ", " + attack + ", " + specialAttack + ")");
	}
	
	//Inserts a new Consumable into the Item and Consumable tables
	public void insertConsumable(int itemId, int price, String imageURL, String effectedStat, int value) throws SQLException{
		insertNewItem(itemId, price, imageURL);
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO Consumable VALUES(" + 
				itemId + ", " + effectedStat + ", " + value + ")");
	}
	
	//Inserts a new generic Item into the Item table
	public void insertNewItem(int itemId, int price, String imageURL) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO Item VALUES(" + 
				itemId + ", " + price + ", " + imageURL + ")");
	}
	
	//Selects all Items in the item table and returns them as a string
	public String getAllItems() throws SQLException{
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM Item");
		String result = "ItemId | Price | ImageURL\n";
		while(rs.next()){
			result += rs.getInt("itemId") + " | ";
			result += rs.getInt("price") + " | ";
			result += rs.getString("imageURL") + "\n";
		}
		
		return result;
		
	}
	
	//Closes the connection to the database
	public void close() throws SQLException{
		this.connection.close();
	}
	
	//Creates a new table with tableName and relevant parameters
	private void createTable(String tableName, String sqlParameters) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.setQueryTimeout(30);
		String query = "CREATE TABLE " + tableName + "(" + sqlParameters + ")";
		statement.execute(query);
	}
	
	
	
	
}
