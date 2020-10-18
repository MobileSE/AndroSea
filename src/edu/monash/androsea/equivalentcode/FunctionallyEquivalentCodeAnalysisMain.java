package edu.monash.androsea.equivalentcode;

import edu.monash.androsea.MevolClient;

/**
 * Github Java Repository Rank: https://github.com/search?l=Java&q=stars%3A%3E1&s=stars&type=Repositories
 *
 * For collecting functionally equivalent code
 */
public class FunctionallyEquivalentCodeAnalysisMain {
    public static void main(String[] args)
    {
        MevolClient.main(new String[] {args[0], args[1]});

        FunctionallyEquivalentCodeAnalysis fecAnalysis = new FunctionallyEquivalentCodeAnalysis();
        String content = fecAnalysis.analyse(MevolClient.methodsRepo, MevolClient.methodsRepo2, MevolClient.updatedMethods);

        if (! content.isEmpty())
            System.out.println(content);
    }
}
