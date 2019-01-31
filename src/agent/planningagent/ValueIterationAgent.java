package agent.planningagent;

import java.util.*;

import util.HashMapUtil;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import environnement.Action;
import environnement.Etat;
import environnement.IllegalActionException;
import environnement.MDP;
import environnement.Action2D;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration
 * et choisit ses actions selon la politique calculee.
 * @author laetitiamatignon
 *
 */
public class ValueIterationAgent extends PlanningValueAgent{
	/**
	 * discount facteur
	 */
	protected double gamma;

	/**
	 * fonction de valeur des etats
	 */
	protected Map<Etat,Double> V;


	/**
	 *
	 * @param gamma
	 * @param mdp
	 */
	public ValueIterationAgent(double gamma,  MDP mdp) {
		super(mdp);
		setGamma(gamma);

		this.V = new HashMap<>();

		for (Etat state: mdp.getEtatsAccessibles()) {
			this.V.put(state,0.0);
		}

		System.out.println("Liste des états : " + this.V);

	}


	public ValueIterationAgent(MDP mdp) {
		this(0.9,mdp);
	}

	/**
	 *
	 * Mise a jour de V: effectue UNE iteration de value iteration (calcule V_k(s) en fonction de V_{k-1}(s'))
	 * et notifie ses observateurs.
	 * Ce n'est pas la version inplace (qui utilise nouvelle valeur de V pour mettre a jour ...)
	 */
	@Override
	public void updateV(){
		//delta est utilise pour detecter la convergence de l'algorithme
		//lorsque l'on planifie jusqu'a convergence, on arrete les iterations lorsque
		//delta < epsilon
		this.delta=0.0;

		List<Etat> stateList = this.getMdp().getEtatsAccessibles();
		Map<Etat, Double> newV = new HashMap<>();

		Double maX = -Double.MAX_VALUE;

		for(Etat e : stateList)
		{
			if(!this.getMdp().estAbsorbant(e))
			{
				List<Action> actionList = this.getMdp().getActionsPossibles(e);
				double best_a = -Double.MAX_VALUE;

				for(Action a : actionList)
				{
					try
					{
						Map<Etat, Double> listTransitionProba = this.getMdp().getEtatTransitionProba(e, a);
						Double sum_inloop = 0.0;

						for(Map.Entry<Etat, Double> s : listTransitionProba.entrySet())
						{
							sum_inloop += s.getValue() * (this.getMdp().getRecompense(e, a, s.getKey()) + this.gamma * this.V.get(s.getKey()));
						}

						if(sum_inloop > best_a)
						{
							best_a = sum_inloop;
						}
					}
					catch(Exception exception)
					{
						System.out.println(exception.getMessage());
					}
				}

				newV.put(e, best_a);
			}
			else
			{
				newV.put(e, 0.0);
			}
		}

		double deltaToTest;

		for(Etat e : stateList)
		{
			deltaToTest = Math.abs(this.V.get(e) - newV.get(e));
			this.delta = this.delta < deltaToTest ? deltaToTest : this.delta;
		}

		this.V = newV;

		//returnactions.add(bestAction(bestAction));

		// mise a jour vmax et vmin pour affichage du gradient de couleur:
		//vmax est la valeur max de V pour tout s
		//vmin est la valeur min de V pour tout s
		for(Map.Entry<Etat, Double> s : this.V.entrySet())
		{
			this.vmin = this.vmin > s.getValue() ? s.getValue(): this.vmin;
			this.vmax = this.vmax < s.getValue() ? s.getValue() : this.vmax;
		}

		//******************* laisser notification a la fin de la methode
		this.notifyObs();
	}


	/**
	 * renvoi l'action executee par l'agent dans l'etat e
	 * Si aucune actions possibles, renvoi Action2D.NONE
	 */
	@Override
	public Action getAction(Etat e) {
		// doit appeler getPolitique, si 1 action, renvoyer cette action, si 0, renvoyer NONE sinon une action au hasard.
		List<Action> politics_list =  this.getPolitique(e);

		if (politics_list.isEmpty()) {
			return Action2D.NONE;
		}

		return politics_list.get(ThreadLocalRandom.current().nextInt(0, politics_list.size()));
	}

	@Override
	public double getValeur(Etat _e) {
		return this.V.getOrDefault(_e, 0.);
	}
	/**
	 * renvoi action(s) de plus forte(s) valeur(s) dans etat
	 * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
	 */
	@Override
	public List<Action> getPolitique(Etat _e) {
		Map<Action, Double> result = new HashMap<>();
		List<Action> actionList = this.getMdp().getActionsPossibles(_e);

		for(Action a : actionList)
		{
			try
			{
				Map<Etat, Double> listeTransitions = this.getMdp().getEtatTransitionProba(_e, a);
				Double sum_inloop = 0.0;

				for(Map.Entry<Etat, Double> s : listeTransitions.entrySet())
				{
					sum_inloop += s.getValue() * (this.getMdp().getRecompense(_e, a, s.getKey()) + this.gamma * this.V.get(s.getKey()));
				}

				result.put(a, sum_inloop);
			}
			catch(Exception exception)
			{
				System.out.println(exception.getMessage());
			}
		}


		List<Action> returnActions = new ArrayList<>();
		double maX = -Double.MAX_VALUE;

		for(Map.Entry<Action, Double> entry : result.entrySet())
		{
			if(entry.getValue() > maX)
			{
				maX = entry.getValue();
				returnActions.clear();
				returnActions.add(entry.getKey());
			}
			else if(entry.getValue() == maX)
			{
				returnActions.add(entry.getKey());
			}
		}

		return returnActions;

	}

	@Override
	public void reset() {
		super.reset();


		this.V.clear();
		for (Etat etat:this.mdp.getEtatsAccessibles()){
			V.put(etat, 0.0);
		}
		this.notifyObs();
	}



	public Map<Etat,Double> getV() {
		return this.V;
	}

	public double getGamma() {
		return gamma;
	}
	@Override
	public void setGamma(double _g){
		System.out.println("gamma= "+gamma);
		this.gamma = _g;
	}




}
