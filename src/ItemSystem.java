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
		
	public void deleteCharacter(int characterId) throws SQLException{
		Statement statement = connection.createStatement();
		statement.execute("DELETE FROM CHARACTERS WHERE characterId = " + characterId);
		statement.execute("DELETE FROM INVENTORY WHERE characterId = " + characterId);
	}
	
	public void removeFromInventory(int itemId, int characterId) throws SQLException{
		Statement statement = connection.createStatement();
		statement.execute("DELETE FROM INVENTORY WHERE itemId = " +
				itemId + " AND characterId = " + characterId);
	}
	
	public void deleteItem(String table, int id) throws SQLException{
		Statement statement = this.connection.createStatement();
		if(!table.toUpperCase().equals("ITEM")){
			statement.execute("DELETE FROM " + table + " WHERE itemId = " + id);
		}
		else{
			statement.execute("DELETE FROM WEAPON WHERE itemId = " + id);
			statement.execute("DELETE FROM ARMOR WHERE itemId = " + id);
			statement.execute("DELETE FROM CONSUMABLE WHERE itemId = " + id);
		}
		statement.execute("DELETE FROM ITEM WHERE itemId = " + id);
	}
	
	public int getNextId(String tableName) throws SQLException{
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM SQLITE_SEQUENCE WHERE name = '" + tableName + "'");
		if(rs.next()){
			return rs.getInt("seq") + 1;
		}
		return 1;
	}
	
	//Inserts a new Character Tuple into the Character table
	public void insertCharacter( String name, String health, String attack, 
			String defense, String specialAttack, String specialDefense) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO CHARACTERS VALUES(NULL, '" + name + "', " + health + ", " +
				attack + ", " + defense + ", " + specialAttack + ", " + specialDefense + ")");
	}
	
	//Inserts an item into a character's inventory
	public void insertIntoInventory(String characterId, String itemId) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO INVENTORY VALUES(" + characterId + ", " + itemId + ")");
	}
	
	//Insert a new Armor item into the Item and Armor tables
	public void insertArmor(int itemId, String defense, String specialDefense) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO ARMOR VALUES(" + 
				itemId + ", " + defense + ", " + specialDefense + ")");
	}
	
	//Inserts a new Weapon into the Item and Weapon tables
	public void insertWeapon(int itemId, String attack, String specialAttack) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO WEAPON VALUES(" + 
				itemId + ", " + attack + ", " + specialAttack + ")");
	}
	
	//Inserts a new Consumable into the Item and Consumable tables
	public void insertConsumable(int itemId, String effectedStat, String value) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO CONSUMABLE VALUES(" + 
				itemId + ", '" + effectedStat + "', " + value + ")");
	}
	
	//Inserts a new generic Item into the Item table
	public void insertNewItem(String price, String imageURL) throws SQLException{
		Statement statement = this.connection.createStatement();
		statement.execute("INSERT INTO ITEM VALUES(NULL, " + price + ", '" + imageURL + "')");
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
