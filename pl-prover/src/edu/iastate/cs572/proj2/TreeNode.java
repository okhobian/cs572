package edu.iastate.cs572.proj2;

/**
 * TreeNode Object class
 *
 * @author okhobian
 */
public class TreeNode
{
    String value;           // either operator or operand
    String cnf;             // CNF up to this node
    TreeNode left, right;   // left and right child

    /**
     * Constructor for class TreeNode
     * @param value  operator or operand at current node
     */
    TreeNode(String value)
    {
        this.value = value;
        left = right = null;
    }

    /**
     * Override toString method to get the CNF up to current node
     */
    @Override
    public String toString() { return cnf; }
}