package agent.rlapproxagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environnement.Action;
import environnement.Action2D;
import environnement.Etat;
import javafx.util.Pair;
/**
 * Vecteur de fonctions caracteristiques phi_i(s,a): autant de fonctions caracteristiques que de paire (s,a),
 * <li> pour chaque paire (s,a), un seul phi_i qui vaut 1  (vecteur avec un seul 1 et des 0 sinon).
 * <li> pas de biais ici 
 * 
 * @author laetitiamatignon
 *
 */
public class FeatureFunctionIdentity implements FeatureFunction {

	private int nbEtat;
	private int ndAction;

	public FeatureFunctionIdentity(int _nbEtat, int _nbAction){
		this.nbEtat = _nbEtat;
		this.ndAction = _nbAction;
	}
	
	@Override
	public int getFeatureNb() {
		return this.ndAction * this.nbEtat;
	}

	@Override
	public double[] getFeatures(Etat e,Action a){
		HashMap<Etat,HashMap<Action, double[]>> map;
		double[] d;
		for(int i = 0; i < this.getFeatureNb(); i++){

			d[i] = 0;

			//genenrer nb alea
			map.put() = 00
					//si = nb alea alors 1
		}
		return null;
	}
	

}
