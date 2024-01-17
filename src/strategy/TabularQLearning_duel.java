package strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import model.SnakeGame;
import utils.AgentAction;


public class TabularQLearning_duel extends Strategy {

	private HashMap<String,double[]> Qtab;

    public TabularQLearning_duel(int nbActions, double epsilon, double gamma, double alpha) {	
        super(nbActions, epsilon, gamma, alpha);
			Qtab = new HashMap<>();
    }

    

	@Override
	public synchronized AgentAction chooseAction(int idxSnake, SnakeGame snakeGame) {
		String state=encodeState(idxSnake,snakeGame);
		if(!Qtab.containsKey(state)){
			double vide[] = { 0, 0, 0, 0 };
			Qtab.put(state, vide);
		}
	
		if(Math.random()< epsilon) {
			return listeAction.get(random.nextInt(nbActions));
		} else {
			// recupere dans res la valeur de Qtab pour chaque action 
			HashMap<Integer,Double> res=  new HashMap<>();
			for(int i=0; i<nbActions;i++){
				double val = Qtab.get(state)[i];
				res.put(i,val);
			}
			// Recupere la ou les actions qui ont la valeur la plus grande
			double maxValue=(Collections.max(res.values()));
			ArrayList<Integer> maxKeys=new ArrayList<>();
			for (Entry<Integer, Double> entry : res.entrySet()) {
				if (entry.getValue().equals(maxValue)) {
					maxKeys.add(entry.getKey());
				}
			}
			// Si un seul action retourne l'action
			if(maxKeys.size()==1){
				return listeAction.get(maxKeys.get(0));
			// sinon retourne une des actions random
			}else{
				int i = random.nextInt(maxKeys.size());
				int k = maxKeys.get(i);
				return  listeAction.get(k);
			}
	
		}
        
    }


	@Override
	public synchronized void update(int idxSnake, SnakeGame state, AgentAction action, SnakeGame nextState, int reward, boolean isFinalState) {
		ArrayList <Double> listNextStateSc = new ArrayList<Double>();
		String stateEncode=encodeState(0,state);
		String nextStateEncode=encodeState(idxSnake,nextState);
		if(!Qtab.containsKey(nextStateEncode)){
			double vide[] = { 0, 0, 0, 0 };
			Qtab.put(nextStateEncode, vide);
		}
		for(int i=0; i<listeAction.size();i++){
			listNextStateSc.add(this.Qtab.get(nextStateEncode)[i]);
		}
		double maxQtab_next_state = Collections.max(listNextStateSc);
		this.Qtab.get(stateEncode)[listeAction.indexOf(action)] = (1 - this.alpha)*this.Qtab.get(stateEncode)[listeAction.indexOf(action)] + this.alpha*(reward + this.gamma*maxQtab_next_state);
	}
}
