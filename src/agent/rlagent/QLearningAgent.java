package agent.rlagent;

import java.util.Map.Entry;
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

		Double maxQValeur = Double.NEGATIVE_INFINITY;
		double currentQValeur = 0.d;

		List<Action> returnactions = new ArrayList<Action>();
		List<Action> actionsPossibles = this.getActionsLegales(e);

		if(actionsPossibles.size() == 0){
			//etat  absorbant; impossible de le verifier via environnement
			System.out.println("aucune action legale");
			return new ArrayList<Action>();
		}
		for(Action action : actionsPossibles)
		{
			currentQValeur = this.getQValeur(e, action);

			if(currentQValeur > maxQValeur)
			{
				maxQValeur = currentQValeur;
				returnactions.clear();
				returnactions.add(action);
			}
			else if(currentQValeur == maxQValeur)
			{
				returnactions.add(action);
			}
		}

		return returnactions;
	}

	@Override
	public double getValeur(Etat e) {
		double max = 0.d;
		if(qvaleurs.get(e) != null){
			//Parcours les scores des actions futures
			for( Entry<Action, Double> scoreActionFuture : qvaleurs.get(e).entrySet() ){
				if(max < scoreActionFuture.getValue())
					max = scoreActionFuture.getValue();
			}
		}
		//retourne le max des actions possibles
		return max;
	}

	@Override
	public double getQValeur(Etat e, Action a) {
		//Si état inconnu
		if(this.qvaleurs.get(e) == null) {
			qvaleurs.put(e, new HashMap<>());
		}
		//Si l'action n'existe pas
		if(this.qvaleurs.get(e).get(a) == null){
			qvaleurs.get(e).put(a,0.d);
		}
		return this.qvaleurs.get(e).get(a);
	}

	@Override
	public void setQValeur(Etat e, Action a, double d) {
		this.qvaleurs.get(e).put(a,d);

		if(d > vmax)
			vmax = d;
		if(d < vmin)
			vmin = d;
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
		if (RLAgent.DISPRL)
			System.out.println("QL mise a jour etat "+e+" action "+a+" etat' "+esuivant+ " r "+reward);

		//L'état n'existe pas
		if(qvaleurs.get(e) == null)
			qvaleurs.put(e, new HashMap<>());
		//L'action n'existe pas
		if(qvaleurs.get(e).get(a) == null)
			qvaleurs.get(e).put(a,0.d);

		if(qvaleurs.get(esuivant) == null)
			qvaleurs.put(esuivant, new HashMap<>());
		if(qvaleurs.get(esuivant).get(a) == null)
			qvaleurs.get(esuivant).put(a,0.d);

		//Permet d'évaluer la récompense des actions possibles depuis un état donné
		Double value = ((1 - this.getAlpha())*(qvaleurs.get(e).get(a))) + this.getAlpha() * (reward + (this.getGamma()*this.getValeur(esuivant)));

		if(value > vmax)
			vmax = value;

		this.setQValeur(e, a, value);
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
