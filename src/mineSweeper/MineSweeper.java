package mineSweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.Timer;

public class MineSweeper {
	public static char[][] grid;
	public static int n;
	public static int mines;
	public static JButton[][] displayGrid;
	public static int safeTiles = 0;
	public static int mineCount;
	public static int timePlayed = 0;
	public static Timer timer;
	
	/**
	 * Create a minefield where it contains 'x' mines on a nxn field
	 * 
	 * Following the below example:
	 * x=4, n=5
	 * 
	 * 1221
	 * 1**1
	 * 12221
	 *  12*1
	 *  1*21
	 * 
	 * @param args
	 * 		Contains the number of mines, and the size of the grid
	 */
	public static void main(String[] args){
		//Error checking
		if(args.length != 2){
			System.out.println("Invalid number of arguments should be: "
					+ "Minesweeper n mines");
			return;
		}
		for(int i=0; i<2; i++){
			try { 
		        Integer.parseInt(args[i]); 
		    } catch(NumberFormatException e) { 
		    	System.out.println("Given arg isnt an integer");
		        return; 
		    }
		}
		mines = Integer.parseInt(args[0]);
		n = Integer.parseInt(args[1]);
		mineCount = mines;
		grid = new char[n][n];
		
		
		//Loop over the grid and randomly place the given amount of mines
		generateMinefield();
	
		//Create the display
		final JFrame frame = new JFrame();
		frame.setTitle("Minesweeper");
		frame.setLocation(100, 100);
		//Setup the option bar at the top
		JPanel statPanel = new JPanel();
		JButton newGame = new JButton("New Game");
		newGame.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newGame();
			}});
		
		final JLabel timeSpent = new JLabel("Time: " + timePlayed);
		final JLabel minesCount = new JLabel("Mines Remaining: " + mineCount);
		
		statPanel.setLayout(new GridLayout(1, 3, 100, 10));
		statPanel.add(newGame);
		statPanel.add(timeSpent);
		statPanel.add(minesCount);
		
		JPanel panel = new JPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statPanel, panel);
		splitPane.setEnabled(false);
		displayGrid = new JButton[n][n];
		for(int i=0; i<n; i++){
			for(int j=0; j<n; j++){
				//Edit the status & color of each node 
				JButton button = new JButton();
				button.setMargin(new Insets(0, 0, 0, 0));
				button.addMouseListener(new MouseAdapter(){		//Uncover the tile if left clicked
					public void mouseClicked(MouseEvent e){
						if(e.getButton() == 3){
							for(int i=0; i<n; i++){
								for(int j=0; j<n; j++){
									if(displayGrid[i][j] == e.getSource()){
										if(displayGrid[i][j].getText() == "F"){
											displayGrid[i][j].setText("");
											mineCount++;
											minesCount.setText("Mines Remaining: " + mineCount);
										}else if(displayGrid[i][j].getText() == "" && displayGrid[i][j].isEnabled()){
											displayGrid[i][j].setText("F");
											mineCount--;
											minesCount.setText("Mines Remaining: " + mineCount);
										}
									}
								}
							}
						} else {
							for(int i=0; i<n; i++){
								for(int j=0; j<n; j++){
									if(displayGrid[i][j] == e.getSource()){
										if(grid[i][j] == '*'){
											if(displayGrid[i][j].getText() != "F"){
												timer.stop();
												displayGrid[i][j].setText("*");
												displayGrid[i][j].setBackground(Color.RED);
												for(int x=0; x<n; x++){
													for(int y=0; y<n; y++){
														if(grid[x][y] == '*'){
															displayGrid[x][y].setText("*");
														}
													}
												}
												int dialogButton = JOptionPane.YES_NO_OPTION;
												int dialogResult = JOptionPane.showConfirmDialog(null, "You lost, would you like to play again?", "Game Over", dialogButton);
												if(dialogResult==0){
													generateMinefield();
													for(int x=0; x<n; x++){
														for(int y=0; y<n; y++){
															displayGrid[x][y].setText("");
															displayGrid[x][y].setBackground(null);
															displayGrid[x][y].setEnabled(true);
														}
													}
													safeTiles = 0;
												}else{
													frame.dispose();
												}
											}
										}else{
											if(safeTiles == 0){
												ActionListener timerListener = new ActionListener(){  
										            public void actionPerformed(ActionEvent e)  
										            {   
										                timePlayed++;
										                timeSpent.setText("Time: " + timePlayed);
										            }  
										        };  
										        timer = new Timer(1000, timerListener);
										        timer.setInitialDelay(0);  
										        timer.start();
											}
											//Check the adjacent tiles
											checkAdjacent(i, j, i, j);
											//Check if the game has been won
											if(safeTiles >= (n*n-mines)){
												timer.stop();
												int dialogButton = JOptionPane.YES_NO_OPTION;
												int dialogResult = JOptionPane.showConfirmDialog(null, "You won, would you like to play again?", "You WON!!!", dialogButton);
												if(dialogResult==0){
													newGame();
												}else{
													frame.dispose();
												}
											}
										}
									}
								}
							}
						}
					}
				});
				displayGrid[j][i] = button;
				panel.add(displayGrid[j][i]);
			}
		}
		panel.setLayout(new GridLayout(n, n));
		frame.add(splitPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(new Dimension(750, 750));
		frame.setVisible(true);
	}
	
	/**
	 * Create a new game for displayGird
	 */
	public static void newGame(){
		generateMinefield();
		for(int x=0; x<n; x++){
			for(int y=0; y<n; y++){
				displayGrid[x][y].setText("");
				displayGrid[x][y].setBackground(null);
				displayGrid[x][y].setEnabled(true);
			}
		}
		safeTiles = 0;
	}
	
	
	/**
	 * Generate a minefield and assign it to the global variable grid
	 */
	public static void generateMinefield(){
		int x = 0;
		for(int i=0; i<n; i++){
			for(int j=0; j<n; j++){
				grid[i][j] = ' ';
			}
		}
		while(x<mines){
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					Random rand = new Random();
					int num = rand.nextInt(100);
					if(num < 5 && x < mines){
						grid[i][j] = '*';
						x++;
					}
				}
			}
		}
	}
	
	
	/**
	 * Check each adjacent tile to determine if it contains a mine or not, if it does then add to the total count of mines
	 * If no mines are detected then continue searching through each of the adjacent tiles using recursion.
	 * TODO: Don't think that I need to have the previ and prevj anymore since checking for the enabled status
	 * 
	 * @param i
	 * @param j
	 * @param previ
	 * @param prevj
	 */
	public static void checkAdjacent(int i, int j, int previ, int prevj){
		int mines = 0;
		if(j!=0){
			if(grid[i][j-1] == '*'){		//Check north
				mines++;
			}
		}
		if(j!=0 && i!=n-1){
			if(grid[i+1][j-1] == '*'){		//Check north-east
				mines++;
			}
		}
		if(i!=n-1){
			if(grid[i+1][j] == '*'){		//Check east
				mines++;
			}
		}
		if(j!=n-1 && i!=n-1){
			if(grid[i+1][j+1] == '*'){		//Check south-east
				mines++;
			}
		}
		if(j!=n-1){
			if(grid[i][j+1] == '*'){		//Check south
				mines++;
			}
		}
		if(j!=n-1 && i!=0){
			if(grid[i-1][j+1] == '*'){		//Check south-west
				mines++;
			}
		}
		if(i!=0){
			if(grid[i-1][j] == '*'){		//Check west
				mines++;
			}
		}
		if(j!=0 && i!=0){
			if(grid[i-1][j-1] == '*'){		//Check north-west
				mines++;
			}
		}
		if(displayGrid[i][j].getText() != "F"){
			safeTiles++;
			displayGrid[i][j].setEnabled(false);
			if(mines != 0){
				displayGrid[i][j].setText("" + mines);
			}
		}
		
		//If no mines were found then continue checking adjacent mines
		if(mines == 0){
			if(j!=0 && displayGrid[i][j-1].isEnabled()){		//Continue north
				if(!(i == previ && j-1 == prevj)){
					checkAdjacent(i, j-1, i, j);
				}
			}
			if(j!=0 && i!= n-1 && displayGrid[i+1][j-1].isEnabled()){		//Continue north-east
				if(!(i+1 == previ && j-1 == prevj)){
					checkAdjacent(i+1, j-1, i, j);
				}
			}
			if(i!=n-1 && displayGrid[i+1][j].isEnabled()){		//Continue east
				if(!(i+1 == previ && j == prevj)){
					checkAdjacent(i+1, j, i, j);
				}
			}
			if(j!=n-1 && i!=n-1 && displayGrid[i+1][j+1].isEnabled()){		//Continue south-east
				if(!(i+1 == previ && j+1 == prevj)){
					checkAdjacent(i+1, j+1, i, j);
				}
			}
			if(j!=n-1 && displayGrid[i][j+1].isEnabled()){		//Continue south
				if(!(i == previ && j+1 == prevj)){
					checkAdjacent(i, j+1, i, j);
				}
			}
			if(j!=n-1 && i!=0 && displayGrid[i-1][j+1].isEnabled()){		//Continue south-west
				if(!(i-1 == previ && j+1 == prevj)){
					checkAdjacent(i-1, j+1, i, j);
				}
			}
			if(i!=0 && displayGrid[i-1][j].isEnabled()){		//Continue west
				if(!(i-1 == previ && j == prevj)){
					checkAdjacent(i-1, j, i, j);
				}
			}
			if(j!=0 && i!=0 && displayGrid[i-1][j-1].isEnabled()){		//Continue north-west
				if(!(i-1 == previ && j-1 == prevj)){
					checkAdjacent(i-1, j-1, i, j);
				}
			}
		}
	}
}
