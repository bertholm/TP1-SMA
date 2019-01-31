package pacman.environnementRL;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.util.Pair;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import environnement.Etat;
/**
 * Classe pour définir un etat du MDP pour l'environnement pacman avec QLearning tabulaire

 */
public class EtatPacmanMDPClassic implements Etat , Cloneable{

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EtatPacmanMDPClassic that = (EtatPacmanMDPClassic) o;

		if (fantome != null ? !fantome.equals(that.fantome) : that.fantome != null) return false;
		if (pacman != null ? !pacman.equals(that.pacman) : that.pacman != null) return false;
		return pacdot != null ? pacdot.equals(that.pacdot) : that.pacdot == null;
	}

	@Override
	public int hashCode() {
		int result = fantome != null ? fantome.hashCode() : 0;
		result = 31 * result + (pacman != null ? pacman.hashCode() : 0);
		result = 31 * result + (pacdot != null ? pacdot.hashCode() : 0);
		return result;
	}

	private ArrayList<Pair<Integer, Integer>> fantome = new ArrayList<>();
	private Pair<Integer, Integer> pacman;
	private ArrayList<Pair<Integer, Integer>> pacdot = new ArrayList<>();

	public EtatPacmanMDPClassic(StateGamePacman _stategamepacman){
		for(int i=0; i<_stategamepacman.getNumberOfGhosts(); i++){
			this.fantome.add(new Pair<>(_stategamepacman.getGhostState(i).getX(),_stategamepacman.getGhostState(i).getY()));
		}

		for(int i=0; i<_stategamepacman.getMaze().getSizeX(); i++){
			for(int j=0; j<_stategamepacman.getMaze().getSizeY(); j++){
			if(_stategamepacman.getMaze().isFood(i,j))
				this.pacdot.add(new Pair<>(i,j));
			}
		}

		this.pacman = new Pair<>(_stategamepacman.getPacmanState(0).getX(),_stategamepacman.getPacmanState(0).getY());
	}
	
	@Override
	public String toString() {
		
		return "";
	}
	
	
	public Object clone() {
		EtatPacmanMDPClassic clone = null;
		try {
			// On recupere l'instance a renvoyer par l'appel de la 
			// methode super.clone()
			clone = (EtatPacmanMDPClassic)super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implementons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		


		// on renvoie le clone
		return clone;
	}



	

}
