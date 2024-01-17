package strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import agent.Snake;
import model.SnakeGame;
import utils.AgentAction;
import utils.Position;


public abstract class Strategy {

	private boolean modeTrain;
	
	protected int nbActions;
	protected double epsilon;
	protected double base_epsilon;
	protected double gamma;
	protected double alpha;
	protected List<AgentAction> listeAction;
    Random random = new Random();


	public Strategy() {
	}
	
	public Strategy(int nbActions, double epsilon, double gamma, double alpha) {
		this.nbActions = nbActions;
		this.epsilon = epsilon;
		this.base_epsilon = epsilon;
		this.gamma = gamma;
		this.alpha = alpha;
		listeAction = Arrays.asList(AgentAction.values());   
		//System.out.println(listeAction);       
	}

	public abstract AgentAction chooseAction(int idxSnake, SnakeGame snakeGame);

	
	public abstract void update(int idx, SnakeGame state,  AgentAction action, SnakeGame nextState, int reward, boolean isFinalState);
	
	
	public boolean isModeTrain() {
		return modeTrain;
	}


	public void setModeTrain(boolean modeTrain) {
		
		this.modeTrain = modeTrain;
		
		if(this.modeTrain) {
			this.epsilon = this.base_epsilon;
		} else {
			this.epsilon = 0;
			
		}
	}

	public String encodeState( int idxSnake, SnakeGame snakeGame){
		String state="";
		ArrayList<Position> psnakes=snakeGame.getSnakes().get(idxSnake).getPositions();
		int ItemX=snakeGame.getItems().get(0).getX();
		int ItemY=snakeGame.getItems().get(0).getY();


		for(int x=0;x<snakeGame.getSizeX();x++){
			for(int y=0;y<snakeGame.getSizeY();y++){
				state+="V";
			}
		}

		StringBuilder stateString = new StringBuilder(state);
		int i=0;
		int index=0;
		for(Position p : psnakes){
			index=p.getY()*snakeGame.getSizeX()+p.getX();
			if(i==0){
				stateString.setCharAt(index, 'H');

			}else{
				stateString.setCharAt(index, 'B');
			}
			i++;
		}

		if(snakeGame.getSnakes().size()>1){
			int idxSnake2=0;
			if(idxSnake==0){
				idxSnake2=1;  
			}else{
				idxSnake2=0;
			}	
			boolean sup = snakeGame.getSnakes().get(idxSnake).getPositions().size()>snakeGame.getSnakes().get(idxSnake2).getPositions().size();

			ArrayList<Position> psnakes2=snakeGame.getSnakes().get(idxSnake2).getPositions();
			for(Position p : psnakes2){
				index=p.getY()*snakeGame.getSizeX()+p.getX();
				if(sup)
					stateString.setCharAt(index, 'E');
				else
					stateString.setCharAt(index, 'R');
			}
		}

		int yw=0;
		for(boolean[] w : snakeGame.getWalls()){
			int xw=0;
			for(boolean w2 : w){
				if(w2){
					index=yw*snakeGame.getSizeX()+xw;
					stateString.setCharAt(index, 'W');
				}
				xw++;
			}
			yw++;
		}
		index=ItemY*snakeGame.getSizeX()+ItemX;
		stateString.setCharAt(index, 'A');
		return stateString.toString();
	}

	public int[][] createGrid(SnakeGame state,ArrayList<Position> pos) {
		int[][] grid = new int[state.getSizeX()][state.getSizeY()];
	
		// Fill the grid with information about the walls and the snake
		for (int i = 0; i < state.getSizeX(); i++) {
			for (int j = 0; j < state.getSizeY(); j++) {
				if (state.getWalls()[i][j]) {
					// Wall
					grid[i][j] = -1;
				
				
				} else {
					// Empty space
					grid[i][j] = 0;
				}
			}
		}

		for(Position p : pos){
			//System.out.println("p: "+p.getX()+" "+p.getY());
			grid[p.getX()][p.getY()] = -1;
		}
		int ItemX=state.getItems().get(0).getX();
		int ItemY=state.getItems().get(0).getY();
		grid[ItemX][ItemY] = 0;
		return grid;
	}

	public String toStringG(int[][] grid) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				sb.append(grid[i][j] + " ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}