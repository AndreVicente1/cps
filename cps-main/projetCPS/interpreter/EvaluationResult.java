package interpreter;

import java.util.ArrayList;

/**
 * Classe pour stocker les résultats qui vérifie l'évaluation lors de l'interprétation
 */
public class EvaluationResult {
    private final boolean result;
    private final ArrayList<String> sensorIds;

    public EvaluationResult(boolean result, ArrayList<String> sensorIds) {
        this.result = result;
        this.sensorIds =sensorIds;
    }
    
    public EvaluationResult(boolean result) {
    	this.result = result;
    	sensorIds = new ArrayList<>();
    }

    public boolean getResult() {
        return result;
    }

    public ArrayList<String> getSensorIds() {
        return sensorIds;
    }

    // Méthode pour combiner deux EvaluationResult
    public ArrayList<String> fusionSensorIds(EvaluationResult a) {
        sensorIds.addAll(a.getSensorIds());
        return sensorIds;
    }
}

