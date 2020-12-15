package edu.iastate.cs572.proj2;

import java.util.*;

/**
 * Prover class
 *
 * @author okhobian
 */
public class Prover
{
    ArrayList<Sentence> KB; // knowledge base
    ArrayList<Sentence> PR; // sentences to be proved
    StringBuilder result;   // result string including steps

    /**
     * Constructor for class Prover
     * Initialize knowledge base list and prove list
     */
    public Prover()
    {
        KB = new ArrayList<>();
        PR = new ArrayList<>();
        result = new StringBuilder();
    }

    /**
     * Method for parsing file inputs
     * @param input string parsed by Utils.readFile()
     */
    public void parseSentences(String input)
    {
        /* split KB and PL and PR */
        String[] splitInput = input.split("#");
        String[] splitKB = splitInput[1].split("@");
        String[] splitPR = splitInput[2].split("@");

        /* add each sentence to KB or PR */
        for(String kb : splitKB) {
            if (!kb.isEmpty())  KB.add(new Sentence(kb));
        }

        for(String pr : splitPR){
            if (!pr.isEmpty())  PR.add(new Sentence(pr));
        }
    }

    /**
     * Method to resolve the PL-Logic
     */
    public String resolve()
    {
        result.append("knowledge base in clauses:\n\n");
        for(Sentence s : KB)                                // every sentence (all operators)
            for(String clause : s.getCnfClauses())          // every clause   (split by &&, only w/ ||)
                result.append(clause + "\n\n");

        for(int i=0; i<PR.size(); i++)                      // every sentence needs to prove
        {
            Sentence sentence = PR.get(i);
            result.append("****************\n");
            result.append("Goal sentence " + (i+1) + ":\n\n");
            result.append(sentence.getSentence()  + "\n");
            result.append("****************\n\n");
            result.append("Negated goal in clauses:\n\n");
            result.append(sentence.getNegatedCnf() + "\n\n");
            result.append("Proof by refutation:\n\n");

            // start PL_RESOLUTION Algorithm
            boolean pl = PL_Resolution( sentence.getNegatedCnf() );

            if(pl)
                result.append("The KB entails " + sentence.getSentence() + "\n\n");
            else
                result.append("The KB does not entail " + sentence.getSentence() + "\n\n");
        }

        return result.toString();
    }

    /**
     * Helper method to implement the PL_RESOLUTION algorithm
     * @param alpha current negated sentence in prove list
     */
    private boolean PL_Resolution(String alpha)
    {
        ArrayList<String> clauses = new ArrayList<>();
        ArrayList<String> newSet = new ArrayList<>();

        for(Sentence sentence : KB){
            clauses.addAll(sentence.getCnfClauses());
        }
        clauses.add(alpha);

        while(true)
        {
            for(int i=0; i<clauses.size(); i++)         // first clause
            {
                for(int j=0; j<clauses.size(); j++)     // second clause
                {
                    String resolvent = PL_Resolve(clauses.get(i), clauses.get(j));  // resolve each pair

                    if (resolvent.equals(Utils.LOGIC_EMPTY)) // got an empty result, return true
                    {
//                        Utils.printResolveStep(clauses.get(i), clauses.get(j), resolvent);
                        result.append(clauses.get(i)+"\n"+clauses.get(j)+"\n");
                        result.append("--------------------\n");
                        result.append(resolvent+"\n\n");
                        return true;
                    }
                    if ( ! resolvent.equals(Utils.LOGIC_NOT_EMPTY)) // resolvable clauses
                    {
                        if( !newSet.contains(resolvent) )
                        {
                            newSet.add(resolvent);
//                            Utils.printResolveStep(clauses.get(i), clauses.get(j), resolvent);
                            result.append(clauses.get(i)+"\n"+clauses.get(j)+"\n");
                            result.append("--------------------\n");
                            result.append(resolvent+"\n\n");
                        }
                    }
                }
            }
            if (clauses.containsAll(newSet))    // newSet is a subset of clauses, no new clauses added
            {
//                System.out.println("No new clauses are added.");
                result.append("No new clauses are added.\n");
                return false;
            }
            clauses = Utils.union(clauses, newSet); // add new resultants to clauses
        }
    }

