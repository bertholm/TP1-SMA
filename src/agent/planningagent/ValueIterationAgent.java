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
	protected HashMap<Etat,Double> V;

	/**
	 *
	 * @param gamma
	 * @param mdp
	 */
	public ValueIterationAgent(double gamma,  MDP mdp) {
		super(mdp);
		setGamma(gamma);
		//*** VOTRE CODE

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

		Map<Etat, Double> oldValues = (Map<Etat, Double>) this.V.clone();
		HashMap<Etat, Double> newValues = new HashMap<Etat,Double>();

		// retourne action de meilleure valeur dans _e selon V,
		// retourne liste vide si aucune action legale (etat absorbant)
		Double maX = -Double.MAX_VALUE;

		for (Etat _e : mdp.getEtatsAccessibles()) {
			try {
				newValues.put(_e,
						Stream.of(getAction(_e))
								.mapToDouble(a -> getValeur(_e))
								.max()
								.getAsDouble());
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.V.put(_e, maX);

		}


		super.delta = newValues.entrySet()
				.stream()
				.filter(e -> e.getValue() != null && e.getKey() != null)
				.mapToDouble(e -> Math.abs(e.getValue() - V.get(e.getKey())))
				.max()
				.getAsDouble();

		V = newValues;

		super.vmax = V.entrySet().stream().mapToDouble(e -> e.getValue()).max().getAsDouble();
		super.vmin = V.entrySet().stream().mapToDouble(e -> e.getValue()).min().getAsDouble();





		//returnactions.add(bestAction(bestAction));

		// mise a jour vmax et vmin pour affichage du gradient de couleur:
		//vmax est la valeur max de V pour tout s
		//vmin est la valeur min de V pour tout s 
		// ...

		//******************* laisser notification a la fin de la methode
		this.notifyObs();
	}


	/**
	 * renvoi l'action executee par l'agent dans l'etat e
	 * Si aucune actions possibles, renvoi Action2D.NONE
	 */
	@Override
	public Action getAction(Etat e) {
		//*** VOTRE CODE
		// doit appeler getPolitique, si 1 action, renvoyer cette action, si 0, renvoyer NONE sinon une action au hasard.
		List<Action> retourPolitique =  this.getPolitique(e);

		if (retourPolitique.isEmpty()) {
			return Action2D.NONE;
		}

		return retourPolitique.get(ThreadLocalRandom.current().nextInt(0, retourPolitique.size()));
	}

	@Override
	public double getValeur(Etat _e) {
		return this.V.get(_e);
	}
	/**
	 * renvoi action(s) de plus forte(s) valeur(s) dans etat
	 * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
	 */
	@Override
	public List<Action> getPolitique(Etat _e) {
		System.out.println("GetPolitique : " + _e);

		// retourne action de meilleure valeur dans _e selon V,
		// retourne liste vide si aucune action legale (etat absorbant)
		List<Action> returnactions = new ArrayList<Action>();
		List<Action> actionsPossibles = this.mdp.getActionsPossibles(_e);

		Double max = -Double.MAX_VALUE;
		for (Action action : actionsPossibles) {
			Double value = 0.0;
			try {
				Map<Etat, Double> map = this.mdp.getEtatTransitionProba(_e,action);
				//Pour x dans map calculer tous les V, les mettre dans une liste et ensuite ressortir le max

				for (Map.Entry<Etat, Double> mapEntry : map.entrySet()){

					Etat e = mapEntry.getKey();
					Double t = mapEntry.getValue();
					//System.out.println(e + " - " + t);

					if(this.mdp.getRecompense(e,action,_e) != 0.0)
						System.out.println(this.mdp.getRecompense(e,action,_e));
					//System.out.println(this.V.get(_e));
					value += t * (this.mdp.getRecompense(e,action,_e) + (gamma* this.V.get(e)));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}


			System.out.println(value + " - " + max);
			if(value > max) {
				max=value;
				returnactions.clear();
				returnactions.add(action);
			} else if(value.equals(max)) {
				returnactions.add(action);
			}
		}

		System.out.println(returnactions);

		return returnactions;

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





	public HashMap<Etat,Double> getV() {
		return V;
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
