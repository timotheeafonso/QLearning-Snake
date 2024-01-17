package strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import model.SnakeGame;
import utils.AgentAction;
import utils.Position;

public class ApproximateQLearning_duel extends Strategy{

    int d=5; // nombre de caracteristique
    double [] w;
    double [] current_f;
	
	

    public ApproximateQLearning_duel(int nbActions, double epsilon, double gamma, double alpha) {	
        super(nbActions, epsilon, gamma, alpha);
        this.w=new double[d+1];
		for(int i=0;i<=d;i++){
        	this.w[i]= random.nextGaussian();
		}
    }
    
	
	public double[] extractFeatures(SnakeGame state,int a, int idxSnake){	
        double[] f=new double[d+1];
		int idxSnake2=0;
		if(idxSnake==0){
			idxSnake2=1;  
		}else{
			idxSnake2=0;
		} 

		int x_snake=state.getSnakes().get(idxSnake).getPositions().get(0).getX();
		int y_snake=state.getSnakes().get(idxSnake).getPositions().get(0).getY();
		boolean limiteUp = y_snake==0;
		boolean limiteDown = y_snake==state.getSizeY();
		boolean limiteLeft = x_snake==0;
		boolean limiteRight = x_snake==state.getSizeX();
		boolean wall = state.getWalls()[0][0];

		switch(a){
			case 0: // up
				if(wall || (!wall && !limiteUp))
					y_snake-=1;
				else
					y_snake=state.getSizeY();
			break;
			case 1: // down
				if(wall || (!wall && !limiteDown))
					y_snake+=1;
				else
					y_snake=0;
			break;
			case 2: // left
				if(wall || (!wall && !limiteLeft))
					x_snake-=1;
				else
					x_snake=state.getSizeX();
			break;
			case 3: //right	
				if(wall || (!wall && !limiteRight))
					x_snake+=1;
				else
					x_snake=0;
			break;
		}

		ArrayList<Position> newPos = new ArrayList<>();
		newPos.add(new Position(x_snake, y_snake));
		int pos=0;
		for(Position p : state.getSnakes().get(idxSnake).getPositions()){
			if(pos!=state.getSnakes().get(idxSnake).getPositions().size()-1){
				newPos.add(p);
			}
			pos++;
		}


		// f0
        f[0]=1;
		// f1 distance à la pomme
		int x_pomme=state.getItems().get(0).getX();
		int y_pomme=state.getItems().get(0).getY();
		int x = Math.abs(x_snake-x_pomme);
        int y = Math.abs(y_snake-y_pomme);
        double distance = x+y;
		f[1]=distance;
		// f2 distance à son corps
		int i=0;
		double distanceCorp=3;
		for(Position p : newPos){
			if(i>2){
				int xi = Math.abs(x_snake-p.getX());
				int yi = Math.abs(y_snake-p.getY());
				if(xi+yi<3){
					distanceCorp=xi+yi;
				}
			}
			i++;
		}
		f[2]=distanceCorp;
		
		// f3 distance au mur le plus proche
		ArrayList<Double> distancesWall = new ArrayList<>();
		
		if(wall){
			double distanceWallUp = y_snake;
			double distanceWallDown = state.getSizeY()-y_snake ;
			double distanceWallLeft = x_snake;
			double distanceWallRight = state.getSizeX() - x_snake;
			distancesWall.add(distanceWallUp);
			distancesWall.add(distanceWallDown);
			distancesWall.add(distanceWallLeft);
			distancesWall.add(distanceWallRight);
			f[3]=Math.min(distanceWallUp, distanceWallDown);
			f[4]=Math.min(distanceWallLeft, distanceWallRight);

		}else{
			f[3]=1;
			f[4]=1;
		}
		// f4 distance au corps enemie le plus proche
		ArrayList<Double> distancesCorpEnemie = new ArrayList<>();
	
		for(Position p : state.getSnakes().get(idxSnake2).getPositions()){
				int xi = x_snake-p.getX();
				int yi = y_snake-p.getY();
				double distancei = Math.sqrt(xi*xi + yi*yi);
				distancesCorpEnemie.add(distancei);
		}
		if(distancesCorpEnemie.size()<1){
			f[4]=Math.max(state.getSizeX(),state.getSizeY());
		}else{
			f[4]=Collections.min(distancesCorpEnemie);
		}
		// f5 difference taille
		if(state.getSnakes().get(idxSnake).getPositions().size()>-state.getSnakes().get(idxSnake2).getPositions().size())
			f[5]=1;
		else
			f[5]=0;
        return f;
    }

	public double scalarProduct(double [] w , double [] f){
        double q=0;
        for(int i=0;i<w.length;i++){
            q+=w[i]*f[i];
        }
        return q;
    }
    
	@Override
	public synchronized AgentAction chooseAction(int idxSnake, SnakeGame state) {

        if(random.nextDouble()<this.epsilon){
			int irandom = random.nextInt(nbActions);
			current_f=extractFeatures(state, irandom, idxSnake);
            return listeAction.get(irandom);
        }else{
			HashMap<Integer,Double> res=  new HashMap<>();
			// Calcul les produits scalaires q pour chaque action
			for(int i=0;i<nbActions;i++){
            	double [] f = extractFeatures(state,i, idxSnake);
            	double q=scalarProduct(this.w, f);
				res.put(i,q); 
			}
			// Recupere la ou les actions qui ont le produit scalaire le plus grand
			double maxValue=(Collections.max(res.values()));
			ArrayList<Integer> maxKeys=new ArrayList<>();
			for (Entry<Integer, Double> entry : res.entrySet()) {
				if (entry.getValue().equals(maxValue)) {
					maxKeys.add(entry.getKey());
				}
			}
			// Si un seul action retourne l'action
			if(maxKeys.size()==1){
				current_f=extractFeatures(state, maxKeys.get(0),idxSnake);
				return listeAction.get(maxKeys.get(0));
			// sinon retourne une des actions random
			}else{
				int i = random.nextInt(maxKeys.size());
				current_f=extractFeatures(state, maxKeys.get(i),idxSnake);
				return listeAction.get(i);
			}
		}
    }
	

	@Override
	public synchronized void update(int idx, SnakeGame state, AgentAction action, SnakeGame nextState, int reward, boolean isFinalState) {

		ArrayList<Double> res=  new ArrayList<>();
		for(int i=0;i<nbActions;i++){
			double [] f = extractFeatures(nextState,i,idx);
			double q=scalarProduct(this.w, f);
			res.add(q); 
		}
		double maxValue=(Collections.max(res));		
		double target = reward + this.gamma*maxValue;
		double Qstate = scalarProduct(this.w,  this.current_f);
		
		for(int i = 0; i <= d; i++) {	
			this.w[i] = this.w[i] - 2*this.alpha*this.current_f[i]*(Qstate - target);
		}
	}

}
