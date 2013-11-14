import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ItemSystem {
	private Connection connection;
	
	public ItemSystem() throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
	}
	
	public ItemSystem(String databaseFilePath) throws ClassNotFoundException, SQLException{
		this();
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
	}
	
	public int getCharacterCount() throws SQLException{
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS cnt FROM CHARACTERS");
		rs.next();
		return rs.getInt("cnt");
	}
	
	//Inserts a new Character Tuple into the Character table
	public void insertCharacter( String name, String health, String attack, 
			String defense, String specialAttack, String specialDefense) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO Characters VALUES(NULL, '" + name + "', " + health + ", " +
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
	
	//Selects all ItemIds and CharacterIds in the INVENTORY table and returns them as a string[] ArrayList
	public ArrayList<String[]> getInventories() throws SQLException{
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM INVENTORY");
				
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(int i = 0; rs.next(); i++){
			result.add(new String[2]);
			result.get(i)[0] = Integer.toString(rs.getInt("characterId"));
			result.get(i)[1] = Integer.toString(rs.getInt("itemId"));
		}
			
		return result;
	}
	
	//Selects all Characters in the CHARACTERS table and returns them as a string[] ArrayList
	public ArrayList<String[]> getAllCharacters() throws SQLException{
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM CHARACTERS");
					
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(int i = 0; rs.next(); i++){
			result.add(new String[7]);
			result.get(i)[0] = Integer.toString(rs.getInt("characterId"));
			result.get(i)[1] = rs.getString("name");
			result.get(i)[2] = Integer.toString(rs.getInt("health"));
			result.get(i)[3] = Integer.toString(rs.getInt("attack"));
			result.get(i)[4] = Integer.toString(rs.getInt("defense"));
			result.get(i)[5] = Integer.toString(rs.getInt("specialAttack"));
			result.get(i)[6] = Integer.toString(rs.getInt("specialDefense"));
		}
				
		return result;
	}
	
	//Selects all Consumables in the CONSUMABLE table and returns them as a string[] ArrayList
	public ArrayList<String[]> getAllConsumables() throws SQLException{
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM CONSUMABLE");
				
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(int i = 0; rs.next(); i++){
			result.add(new String[3]);
			result.get(i)[0] = Integer.toString(rs.getInt("itemId"));
			result.get(i)[1] = rs.getString("effectedStat");
			result.get(i)[2] = Integer.toString(rs.getInt("amount"));
		}
			
		return result;
	}
	
	//Selects all Armor in the ARMOR table and returns them as a string[] ArrayList
	public ArrayList<String[]> getAllArmor() throws SQLException{
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM ARMOR");
			
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(int i = 0; rs.next(); i++){
			result.add(new String[3]);
			result.get(i)[0] = Integer.toString(rs.getInt("itemId"));
			result.get(i)[1] = Integer.toString(rs.getInt("defense"));
			result.get(i)[2] = Integer.toString(rs.getInt("specialDefense"));
		}
		
		return result;
	}
	
	
	//Selects all Weapons in the WEAPON table and returns them as a string[] ArrayList
	public ArrayList<String[]> getAllWeapons() throws SQLException{
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM WEAPON");
		
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(int i = 0; rs.next(); i++){
			result.add(new String[3]);
			result.get(i)[0] = Integer.toString(rs.getInt("itemId"));
			result.get(i)[1] = Integer.toString(rs.getInt("attack"));
			result.get(i)[2] = Integer.toString(rs.getInt("specialAttack"));
		}
		
		return result;
	
	}
	
	//Selects all Items in the item table and returns them as a string[] ArrayList
	public ArrayList<String[]> getAllItems() throws SQLException{
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM ITEM");
	
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(int i = 0; rs.next(); i++){
			result.add(new String[3]);
			result.get(i)[0] = Integer.toString(rs.getInt("itemId"));
			result.get(i)[1] = Integer.toString(rs.getInt("price"));
			result.get(i)[2] = rs.getString("imageURL");
		}
		
		return result;
		
	}
	
	//Closes the connection to the database
	public void close() throws SQLException{
		this.connection.close();
	}
}
