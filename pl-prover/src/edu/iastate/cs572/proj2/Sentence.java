package edu.iastate.cs572.proj2;

import java.util.Arrays;
import java.util.List;

/**
 * Sentence Object class
 *
 * @author okhobian
 */
public class Sentence
{
    private String sentence;
    private String[] elements;
    private TreeNode tree;
    private String cnf;
    private String negatedCnf;

    /**
     * Constructor for class Sentence
     * Automatically builds the following fields:
     *  1) split sentence string into operands and operators
     *  2) construct expression tree
     *  3) convert sentence into CNF
     *  4) get the negation form of the sentence
     *
     * @param sentence  sentence in string format from the input file
     */
    public Sentence(String sentence)
    {
        this.sentence = sentence;
        this.elements = Utils.getElements(sentence);
        this.tree = Utils.constructExpressionTree(Utils.infixToPostfix(elements));
        this.cnf = Utils.buildCnf(tree).getCNF();
        this.negatedCnf = new ConjunctiveNormalForm(cnf, "", Utils.LOGIC_NOT).getCNF();
    }

    /**
     * Getter method for Sentence
     * @return sentence in String format
     */
    protected String getSentence() { return sentence; }

    /**
     * Getter method for Sentence elements
     * @return sentence in String[] format
     */
    protected String[] getElements(){
        return elements;
    }

    /**
     * Getter method for expression tree
     * @return root of the expression tree
     */
    protected TreeNode getExpressionTree() { return tree; }

    /**
     * Getter method for CNF of the sentence
     * @return CNF of the sentence in String format
     */
    protected String getCnf() { return cnf; }

    /**
     * Getter method for negated sentence
     * @return negated sentence in String format
     */
    protected String getNegatedCnf() { return negatedCnf; }

    /**
     * Getter method for CNF clauses
     * @return a list of clauses of the CNF
     */
    protected List<String> getCnfClauses()
    {
        String[] clauses = cnf.split(Utils.LOGIC_AND);
        List<String> list = Arrays.asList(clauses);
        return list;
    }

    @Override
    public String toString() {
        return sentence;
    }
}