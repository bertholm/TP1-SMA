package agent.strategy;

import java.util.List;
import java.util.Random;

import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Etat;
/**
 * Strategie qui renvoit un choix aleatoire avec proba epsilon, un choix glouton (suit la politique de l'agent) sinon
 * @author lmatignon
 *
 */
public class StrategyGreedy extends StrategyExploration{
	/**
	 * parametre pour probabilite d'exploration
	 */
	protected double epsilon;
	private Random rand=new Random();
	
	
	
	public StrategyGreedy(RLAgent agent,double epsilon) {
		super(agent);
		this.epsilon = epsilon;
	}

	@Override
	public Action getAction(Etat _e) {
		double d = rand.nextDouble();
		List<Action> actions;

		if (this.agent.getActionsLegales(_e).isEmpty() || this.agent.getEnv().estAbsorbant()){
			return null;
		}

		if(d < this.epsilon)
		{
			actions = this.agent.getActionsLegales(_e);
			return actions.get(rand.nextInt(actions.size()));
		}
		else
		{
			actions = this.agent.getPolitique(_e);
			if(actions.size() > 0)
				return actions.get(0);
			else return null;

		}
 	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
		System.out.println("epsilon:"+epsilon);
	}
}