    /**
     * Helper method to resolve two clauses
     * @param clause1   first clause
     * @param clause2   second clause
     * @return either resolvent or Utils.NOT_EMPTY or Utils.EMPTY
     *          resolvent: solvable between two clauses
     *          Utils.NOT_EMPTY: not solvable between two clauses
     *          Utils.EMPTY: got an empty clause
     */
    private String PL_Resolve(String clause1, String clause2)
    {
        // Split clauses into literals
        String[] literals1 = clause1.split("\\|\\|");
        String[] literals2 = clause2.split("\\|\\|");

        int len1 = literals1.length;
        int len2 = literals2.length;

        if( len1 == 1 && len2 == 1) // both with only one literal // possible to produce EMPTY
        {
            return resolveTwoLiterals(literals1[0].trim(), literals2[0].trim());
        }
        else if ( len1 == 1 && len2 > 1 )  // clause1 is single literal, clause2 is not
        {
            for(int i=0; i<len2; i++)
            {
                String result = resolveTwoLiterals(literals1[0].trim(), literals2[i].trim());
                if ( result.equals(Utils.LOGIC_EMPTY) )
                    return formResolvent(literals2, i).trim();
            }
        }
        else if ( len1 > 1 && len2 == 1 )   // clause2 is single literal, clause1 is not
        {
            for(int i=0; i<len1; i++)
            {
                String result = resolveTwoLiterals(literals1[i].trim(), literals2[0].trim());
                if ( result.equals(Utils.LOGIC_EMPTY) )
                    return formResolvent(literals1, i).trim();
            }
        }
        else if (Utils.containsSameLiterals(literals1, literals2))   // both clauses contains multiple literals
        {
            if(len1 > len2) // remove from the longer clause, clause1 has more literals
            {
                List<String> opposites = Utils.getOpposites(literals1, literals2);  // a set of opposites like P and ~P
                if (opposites.size() == 0) return Utils.LOGIC_NOT_EMPTY;            // not resolvable
                if (opposites.size() == 1)                                          // only resolve when one is opposite
                {
                    String[] resultant = Utils.removeOpposites(literals1, opposites);
                    return formResolvent(resultant, -1);
                }
            }
            else     // remove from the longer clause, clause2 has more literals
            {
                List<String> opposites = Utils.getOpposites(literals2, literals1);
                if (opposites.size() == 0) return Utils.LOGIC_NOT_EMPTY;
                if (opposites.size() == 1)
                {
                    String[] resultant = Utils.removeOpposites(literals2, opposites);
                    return formResolvent(resultant, -1);
                }
            }
        }

        return Utils.LOGIC_NOT_EMPTY;
    }

    /**
     * Helper method to resolve two literals
     * @param literal1   first literal
     * @param literal2   second literal
     * @return either Utils.NOT_EMPTY or Utils.EMPTY
     *          Utils.NOT_EMPTY: not solvable between two literals
     *          Utils.EMPTY: got opposite literals
     */
    private String resolveTwoLiterals(String literal1, String literal2)
    {
        String removeNot1 = literal1.replaceAll(Utils.LOGIC_NOT, "");
        String removeNot2 = literal2.replaceAll(Utils.LOGIC_NOT, "");

        if ( removeNot1.equals(removeNot2) && literal1.length() != literal2.length())
            return Utils.LOGIC_EMPTY;
        else
            return Utils.LOGIC_NOT_EMPTY;
    }

    /**
     * Helper method to form the resolvent clause
     * @param literals   a list of literals
     * @param index      index of literal to be removed
     * @return a string of formed clause
     */
    private String formResolvent(String[] literals, int index)
    {
        List<String> list = new ArrayList<>(Arrays.asList(literals));
        if (index!=-1) list.remove(index);
        return String.join( Utils.LOGIC_OR, list.toArray(new String[0]) );
    }

}