package edu.iastate.cs572.proj2;

import java.util.ArrayList;

/**
 * CNF Object class
 * Automatically build CNF for given CNFs
 *
 * @author okhobian
 */
public class ConjunctiveNormalForm
{
    private String cnf; // CNF string

    /**
     * A constructor for class ConjunctiveNormalForm
     * This is for the leaf nodes in the expression tree (operands)
     * @param cnf1  only one cnf is parsed
     */
    public ConjunctiveNormalForm(String cnf1)
    {
        this.cnf = cnf1;
    }

    /**
     * Another constructor for class ConjunctiveNormalForm
     * This is for the internal nodes in the expression tree (operators)
     * @param cnf1  left CNF at current node
     * @param cnf2  right CNF at current node
     * @param operation current node operation
     */
    public ConjunctiveNormalForm(String cnf1, String cnf2, String operation)
    {
        if(operation.equals(Utils.LOGIC_NOT))       // unary operator NOT
            this.cnf = negation(cnf1);
        else if(cnf1.isEmpty() && !cnf2.isEmpty())  // only cnf2
            this.cnf = cnf2;
        else if (!cnf1.isEmpty() && cnf2.isEmpty()) // only cnf1
            this.cnf = cnf1;
        else                    // cnf1, cnf2, and other operators
        {                       // get cnf string from helper methods
            switch (operation)
            {
                case (Utils.LOGIC_AND):
                    this.cnf = conjunction(cnf1, cnf2);
                    break;
                case (Utils.LOGIC_OR):
                    this.cnf = disjunction(cnf1, cnf2);
                    break;
                case (Utils.LOGIC_IF):
                    this.cnf = implication(cnf1, cnf2);
                    break;
                case (Utils.LOGIC_IFF):
                    this.cnf = biconditional(cnf1, cnf2);
                    break;
                default:
                    cnf = null;
            }
        }
    }

    /**
     * Getter method for CNF
     * @return cnf field in this object
     */
    protected String getCNF()
    {
        return this.cnf;
    }

    /**
     * Helper method for conjunction
     * Simply concatenate two CNFs using AND
     * @param cnf1  left CNF
     * @param cnf2  right CNF
     * @return resultant CNF in string format
     */
    private String conjunction(String cnf1, String cnf2)
    {
        return cnf1 + Utils.LOGIC_AND + cnf2;
    }

    /**
     * Helper method for disjunction
     * Follow the rules for disjunction
     * e.g. (C1||C2) && C3 == (C1 || C3) && (C2 || C3)
     * @param cnf1  left CNF
     * @param cnf2  right CNF
     * @return resultant CNF in string format
     */
    private String disjunction(String cnf1, String cnf2)
    {
        // split each CNF into clauses
        String[] clauses1 = cnf1.split(Utils.LOGIC_AND);
        String[] clauses2 = cnf2.split(Utils.LOGIC_AND);

        int len1 = clauses1.length;
        int len2 = clauses2.length;

        StringBuilder sb = new StringBuilder();

        for(int i=0; i<len1; i++)       // for each clause in clauses1
        {
            String clause1 = clauses1[i];
            for(int j=0; j<len2; j++)   // for each clause in clauses2
            {
                sb.append(clause1);
                sb.append(Utils.LOGIC_OR);
                String clause2 = clauses2[j];
                sb.append(clause2);
                if(i==len1-1 && j==len2-1)  // no operator in the very end
                    continue;
                sb.append(Utils.LOGIC_AND);
            }
        }
        return sb.toString();
    }

    /**
     * Helper method for negation
     * Follow the rules for negation
     * @param cnf1  CNF
     * @return resultant CNF in string format
     */
    private String negation(String cnf1)
    {
        // split CNF into clauses
        String[] clauses = cnf1.split(Utils.LOGIC_AND);
        int len = clauses.length;

        if ( len == 1 && !cnf1.contains(Utils.LOGIC_OR) )    // one literal, add NOT in front
            return getNegationOfLiteral(cnf1);
        else if ( len==1 && cnf1.contains(Utils.LOGIC_OR) )  // one clause with multiple literals
        {
            String[] literals = cnf1.split("\\|\\|");
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<literals.length; i++)
            {
                sb.append(getNegationOfLiteral(literals[i]));
                if( i != literals.length-1)
                    sb.append(Utils.LOGIC_AND);
            }
            return sb.toString();
        }
        else
        {
            String[][] literals = new String[len][];
            for(int i=0; i<len; i++)
                literals[i] = clauses[i].split("\\|\\|");

//            print2D(literals);

            ArrayList<String> eachPair = new ArrayList<>();
            for(int i=0; i<len; i++)                            // each clause
            {
                for(int j=0; j<literals[i].length; j++)         // each literal in current clause
                {
                    for(int k=i+1; k<len; k++)                  // each clause after current clause
                    {
                        for(int l=0; l<literals[k].length; l++) // each literal in next clause
                        {
                            eachPair.add(getNegationOfLiteral(literals[i][j])
                                    + "||"
                                    + getNegationOfLiteral(literals[k][l]));
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for(int i=0; i<eachPair.size(); i++)
            {
                sb.append(eachPair.get(i));
                if(i != eachPair.size()-1)
                    sb.append(Utils.LOGIC_AND);
            }

            return sb.toString();
        }
    }

    /**
     * Helper method for implication
     * Follow the rules for implication
     * Basically apply negation and disjunction
     * e.g. P=>Q == ~P||Q
     * @param cnf1  left CNF
     * @param cnf2  right CNF
     * @return resultant CNF in string format
     */
    private String implication(String cnf1, String cnf2)
    {
        String cnf1_prime = negation(cnf1);
        return disjunction(cnf1_prime, cnf2);
    }

    /**
     * Helper method for bi-conditional relation
     * Follow the rules for bi-directional implication
     * Basically apply negation and disjunction to each implication
     * Then apply conjunction to concatenate
     * e.g. P<=>Q == P=>Q && Q=>P == ~P||Q && ~Q||P
     * @param cnf1  left CNF
     * @param cnf2  right CNF
     * @return resultant CNF in string format
     */
    private String biconditional(String cnf1, String cnf2)
    {
        String cnf1_prime = implication(cnf1, cnf2);
        String cnf2_prime = implication(cnf2, cnf1);
        return conjunction(cnf1_prime, cnf2_prime);
    }

    /**
     * Helper method for get negation of a single literal
     * e.g. P == ~P
     * @param literal  literal
     * @return resultant CNF in string format
     */
    private String getNegationOfLiteral(String literal)
    {
        if(literal.charAt(0)!='~')  // one positive literal
            return "~" + literal;
        else                        // one negative literal
            return literal.substring(1);
    }

}