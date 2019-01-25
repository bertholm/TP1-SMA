package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.util.Pair;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
/**
 * Renvoi 0 pour valeurs initiales de Q
 * @author laetitiamatignon
 *
 */
public class QLearningAgent extends RLAgent {
	/**
	 *  format de memorisation des Q valeurs: utiliser partout setQValeur car cette methode notifie la vue
	 */
	protected HashMap<Etat,HashMap<Action,Double>> qvaleurs;

	//AU CHOIX: vous pouvez utiliser une Map avec des Pair pour clés si vous préférez
	//protected HashMap<Pair<Etat,Action>,Double> qvaleurs;


	/**
	 *
	 * @param alpha
	 * @param gamma
	 * @param _env

	 */
	public QLearningAgent(double alpha, double gamma,Environnement _env) {
		super(alpha, gamma,_env);
		qvaleurs = new HashMap<Etat,HashMap<Action,Double>>();
	}




	/**
	 * renvoi action(s) de plus forte(s) valeur(s) dans l'etat e
	 *  (plusieurs actions sont renvoyees si valeurs identiques)
	 *  renvoi liste vide si aucunes actions possibles dans l'etat (par ex. etat absorbant)

	 */
	@Override
	public List<Action> getPolitique(Etat e) {
		// retourne action de meilleures valeurs dans _e selon Q : utiliser getQValeur()
		// retourne liste vide si aucune action legale (etat terminal)

		Double maxQValeur = 0.0;
		Double currentQValeur;

		List<Action> returnactions = new ArrayList<Action>();
		List<Action> actionsPossibles = this.getActionsLegales(e);

		if (actionsPossibles.size() == 0){
			//etat  absorbant; impossible de le verifier via environnement
			System.out.println("aucune action legale");
			return new ArrayList<Action>();
		}

		for(Action action : actionsPossibles)
		{
			currentQValeur = this.getQValeur(e, action);

			if(currentQValeur > maxQValeur)
			{
				returnactions.clear();
				returnactions.add(action);
				maxQValeur = currentQValeur;
			}
			else if(currentQValeur.equals(maxQValeur))
			{
				returnactions.add(action);
			}
		}
		return returnactions;
	}

	@Override
	public double getValeur(Etat e) {
		List<Action> actionsPossibles = this.getPolitique(e);

		if(actionsPossibles.size() > 0)
		{
			Double m = 0.0;

			for(Action actionPossible : actionsPossibles)
			{
				if(this.getQValeur(e, actionPossible) > m)
				{
					m = this.getQValeur(e, actionPossible);
				}
			}
			return m;
		}
		return 0.0;
	}

	@Override
	public double getQValeur(Etat e, Action a) {
		HashMap<Action,Double> hm = this.qvaleurs.get(e);
		if(hm == null) {
			return 0.0;
		}
		return hm.getOrDefault(a,0.0);
	}



	@Override
	public void setQValeur(Etat e, Action a, double d) {
		HashMap<Action, Double> hm = new HashMap<Action, Double>();
		hm.put(a,d);
		this.qvaleurs.put(e, hm);

		// mise a jour vmax et vmin pour affichage du gradient de couleur:
		for(HashMap.Entry<Etat,HashMap<Action,Double>> entry : this.qvaleurs.entrySet())
		{
			for(HashMap.Entry<Action,Double> entry2 : entry.getValue().entrySet())
			{
				//vmax est la valeur max de V pour tout s
				if(this.vmax < entry2.getValue())
				{
					this.vmax = entry2.getValue();
				}
				//vmin est la valeur min de V pour tout s
				if(this.vmin > entry2.getValue())
				{
					this.vmin = entry2.getValue();
				}
			}
		}
		this.notifyObs();
	}


	/**
	 * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward>
	 * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
	 * @param e
	 * @param a
	 * @param esuivant
	 * @param reward
	 */
	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {
		System.out.println("endStep");

		if (RLAgent.DISPRL)
			System.out.println("QL mise a jour etat "+e+" action "+a+" etat' "+esuivant+ " r "+reward);

		Double value = (1 - this.alpha) * this.getQValeur(e, a) + this.alpha * (reward + this.gamma * this.getValeur(esuivant));
		this.setQValeur(e, a, value);
		System.out.println(this.qvaleurs);
	}

	@Override
	public Action getAction(Etat e) {
		this.actionChoisie = this.stratExplorationCourante.getAction(e);
		return this.actionChoisie;
	}

	@Override
	public void reset() {
		super.reset();
		this.qvaleurs.clear();
		this.episodeNb = 0;
		this.notifyObs();
	}
}
