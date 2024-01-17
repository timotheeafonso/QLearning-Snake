	package strategy;

	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
	import model.SnakeGame;
	import utils.AStar;
	import utils.AgentAction;
	import utils.Node;
	import utils.Position;

	public class ApproximateQLearning_solo extends Strategy{
	
		int d=2; // nombre de caracteristique
		double [] w;
		double [] current_f;
		
		

		public ApproximateQLearning_solo(int nbActions, double epsilon, double gamma, double alpha) {	
			super(nbActions, epsilon, gamma, alpha);
			this.w=new double[d+1];
			for(int i=0;i<=d;i++){
				this.w[i]= random.nextGaussian();
			}
		}
		
		
		public double[] extractFeatures(SnakeGame state,AgentAction a, int idxSnake){	
			double[] f=new double[d+1]; 

			int x_snake=state.getSnakes().get(idxSnake).getPositions().get(0).getX();
			int y_snake=state.getSnakes().get(idxSnake).getPositions().get(0).getY();
			boolean wall = state.getWalls()[0][0];
			if(wall){
				switch(a){
					case MOVE_UP: // up
							y_snake-=1;
					break;
					case MOVE_DOWN: // down
							y_snake+=1;
					break;
					case MOVE_LEFT: // left
							x_snake-=1;
					break;
					case MOVE_RIGHT: //right	
							x_snake+=1;
					break;
				}
				if(x_snake<0)
					x_snake=0;
				if(y_snake<0)
					y_snake=0;
				if(x_snake>=state.getSizeX())
					x_snake=state.getSizeX()-1;
				if(y_snake>=state.getSizeY())
					y_snake=state.getSizeY()-1;
			}else{
				switch(a){
					case MOVE_UP: // up
						if(y_snake == 0 )
							y_snake=state.getSizeY()-1;
						else
							y_snake-=1;
					break;
					case MOVE_DOWN: // down
						if(y_snake==state.getSizeY()-1)
							y_snake=0;
						else
							y_snake+=1;
					break;
					case MOVE_LEFT: // left
						if(x_snake==0)
							x_snake=state.getSizeX()-1;
						else
							x_snake-=1;
					break;
					case MOVE_RIGHT: //right	
						if(x_snake==state.getSizeX()-1)
							x_snake=0;
						else
							x_snake+=1;
					break;
				}
			}
			//System.out.print.out.println("x: "+x_snake+" y: "+y_snake);

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
			int[][] grille = this.createGrid(state,newPos);
			AStar astar = new AStar(grille);
			if(wall){
				int x =Math.abs(x_snake-x_pomme);
				int y =Math.abs(y_snake-y_pomme);
				double distancePomme = x+y;
				f[1]=distancePomme;
			}else{
				List<Node> dd = astar.findPath(new Node(x_snake,y_snake),new Node(x_pomme,y_pomme));
				if(dd!=null){
					//System.out.println(a+" "+dd.size());
					f[1]=dd.size()-1;
					
				}else{
					//System.out.println("null");
					f[1]=state.getSizeX()+state.getSizeY();
				}
			}	
		
			// f2 distance à son corps
			
			int i=0;
			double distanceCorp=3;
			ArrayList<Integer> distCorp = new ArrayList<>();
			int distmin = state.getSizeX()+state.getSizeY();
			for(Position p : newPos){
				if(i>2){
					int xi = Math.abs(x_snake-p.getX());
					int yi = Math.abs(y_snake-p.getY());
					int d2=xi+yi;
					List<Node> dd = astar.findPath(new Node(x_snake,y_snake),new Node(p.getX(),p.getY()));
					if(dd!=null ){
						//System.out.println(a+" "+dd.size());
						distmin=dd.size();
					}else if(d2<distmin){
						distmin=d2;
					}
					/* 
					if(xi+yi<distmin){
						distmin=xi+yi;
					} */
				}
				i++;
			}
			f[2]=distmin;
			
			// f3 distance au mur le plus proche
			
			ArrayList<Double> distancesWall = new ArrayList<>();
			
			/*if(wall){
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

			}else{*/
				//f[3]=1;
				//f[4]=1;
			//}
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
			AgentAction lastA = state.getSnakes().get(idxSnake).getLastMove();
			List<AgentAction> listeActionPossible =this.listeAction;
	
			int nbActionsPossible = listeActionPossible.size();
			
			if(random.nextDouble()<this.epsilon){
				int irandom = random.nextInt(nbActionsPossible);
				//System.out.print.out.println("rand "+irandom);
				current_f=extractFeatures(state, listeActionPossible.get(irandom), idxSnake);
				return listeActionPossible.get(irandom);
			}else{
				HashMap<Integer,Double> res=  new HashMap<>();
				ArrayList<double []> resFeatures = new ArrayList<>();
				// Calcul les produits scalaires q pour chaque action
				for(int i=0;i<nbActionsPossible;i++){

					//System.out.print.out.println("iter "+i);
					double [] f = extractFeatures(state,listeActionPossible.get(i), idxSnake);
					resFeatures.add(f);
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
					current_f=resFeatures.get(maxKeys.get(0));
					//////System.out.print.out.println(listeAction.get(maxKeys.get(0)));
					return listeActionPossible.get(maxKeys.get(0));
				// sinon retourne une des actions random
				}else{
					int i = random.nextInt(maxKeys.size());
					current_f=resFeatures.get(maxKeys.get(i));
					//////System.out.print.out.println( listeAction.get(maxKeys.get(i)));
					return listeActionPossible.get(maxKeys.get(i));
				}
				
			}

		}
		
		

		@Override
		public synchronized void update(int idx, SnakeGame state, AgentAction action, SnakeGame nextState, int reward, boolean isFinalState) {
			//System.out.print.out.println("------------------------------- update");
			AgentAction lastA = state.getSnakes().get(idx).getLastMove();
			List<AgentAction> listeActionPossible = this.listeAction;

			int nbActionsPossible = listeActionPossible.size();

			ArrayList<Double> res=  new ArrayList<>();
			for(int i=0;i<nbActionsPossible;i++){
				////System.out.print.out.println("updt "+i);
				double [] f = extractFeatures(nextState,listeActionPossible.get(i),idx);
				double q=scalarProduct(this.w, f);
				res.add(q); 
			}
			double maxValue=(Collections.max(res));		
			double target = reward + this.gamma*maxValue;
			double Qstate = scalarProduct(this.w,  this.current_f);
			
			for(int i = 0; i < d+1; i++) {	
				this.w[i] = this.w[i] - 2*this.alpha*this.current_f[i]*(Qstate - target);
			}
		}

	}
