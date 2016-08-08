package lib;

import java.util.List;

/**
 * Helper class for all probaboloty related methods
 */
public class ProbabilityHelper {

    // p(A or B or C) = p(A) + p(B or C) - p(A)*p(B or C)
    public static Float getCummulativeProbabilityOfIndependantEvents(List<Float> probabilities) {
        if(probabilities.size() > 2) {
            return getCummulativeProbabilityOfIndependantEvents(probabilities.get(0),
                    getCummulativeProbabilityOfIndependantEvents(probabilities.subList(1, probabilities.size())));
        } else if(probabilities.size() == 2) {
            return getCummulativeProbabilityOfIndependantEvents(probabilities.get(0), probabilities.get(1));
        } else if(probabilities.size() == 1) {
            return probabilities.get(0);
        } else {
            return 0F;
        }
    }

    //    p(A or B) = p(A) + p(B) - p(A)*p(B) when A and B are independent
    public static Float getCummulativeProbabilityOfIndependantEvents(Float a, Float b) {
        return (a + b - (a * b)) ;
    }

}
