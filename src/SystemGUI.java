import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

import javax.swing.*;

public class SystemGUI extends JFrame{
	private ItemSystem itemSystem;
	private TopPanel tPanel;
	private JTabbedPane tabPane;
	
	public SystemGUI(String itemDatabasePath) throws ClassNotFoundException, SQLException{
		this.itemSystem = new ItemSystem(itemDatabasePath);
		this.setTitle("Item System Manager");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		this.setLayout(layout);
		
		tPanel = new TopPanel("Current Inventory");
		this.add(tPanel, BorderLayout.NORTH);
		
		
		
		JButton btn = new JButton("Change Text");
		btn.setActionCommand("changeText");
		btn.addActionListener(new ButtonListener());
		this.add(btn, BorderLayout.SOUTH);
		
		this.addWindowListener(new closeListener());	
		this.setBackground(Color.black);
		this.setVisible(true);
		
	}
	
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
			if(e.getActionCommand().equals("changeText")){
				SystemGUI.this.tPanel.editText("Text edited");
			}
		}
		
	}
	
	private class TopPanel extends JPanel{
		private JLabel label;
		public TopPanel(String text){
			super();
			this.setBackground(Color.WHITE);
			label = new JLabel(text);
			this.setLayout(new BorderLayout());
			this.add(label, BorderLayout.CENTER);
		}
		
		public void editText(String newText){
			this.label.setText(newText);
		}
	}
	
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
